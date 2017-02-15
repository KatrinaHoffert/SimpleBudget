package katrinahoffert.simplebudget;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import katrinahoffert.simplebudget.database.BudgetEntryDbManager;
import katrinahoffert.simplebudget.model.BudgetEntry;

public class AddEditBudgetEntryActivity extends BudgetEntryBaseActivity {
    private AddEditActivityMode mode;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_edit_budget_entry);
        super.onCreate(savedInstanceState);

        mode = (AddEditActivityMode) getIntent().getSerializableExtra("mode");
        id = getIntent().getIntExtra("id", -1);

        // Initialize fields with previous values (note that date is initialized in BudgetEntryBaseActivity)
        if(mode == AddEditActivityMode.EDIT) {
            // Get the appropriate category of the event we're editing
            int defaultSelected = 0;
            int categoryId = getIntent().getIntExtra("categoryId", -1);
            for(int i = 0; i < categories.size(); ++i) {
                if(mode == AddEditActivityMode.EDIT) {
                    if(categories.get(i)._id == categoryId) {
                        defaultSelected = i;
                    }
                }
            }

            Spinner categorySelect = (Spinner) findViewById(R.id.categorySelect);
            categorySelect.setSelection(defaultSelected);

            // Initialize the amount input
            int amount = getIntent().getIntExtra("amount", -1);
            String amountString = String.format("%d.%02d", amount / 100, Math.abs(amount % 100));
            EditText amountInput = (EditText) findViewById(R.id.amountInput);
            amountInput.setText(amountString);
        }
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

    /** Submits the input as either a new budget entry or updating the existing one, depending on mode. */
    @Override
    protected void submitButtonPressed() {
        BudgetEntry entry = parseInput();
        if(entry == null) return;

        if(mode == AddEditActivityMode.ADD) {
            BudgetEntryDbManager.addEntry(this, entry);
            Toast.makeText(this, getString(R.string.submitEntrySuccess), Toast.LENGTH_SHORT).show();
        }
        else {
            entry._id = id;
            BudgetEntryDbManager.updateEntry(this, entry);
            Toast.makeText(this, getString(R.string.editEntrySuccess), Toast.LENGTH_SHORT).show();
        }
    }

    /** The mode that this activity is in. We can either add a new budget entry or edit an existing one. */
    public enum AddEditActivityMode {
        ADD,
        EDIT
    }
}
