package katrinahoffert.simplebudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

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
import katrinahoffert.simplebudget.util.Functional;
import katrinahoffert.simplebudget.util.GuiUtil;

public class CalendarActivity extends AppCompatActivity {
    /**
     * The budget entries for the selected date (used so that our menu menu selection can get extra
     * info on the selected entry.
     */
    private List<BudgetEntry> selectedEntries;

    /** The month that is currently displayed. Specifically the first day of that month. */
    private CalendarDay displayedMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeCalendar();

        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialCalendarView calendar = (MaterialCalendarView) findViewById(R.id.calendarView);
                Intent intent = new Intent(CalendarActivity.this, AddEditBudgetEntryActivity.class);
                intent.putExtra("mode", AddEditBudgetEntryActivity.AddEditActivityMode.ADD);
                intent.putExtra("date", calendarDayToString(calendar.getSelectedDate()));
                CalendarActivity.this.startActivity(intent);
            }
        });

        Button statsButton = (Button) findViewById(R.id.monthStatsButton);
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialCalendarView calendar = (MaterialCalendarView) findViewById(R.id.calendarView);
                Intent intent = new Intent(CalendarActivity.this, StatsActivity.class);
                intent.putExtra("date", calendarDayToString(displayedMonth));
                CalendarActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCalendarAndEntryList();
    }

    /**
     * Initializes the calendar, attaching appropriate event handling and decorators for showing
     * what days have entries.
     */
    private void initializeCalendar() {
        final MaterialCalendarView calendar = (MaterialCalendarView) findViewById(R.id.calendarView);
        CalendarDay today = CalendarDay.from(new Date());

        updateDecoration(calendar, today.getYear(), today.getMonth());
        displayedMonth = CalendarDay.from(today.getYear(), today.getMonth(), 1);
        calendar.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                updateDecoration(calendar, date.getYear(), date.getMonth());
                displayedMonth = CalendarDay.from(date.getYear(), date.getMonth(), 1);
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
        dateLabel.setText(String.format(getResources().getString(R.string.calendar_date_header), iso8601Date));

        String[] arrayEntryStrings = new String[selectedEntries.size()];
        for (int i = 0; i < arrayEntryStrings.length; ++i) {
            arrayEntryStrings[i] = String.format("$%.2f [%s]\n", selectedEntries.get(i).amount / 100.0, selectedEntries.get(i).category);
        }

        // Handle possibility of no entries
        if (selectedEntries.isEmpty()) {
            arrayEntryStrings = new String[] { getResources().getString(R.string.calendar_no_entries_error_message) };
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
            menu.add(Menu.NONE, 0, 0, getResources().getString(R.string.calendar_entry_edit));
            menu.add(Menu.NONE, 1, 1, getResources().getString(R.string.calendar_entry_remove));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // Edit
        if(item.getItemId() == 0) {
            Intent intent = new Intent(this, AddEditBudgetEntryActivity.class);
            intent.putExtra("mode", AddEditBudgetEntryActivity.AddEditActivityMode.EDIT);
            intent.putExtra("id", selectedEntries.get(info.position)._id);
            intent.putExtra("category", selectedEntries.get(info.position).category);
            intent.putExtra("categoryId", selectedEntries.get(info.position).categoryId);
            intent.putExtra("amount", selectedEntries.get(info.position).amount);
            intent.putExtra("date", selectedEntries.get(info.position).date);
            this.startActivity(intent);
            return true;
        }
        // Delete
        else if(item.getItemId() == 1) {
            GuiUtil.displayConfirmationPrompt(
                    this,
                    getString(R.string.calendar_remove_title),
                    getString(R.string.calendar_remove_message),
                    getString(R.string.calendar_remove_confirm_button),
                    getString(R.string.generic_cancel),
                    new Functional.Action() {
                        @Override
                        public void action() {
                            BudgetEntryDbManager.deleteEntry(CalendarActivity.this, selectedEntries.get(info.position)._id);
                            updateCalendarAndEntryList();
                        }
                    }
            );
            return true;
        }

        return false;
    }

    /** Updates the calendar decorations and the entry list. Useful if the entries have been modified. */
    private void updateCalendarAndEntryList() {
        MaterialCalendarView calendar = (MaterialCalendarView) findViewById(R.id.calendarView);
        updateSelection(calendar.getSelectedDate());
        CalendarDay today = CalendarDay.from(new Date());
        updateDecoration(calendar, today.getYear(), today.getMonth());
    }
}

