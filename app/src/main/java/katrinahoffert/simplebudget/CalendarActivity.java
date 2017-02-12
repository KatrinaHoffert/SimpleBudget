package katrinahoffert.simplebudget;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class CalendarActivity extends AppCompatActivity {

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
        final List<BudgetEntry> entries = BudgetEntryDbManager.getEntriesInRange(getApplicationContext(), calendarDayToString(minMonth), calendarDayToString(maxMonth));

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
                int color = ContextCompat.getColor(getApplicationContext(), R.color.colorCalendarMarker);
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
        List<BudgetEntry> selectedEntries = BudgetEntryDbManager.getEntriesInRange(getApplicationContext(), iso8601Date, iso8601Date);

        TextView dateLabel = (TextView) findViewById(R.id.dateLabel);
        dateLabel.setText(String.format(getResources().getString(R.string.entries_header), iso8601Date));

        TextView entryList = (TextView) findViewById(R.id.entryList);
        entryList.setText("");
        for (BudgetEntry entry : selectedEntries) {
            entryList.append(String.format("$%.2f [%s]\n", entry.amount / 100.0, entry.category));
        }

        // Handle possibility of no entries
        if (selectedEntries.isEmpty()) {
            entryList.setText(getResources().getString(R.string.no_entries));
        }
    }

    /** Converts a CalendarDay into an ISO 8601 date (eg, "1970-01-01"). */
    private String calendarDayToString(CalendarDay date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date.getDate());
    }
}

