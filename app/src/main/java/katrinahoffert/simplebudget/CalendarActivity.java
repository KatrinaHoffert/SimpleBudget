package katrinahoffert.simplebudget;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import katrinahoffert.simplebudget.database.BudgetEntryDbManager;
import katrinahoffert.simplebudget.model.BudgetEntry;

public class CalendarActivity extends AppCompatActivity {
    private List<BudgetEntry> selectedEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeCalendar();
    }

    /**
     * Initializes the calendar, attaching appropriate event handling and decorators for showing
     * what days have entries.
     */
    private void initializeCalendar() {
        final MaterialCalendarView calendar = (MaterialCalendarView) findViewById(R.id.calendarView);
        CalendarDay today = new CalendarDay(new Date());

        updateDecoration(calendar, today.getYear(), today.getMonth());
        calendar.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                updateDecoration(calendar, date.getYear(), date.getMonth());
            }
        });

        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                updateSelection(date);
            }
        });

        // Make us start with today selected (and there will always be a date selected)
        calendar.setDateSelected(today, true);
        updateSelection(today);
    }

    /**
     * Updates the decoration based on the current month that is shown. The idea is that we need to
     * fetch entries and only want to fetch those close to the displayed month. Then when the month
     * changes, we update our decorations, thus minimizing how many entries we might have to load
     * at once.
     *
     * This will reset all decorators.
     * @param calendar The calendar that we are updating decorators on.
     * @param year The year of the displayed month.
     * @param month The month of the displayed month (note: zero indiced).
     */
    private void updateDecoration(MaterialCalendarView calendar, int year, int month) {
        // We want to get a two month padding for the date range of entries to retrieve
        CalendarDay minMonth = month < 2 ? CalendarDay.from(year - 1, (month - 2) % 12, 1) : CalendarDay.from(year, month - 2, 1);
        CalendarDay maxMonth = month > 9 ? CalendarDay.from(year + 1, (month + 2) % 12, 1) : CalendarDay.from(year, month + 2, 1);
        final List<BudgetEntry> entries = BudgetEntryDbManager.getEntriesInRange(this, calendarDayToString(minMonth), calendarDayToString(maxMonth));

        // Throw these into a hash set so that decorating is fast
        final HashSet<CalendarDay> daysToDecorate = new HashSet<>();
        for(BudgetEntry entry : entries) {
            // Note that CalendarDay has zero indiced months
            String[] splitDate = entry.date.split("-");
            CalendarDay day = CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]) - 1, Integer.parseInt(splitDate[2]));
            daysToDecorate.add(day);
        }

        calendar.removeDecorators();
        calendar.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return daysToDecorate.contains(day);
            }

            @Override
            public void decorate(DayViewFacade view) {
                int color = ContextCompat.getColor(CalendarActivity.this, R.color.colorCalendarMarker);
                view.addSpan(new DotSpan(10, color));
            }
        });
    }

    /**
     * Updates the info about the selected date, which lists the entries for that day.
     * @param date The selected date.
     */
    private void updateSelection(CalendarDay date) {
        String iso8601Date = calendarDayToString(date);
        selectedEntries = BudgetEntryDbManager.getEntriesInRange(this, iso8601Date, iso8601Date);

        TextView dateLabel = (TextView) findViewById(R.id.dateLabel);
        dateLabel.setText(String.format(getResources().getString(R.string.entries_header), iso8601Date));

        String[] arrayEntryStrings = new String[selectedEntries.size()];
        for (int i = 0; i < arrayEntryStrings.length; ++i) {
            arrayEntryStrings[i] = String.format("$%.2f [%s]\n", selectedEntries.get(i).amount / 100.0, selectedEntries.get(i).category);
        }

        // Handle possibility of no entries
        if (selectedEntries.isEmpty()) {
            arrayEntryStrings = new String[] { getResources().getString(R.string.no_entries) };
        }

        NestedListView entryList = (NestedListView) findViewById(R.id.entryList);
        ArrayAdapter<BudgetEntry> adapter = new ArrayAdapter(this, R.layout.entry_list_item, arrayEntryStrings);
        entryList.setAdapter(adapter);
        registerForContextMenu(entryList);
    }

    /** Converts a CalendarDay into an ISO 8601 date (eg, "1970-01-01"). */
    private String calendarDayToString(CalendarDay date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date.getDate());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // Create the menu for long pressing an entry
        if(v.getId() == R.id.entryList) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            String title = String.format("$%.2f [%s]\n", selectedEntries.get(info.position).amount / 100.0, selectedEntries.get(info.position).category);
            menu.setHeaderTitle(title);
            menu.add(Menu.NONE, 0, 0, getResources().getString(R.string.entry_edit));
            menu.add(Menu.NONE, 1, 1, getResources().getString(R.string.entry_remove));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // Edit
        if(item.getItemId() == 0) {
            Log.d("TODO", "Editing " + selectedEntries.get(info.position)._id);
            return true;
        }
        // Delete
        else if(item.getItemId() == 1) {
            BudgetEntryDbManager.deleteEntry(CalendarActivity.this, selectedEntries.get(info.position)._id);
            MaterialCalendarView calendar = (MaterialCalendarView) findViewById(R.id.calendarView);
            updateSelection(calendar.getSelectedDate());
            return true;
        }

        return false;
    }
}

