package katrinahoffert.simplebudget;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddToBudgetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_budget);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List<String> categories =  CategoryDbManager.getCategories(getApplicationContext());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner categorySelect = (Spinner) findViewById(R.id.categorySelect);
        categorySelect.setAdapter(adapter);

        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String category = categorySelect.getSelectedItem().toString();

                EditText amountInput = (EditText) findViewById(R.id.amountInput);
                String amount = amountInput.getText().toString();
                String[] amountSplit = amount.split("\\.");
                int amountInCents = amountSplit.length > 1 ? Integer.parseInt(amountSplit[0]) * 100 + Integer.parseInt(amountSplit[1]) :
                        Integer.parseInt(amountSplit[0]) * 100;

                String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                BudgetEntryDbManager.addEntry(getApplicationContext(), amountInCents, category, currentDate);

                // Inform the user that the entry was added
                amountInput.setText("");
                Toast.makeText(getApplicationContext(), getString(R.string.submitEntrySuccess), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_to_budget, menu);
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
}
