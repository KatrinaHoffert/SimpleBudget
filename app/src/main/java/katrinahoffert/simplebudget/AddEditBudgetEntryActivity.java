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
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import katrinahoffert.simplebudget.database.CategoryDbManager;
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

            int amount = getIntent().getIntExtra("amount", -1);
            String amountString = String.format("%d.%02d", amount / 100, amount % 100);
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

    public enum AddEditActivityMode {
        ADD,
        EDIT
    }
}
