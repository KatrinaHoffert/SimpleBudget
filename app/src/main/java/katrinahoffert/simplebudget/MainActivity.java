package katrinahoffert.simplebudget;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import katrinahoffert.simplebudget.database.BudgetEntryDbManager;
import katrinahoffert.simplebudget.model.BudgetEntry;

public class MainActivity extends BudgetEntryBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        Button calendarButton = (Button) findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, CalendarActivity.class));
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

        if (id == R.id.action_edit_categories) {
            this.startActivity(new Intent(this, CategoriesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Adds a new budget entry from the input */
    @Override
    protected void submitButtonPressed() {
        BudgetEntry entry = parseInput();
        if(entry == null) return;
        BudgetEntryDbManager.addEntry(this, entry);

        // Inform the user that the entry was added
        amountInput.setText("");
        Toast.makeText(this, getString(R.string.add_edit_add_success), Toast.LENGTH_SHORT).show();
    }
}
