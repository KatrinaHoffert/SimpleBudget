package katrinahoffert.simplebudget;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

public class AddEditBudgetEntryActivity extends AppCompatActivity {
    private Animation errorShakeAnim;
    private List<Category> categories;

    private AddEditActivityMode mode;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_budget_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mode = (AddEditActivityMode) getIntent().getSerializableExtra("mode");
        id = getIntent().getIntExtra("id", -1);

        errorShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);

        // Load the categories and if we're in edit mode, identify the index of the category
        // that should be selected.
        categories =  CategoryDbManager.getCategories(this);
        String[] categoryNames = new String[categories.size()];
        int defaultSelected = 0;
        for(int i = 0; i < categories.size(); ++i) {
            categoryNames[i] = categories.get(i).category;

            // Get the appropriate category of the event we're editing
            if(mode == AddEditActivityMode.EDIT) {
                if(categories.get(i)._id == getIntent().getIntExtra("categoryId", -1)) {
                    defaultSelected = i;
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner categorySelect = (Spinner) findViewById(R.id.categorySelect);
        categorySelect.setAdapter(adapter);

        if(mode == AddEditActivityMode.EDIT) {
            categorySelect.setSelection(defaultSelected);

            // Initialize the amount input
            int amount = getIntent().getIntExtra("amount", -1);
            String amountString = String.format("%d.%02d", amount / 100, Math.abs(amount % 100));
            EditText amountInput = (EditText) findViewById(R.id.amountInput);
            amountInput.setText(amountString);
        }

        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEditBudgetEntry();
            }
        });
    }

    /** Submits the input as either a new budget entry or updating the existing one, depending on mode. */
    private void addEditBudgetEntry() {
        // Build up the budget entry to add/edit
        Spinner categorySelect = (Spinner) findViewById(R.id.categorySelect);
        EditText amountInput = (EditText) findViewById(R.id.amountInput);
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if(mode == AddEditActivityMode.EDIT) dateString = getIntent().getStringExtra("date");
        BudgetEntry entry = MainActivity.parseInput(errorShakeAnim, categories, categorySelect, amountInput, dateString);
        if(entry == null) return;
        entry._id = id;

        BudgetEntryDbManager.updateEntry(this, entry._id, entry.amount, entry.category_id, entry.date);

        // Inform the user that the entry was added
        Toast.makeText(this, getString(R.string.editEntrySuccess), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Required for the up button to actually do anything.
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** The mode that this activity is in. We can either add a new budget entry or edit an existing one. */
    public enum AddEditActivityMode {
        ADD,
        EDIT
    }
}
