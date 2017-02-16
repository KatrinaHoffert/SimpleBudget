package katrinahoffert.simplebudget;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StatsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Figure out the start and end dates for the stats
        String startDate = getIntent().getStringExtra("date");
        String endDate = null;
        try {
            Date convertedDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(convertedDate);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            endDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        } catch (ParseException e) {
            throw new RuntimeException("The date should never be invalid");
        }

        TextView startDateInput = (TextView) findViewById(R.id.startDateInput);
        startDateInput.setText(startDate);
        TextView endDateInput = (TextView) findViewById(R.id.endDateInput);
        endDateInput.setText(endDate);
    }
}
