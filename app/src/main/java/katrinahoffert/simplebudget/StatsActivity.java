package katrinahoffert.simplebudget;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import katrinahoffert.simplebudget.database.BudgetEntryDbManager;
import katrinahoffert.simplebudget.model.BudgetEntry;

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
        TextView endDateInput = (TextView) findViewById(R.id.endDateInput);
        endDateInput.setText(endDate);

        entryList = BudgetEntryDbManager.getEntriesInRange(this, startDate, endDate);
        categorySumList = computeCategorySums();

        populateCategorySummaryTable();
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

    /** Populates the category summary table with values from the category sums. */
    private void populateCategorySummaryTable() {
        TableLayout table = (TableLayout) findViewById(R.id.categoryTable);

        // Create the header
        TableRow header = new TableRow(this);
        TextView categoryHeader = new TextView(this);
        categoryHeader.setText(R.string.statsHeaderCategory);
        categoryHeader.setTypeface(null, Typeface.BOLD);
        header.addView(categoryHeader);
        TextView spentHeader = new TextView(this);
        spentHeader.setText(R.string.statsHeaderSpendings);
        spentHeader.setTypeface(null, Typeface.BOLD);
        header.addView(spentHeader);
        table.addView(header, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

        for(CategorySum sum : categorySumList) {
            TableRow row = new TableRow(this);
            TextView category = new TextView(this);
            category.setText(sum.category);
            row.addView(category);
            TextView spent = new TextView(this);
            spent.setText(String.format("$%.2f", sum.amount / 100.0));
            row.addView(spent);
            table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
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