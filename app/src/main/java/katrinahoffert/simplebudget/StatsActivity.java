package katrinahoffert.simplebudget;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import katrinahoffert.simplebudget.database.BudgetEntryDbManager;
import katrinahoffert.simplebudget.model.BudgetEntry;
import katrinahoffert.simplebudget.util.CurrencyFormatter;
import katrinahoffert.simplebudget.util.DateUtil;

public class StatsActivity extends AppCompatActivity {
    private List<BudgetEntry> entryList;
    private List<CategorySum> categorySumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Figure out the start and end dates for the stats. The intent is given the first of the
        // month the user is viewing and by default, we will show the entire month (so gotta
        // figure out what day the end of the month is).
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
        initializeDatePicker(startDateInput, null, endDate);
        TextView endDateInput = (TextView) findViewById(R.id.endDateInput);
        endDateInput.setText(endDate);
        initializeDatePicker(endDateInput, startDate, null);

        entryList = BudgetEntryDbManager.getEntriesInRange(this, startDate, endDate);
        categorySumList = computeCategorySums();

        initializeNetBalance();
        initializeCategorySummaryTable();
        initializePieChart();
    }

    /** Computes the CategorySums from the entries inside this date range. */
    private List<CategorySum> computeCategorySums() {
        HashMap<Integer, CategorySum> categorySums = new HashMap<>();
        for(BudgetEntry entry : entryList) {
            if(!categorySums.containsKey(entry.categoryId)) {
                CategorySum sum = new CategorySum();
                sum.id = entry.categoryId;
                sum.category = entry.category;
                sum.amount = entry.amount;
                categorySums.put(sum.id, sum);
            }
            else {
                categorySums.get(entry.categoryId).amount += entry.amount;
            }
        }

        List<CategorySum> categorySumList = new ArrayList<>(categorySums.values());
        Collections.sort(categorySumList);
        return categorySumList;
    }

    /**
     * Initializes a date picker. The date pickers have constraints and once a date is picked,
     * they'll recreate all the stats.
     * @param dateInput The input that we're applying this to.
     * @param minDate An optional date minimum constraint for the date picker. If null, no constraint is applied.
     * @param maxDate An optional date maximum constraint for the date picker. If null, no constraint is applied.
     */
    private void initializeDatePicker(final TextView dateInput, final String minDate, final String maxDate) {
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                dateInput.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));

                // Reapply event handlers
                TextView startDateInput = (TextView) findViewById(R.id.startDateInput);
                TextView endDateInput = (TextView) findViewById(R.id.endDateInput);
                String startDate = startDateInput.getText().toString();
                String endDate = endDateInput.getText().toString();
                initializeDatePicker(startDateInput, null, endDate);
                initializeDatePicker(endDateInput, startDate, null);

                // Recreate everything that depends on the date ranges
                entryList = BudgetEntryDbManager.getEntriesInRange(StatsActivity.this, startDate, endDate);
                categorySumList = computeCategorySums();

                initializeNetBalance();
                initializeCategorySummaryTable();
                initializePieChart();
            }
        };

        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize date picker to the currently selected date and show it
                String selectedDate = dateInput.getText().toString();
                String[] splitDate = selectedDate.split("-");
                DatePickerDialog dialog = new DatePickerDialog(StatsActivity.this, date, Integer.parseInt(splitDate[0]),
                        Integer.parseInt(splitDate[1]) - 1, Integer.parseInt(splitDate[2]));

                if (minDate != null) dialog.getDatePicker().setMinDate(DateUtil.iso8601StringToDate(minDate).getTime());

                // Max date is exclusive, so add a day to it
                long millisecondsInADay = 1000 * 60 * 60 * 24;
                if (maxDate != null) dialog.getDatePicker().setMaxDate(DateUtil.iso8601StringToDate(maxDate).getTime());

                dialog.show();
            }
        });
    }

    private void initializeNetBalance() {
        TextView balanceText = (TextView) findViewById(R.id.balanceText);

        if(categorySumList.isEmpty()) {
            balanceText.setText(R.string.stats_no_entries_error_message);
        }
        else {
            int balance = 0;
            for(CategorySum sum : categorySumList)  balance += sum.amount;
            String balanceFormatted = CurrencyFormatter.format(this, balance);
            balanceText.setText(String.format(getString(R.string.stats_balance_label), balanceFormatted));
        }
    }

    /** Creates the category summary table with values from the category sums. */
    private void initializeCategorySummaryTable() {
        TableLayout table = (TableLayout) findViewById(R.id.categoryTable);
        table.removeAllViews();

        if(categorySumList.isEmpty()) return;

        // Create the header
        TableRow header = new TableRow(this);
        TextView categoryHeader = new TextView(this);
        categoryHeader.setText(R.string.stats_header_category);
        categoryHeader.setTypeface(null, Typeface.BOLD);
        header.addView(categoryHeader);
        TextView spentHeader = new TextView(this);
        spentHeader.setText(R.string.stats_header_spending);
        spentHeader.setTypeface(null, Typeface.BOLD);
        header.addView(spentHeader);
        table.addView(header, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

        for(CategorySum sum : categorySumList) {
            TableRow row = new TableRow(this);
            TextView category = new TextView(this);
            category.setText(sum.category);
            row.addView(category);
            TextView spent = new TextView(this);
            spent.setText(CurrencyFormatter.format(this, sum.amount));
            row.addView(spent);
            table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    /** Initializes the pie chart with data to display. */
    private void initializePieChart() {
        PieChart chart = (PieChart) findViewById(R.id.categoryPieChart);
        if(categorySumList.isEmpty()) chart.setVisibility(View.GONE);
        else chart.setVisibility(View.VISIBLE);

        List<PieEntry> pieChartEntries = new ArrayList<>();
        for(CategorySum sum : categorySumList) {
            if(sum.amount <= 0) continue;
            pieChartEntries.add(new PieEntry(sum.amount / 100f, sum.category));
        }

        // Create unique colors that span the HSV spectrum
        int[] colors = new int[pieChartEntries.size()];
        float baseHue = 262f;
        float baseSaturation = 0.4f;
        float baseValue = 1.0f;
        float step = 360f / colors.length;

        for(int i = 0; i < colors.length; ++i) {
            float newHue = (baseHue + step * i) % 360;
            colors[i] = Color.HSVToColor(new float[] { newHue, baseSaturation, baseValue });
        }

        PieDataSet dataSet = new PieDataSet(pieChartEntries, "");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(0); // Hide the numbers
        chart.setData(data);

        Description description = new Description();
        description.setEnabled(false);
        chart.setDescription(description);
        chart.setEntryLabelColor(Color.BLACK);
        chart.getLegend().setWordWrapEnabled(true);
        chart.animateX(1000);
        chart.invalidate();
    }

    /** Represents the sum of spending inside a category */
    private class CategorySum implements Comparable<CategorySum> {
        /** The category ID */
        int id;

        /** Category name */
        String category;

        /** Amount in cents */
        int amount;

        @Override
        public int compareTo(CategorySum another) {
            // Descending order
            return another.amount - amount;
        }
    }
}
