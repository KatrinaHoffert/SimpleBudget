package katrinahoffert.simplebudget;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import java.io.File;
import java.io.IOException;

/**
 * A simple settings activity containing a fragment with all our settings.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener preferenceSummaryBinder = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                if(index >= 0) preference.setSummary(listPreference.getEntries()[index]);
            }
            else {
                // For all other preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }

            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #preferenceSummaryBinder
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(preferenceSummaryBinder);

        // Trigger the listener immediately with the preference's
        // current value.
        preferenceSummaryBinder.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new AllPreferencesFragment())
                .commit();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName) || AllPreferencesFragment.class.getName().equals(fragmentName);
    }

    public static class AllPreferencesFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            bindPreferenceSummaryToValue(findPreference("starting_day_of_the_week"));
            bindPreferenceSummaryToValue(findPreference("currency_symbol"));
            bindPreferenceSummaryToValue(findPreference("currency_symbol_placement"));
            bindPreferenceSummaryToValue(findPreference("decimal_separator"));

            // Make sure that the export option actually exports. There's no obvious way to save
            // a file, so we'll throw it in downloads and pass it to the ACTION_SEND intent, which
            // can let other applications handle it (eg, upload to dropbox).
            Preference exportPreference = findPreference("export");
            exportPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        downloadsFolder.mkdirs(); // ensure it exists

                        String filePath = downloadsFolder + "/SimpleBudgetExported.csv";
                        new Exporter().exportToTabDelimitedFile(getActivity().getApplicationContext(), filePath);

                        // Make it public
                        File exportedFile = new File(filePath);
                        exportedFile.setReadable(true, false);

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(exportedFile));
                        sendIntent.setType("*/*");
                        startActivity(Intent.createChooser(sendIntent, getString(R.string.pref_export_action_send)));
                    } catch (IOException e) {
                        Log.e("SimpleBudget", "Couldn't export database to CSV", e);
                    }
                    return true;
                }
            });
        }
    }
}
