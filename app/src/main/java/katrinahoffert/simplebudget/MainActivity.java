package katrinahoffert.simplebudget;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import katrinahoffert.simplebudget.database.BudgetEntryDbManager;
import katrinahoffert.simplebudget.database.CategoryDbManager;
import katrinahoffert.simplebudget.model.BudgetEntry;

public class MainActivity extends BudgetEntryBaseActivity {
    /**
     * A timestamp for when this app was last active. Not necessarily up to date, since we only
     * check for this when the app resumes (and hence it's set on pause). Lets us determine how
     * long it's been since the app was last seen.
     */
    private long lastActiveTime = System.currentTimeMillis();

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
    protected void onResume() {
        super.onResume();

        // Reload categories in case those were changed
        categories =  CategoryDbManager.getCategories(this);
        initializeCategories();

        // If the app hasn't been seen for more than an hour, reset the date to the current. This
        // helps avoid the case of the app being minimized for many days and then when it's brought
        // up, the date picker is still on whatever day we started the app on. We don't want to always
        // update it to the current, though, because that would end up reseting the date every
        // time the user flips away for a second!
        long hourInMs = 1000 * 60 * 60;
        if(lastActiveTime + hourInMs < System.currentTimeMillis()) {
            dateInput = (EditText) findViewById(R.id.dateInput);
            dateInput.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        lastActiveTime = System.currentTimeMillis();
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
        if (id == R.id.action_settings) {
            this.startActivity(new Intent(this, SettingsActivity.class));
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
