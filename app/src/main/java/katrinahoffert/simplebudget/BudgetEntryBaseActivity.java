package katrinahoffert.simplebudget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import katrinahoffert.simplebudget.database.CategoryDbManager;
import katrinahoffert.simplebudget.model.BudgetEntry;
import katrinahoffert.simplebudget.model.Category;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        errorShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);

        // Load the categories and if we're in edit mode, identify the index of the category
        // that should be selected.
        categories =  CategoryDbManager.getCategories(this);
        String[] categoryNames = new String[categories.size()];
        int defaultSelected = 0;
        for(int i = 0; i < categories.size(); ++i) {
            categoryNames[i] = categories.get(i).category;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySelect = (Spinner) findViewById(R.id.categorySelect);
        categorySelect.setAdapter(adapter);

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
    }

    /**
     * Parses user input from the fields into a BudgetEntry object.
     * @return A BudgetEntry object that is valid in all but its "_id" field.
     */
    public BudgetEntry parseInput() {
        int categoryId = categories.get(categorySelect.getSelectedItemPosition())._id;
        String amount = amountInput.getText().toString();

        // Detect invalid inputs. Optional negative sign, optional dollar amount, optional period,
        // and optional decimal amount. At least some number is required. If the decimal amount is
        // present, so must be the period.
        if(!amount.matches("(-?\\.\\d{1,2}|-?\\d+(\\.\\d{0,2})?)")) {
            amountInput.startAnimation(errorShakeAnim);
            return null;
        }

        // Gotta make sure we handle the cases where the dollar portion might be something like
        // "-" or "-0" (as is the case for inputs "-.50" and "-0.50").
        String[] amountSplit = amount.split("\\.");
        int dollarAmount = 0;
        int sign = amountSplit[0].charAt(0) == '-' ? -1 : 1;
        if(!amountSplit[0].equals("") && !amountSplit[0].equals("-")) {
            dollarAmount = Integer.parseInt(amountSplit[0]) * 100;
        }

        // Correct cents if it was only one digit. Eg, for the input ".5". Have to bear in mind that
        // there might not be a cents section
        int centsAmount = amountSplit.length > 1 ? Integer.parseInt(amountSplit[1]) : 0;
        if(centsAmount != 0 && amountSplit[1].length() == 1) centsAmount *= 10;
        int amountInCents = sign * (Math.abs(dollarAmount) + centsAmount);

        // TODO: Validate date

        BudgetEntry entry = new BudgetEntry();
        entry.amount = amountInCents;
        entry.category = categorySelect.getSelectedItem().toString();
        entry.categoryId = categoryId;
        entry.date = dateInput.getText().toString();
        return entry;
    }

    protected abstract void submitButtonPressed();
}
