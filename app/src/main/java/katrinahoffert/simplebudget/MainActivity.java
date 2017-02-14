package katrinahoffert.simplebudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import katrinahoffert.simplebudget.database.BudgetEntryDbManager;
import katrinahoffert.simplebudget.database.CategoryDbManager;
import katrinahoffert.simplebudget.model.BudgetEntry;
import katrinahoffert.simplebudget.model.Category;

public class MainActivity extends AppCompatActivity {
    private Animation errorShakeAnim;
    private List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        errorShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);

        initCategories();

        EditText dateInput = (EditText) findViewById(R.id.dateInput);
        dateInput.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addBudgetEntry();
            }
        });

        Button calendarButton = (Button) findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showCalendar();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initCategories() {
        categories =  CategoryDbManager.getCategories(this);
        String[] categoryNames = new String[categories.size()];
        for(int i = 0; i < categories.size(); ++i) {
            categoryNames[i] = categories.get(i).category;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner categorySelect = (Spinner) findViewById(R.id.categorySelect);
        categorySelect.setAdapter(adapter);
    }

    /** Adds a new budget entry from the input */
    private void addBudgetEntry() {
        Spinner categorySelect = (Spinner) findViewById(R.id.categorySelect);
        EditText amountInput = (EditText) findViewById(R.id.amountInput);
        EditText dateInput = (EditText) findViewById(R.id.dateInput);

        BudgetEntry entry = parseInput(errorShakeAnim, categories, categorySelect, amountInput, dateInput);
        if(entry == null) return;
        BudgetEntryDbManager.addEntry(this, entry);

        // Inform the user that the entry was added
        amountInput.setText("");
        Toast.makeText(this, getString(R.string.submitEntrySuccess), Toast.LENGTH_SHORT).show();
    }

    /**
     * Parses input into a BudgetEntry object. This somewhat awkward method is used so that code can
     * be shared with AddEditBudgetEntryActivity, which needs to parse the same info in a consistent
     * way.
     * @param errorAnimation The animation to apply to the amountInput if it's invalid.
     * @param categories The list of all categories that was used to populat the category select. Its
     *                   indices should match that of the select.
     * @param categorySelect The select that the user uses to select categories.
     * @param amountInput The input used for the amount.
     * @param dateInput The input used for the date.
     * @return A BudgetEntry object that is valid in all but its "_id" field.
     */
    public static BudgetEntry parseInput(Animation errorAnimation, List<Category> categories, Spinner categorySelect, EditText amountInput, EditText dateInput) {
        int categoryId = categories.get(categorySelect.getSelectedItemPosition())._id;
        String amount = amountInput.getText().toString();

        // Detect invalid inputs. Optional negative sign, optional dollar amount, optional period,
        // and optional decimal amount. At least some number is required. If the decimal amount is
        // present, so must be the period.
        if(!amount.matches("(-?\\.\\d{1,2}|-?\\d+(\\.\\d{0,2})?)")) {
            amountInput.startAnimation(errorAnimation);
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

    private void showCalendar() {
        MainActivity.this.startActivity(new Intent(MainActivity.this, CalendarActivity.class));
    }
}
