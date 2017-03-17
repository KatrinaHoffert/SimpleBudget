package katrinahoffert.simplebudget;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import katrinahoffert.simplebudget.database.CategoryDbManager;
import katrinahoffert.simplebudget.model.BudgetEntry;
import katrinahoffert.simplebudget.model.Category;
import katrinahoffert.simplebudget.util.CurrencyFormatter;

public abstract class BudgetEntryBaseActivity extends AppCompatActivity {
    protected Animation errorShakeAnim;
    protected List<Category> categories;

    protected EditText dateInput;
    protected Spinner categorySelect;
    protected EditText amountInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        errorShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);

        categories =  CategoryDbManager.getCategories(this);
        initializeCategories();

        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButtonPressed();
            }
        });

        String date = getIntent().getStringExtra("date");
        if(date == null) date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        dateInput = (EditText) findViewById(R.id.dateInput);
        dateInput.setText(date);

        amountInput = (EditText) findViewById(R.id.amountInput);

        initializeDatePicker();
    }

    /** Creates the category spinner from the categories list. */
    protected void initializeCategories() {
        String[] categoryNames = new String[categories.size()];
        for(int i = 0; i < categories.size(); ++i) {
            categoryNames[i] = categories.get(i).category;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySelect = (Spinner) findViewById(R.id.categorySelect);
        categorySelect.setAdapter(adapter);
    }

    /**
     * Parses user input from the fields into a BudgetEntry object.
     * @return A BudgetEntry object that is valid in all but its "_id" field.
     */
    protected BudgetEntry parseInput() {
        int categoryId = categories.get(categorySelect.getSelectedItemPosition())._id;
        String amount = amountInput.getText().toString();

        try {
            int amountInCents = CurrencyFormatter.parse(this, amount);

            BudgetEntry entry = new BudgetEntry();
            entry.amount = amountInCents;
            entry.category = categorySelect.getSelectedItem().toString();
            entry.categoryId = categoryId;
            entry.date = dateInput.getText().toString();
            return entry;
        }
        catch (NumberFormatException e) {
            amountInput.startAnimation(errorShakeAnim);
            return null;
        }
    }

    /** Initializes the date picker to show when the dateInput is selected. */
    private void initializeDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                dateInput.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
            }
        };

        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize date picker to the currently selected date and show it
                String selectedDate = dateInput.getText().toString();
                String[] splitDate = selectedDate.split("-");
                new DatePickerDialog(BudgetEntryBaseActivity.this, date, Integer.parseInt(splitDate[0]),
                        Integer.parseInt(splitDate[1]) - 1, Integer.parseInt(splitDate[2])).show();
            }
        });
    }

    protected abstract void submitButtonPressed();
}
