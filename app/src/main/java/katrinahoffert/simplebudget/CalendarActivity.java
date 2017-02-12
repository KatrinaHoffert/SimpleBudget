package katrinahoffert.simplebudget;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Date;
import java.util.HashMap;
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

    private void initializeCalendar() {
        MaterialCalendarView calender = (MaterialCalendarView) findViewById(R.id.calendarView);
        calender.setDateSelected(new CalendarDay(new Date()), true);

        List<BudgetEntry> entries = BudgetEntryDbManager.getEntriesInRange(getApplicationContext(), "2017-01-01", "2017-12-31");
        final HashSet<CalendarDay> daysToDecorate = new HashSet<>();
        for(BudgetEntry entry : entries) {
            // Note that CalendarDay has zero indiced months
            String[] splitDate = entry.date.split("-");
            CalendarDay day = CalendarDay.from(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]) - 1, Integer.parseInt(splitDate[2]));
            daysToDecorate.add(day);
        }

        calender.addDecorator(new DayViewDecorator() {
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

        TextView entryList = (TextView) findViewById(R.id.entryList);
        entryList.setText("arsef\nsdfsdf\narsef\n" +
                "sdfsdf\narsef\n" +
                "sdfsdf\narsef\n" +
                "sdfsdf\n");
    }
}

