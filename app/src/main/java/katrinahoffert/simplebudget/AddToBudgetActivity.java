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

public class AddToBudgetActivity extends AppCompatActivity {
    private Animation errorShakeAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_budget);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        errorShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);

        List<String> categories =  CategoryDbManager.getCategories(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner categorySelect = (Spinner) findViewById(R.id.categorySelect);
        categorySelect.setAdapter(adapter);

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

    private void addBudgetEntry() {
        Spinner categorySelect = (Spinner) findViewById(R.id.categorySelect);
        String category = categorySelect.getSelectedItem().toString();

        EditText amountInput = (EditText) findViewById(R.id.amountInput);
        String amount = amountInput.getText().toString();

        // Detect invalid inputs
        if(!amount.matches("-?\\d*\\.\\d{0,2}")) {
            amountInput.startAnimation(errorShakeAnim);
            return;
        }

        String[] amountSplit = amount.split("\\.");
        int dollarAmount = !amountSplit[0].equals("") ? Integer.parseInt(amountSplit[0]) * 100 : 0;
        int sign = dollarAmount < 0 ? -1 : 1;
        int centsAmount = amountSplit.length > 1 ? Integer.parseInt(amountSplit[1]) : 0;
        // Correct if cents was only one digit
        if(centsAmount != 0 && amountSplit[1].length() == 1) centsAmount *= 10;
        int amountInCents = sign * (Math.abs(dollarAmount) + centsAmount);

        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        BudgetEntryDbManager.addEntry(this, amountInCents, category, currentDate);

        // Inform the user that the entry was added
        amountInput.setText("");
        Toast.makeText(this, getString(R.string.submitEntrySuccess), Toast.LENGTH_SHORT).show();
    }

    private void showCalendar() {
        AddToBudgetActivity.this.startActivity(new Intent(AddToBudgetActivity.this, CalendarActivity.class));
    }
}
