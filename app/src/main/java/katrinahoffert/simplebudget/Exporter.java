package katrinahoffert.simplebudget;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import katrinahoffert.simplebudget.database.BudgetEntryDbManager;
import katrinahoffert.simplebudget.model.BudgetEntry;
import katrinahoffert.simplebudget.util.CurrencyFormatter;

/**
 * Contains functionality for exporting content.
 */
public class Exporter {
    /**
     * Exports the entries to a tab delimited file.
     * @param context The application context.
     * @param filePath Path for the exported file.
     * @throws IOException Thrown if the file can't be written (eg, insufficient permission).
     */
    public void exportToTabDelimitedFile(Context context, String filePath) throws IOException {
        List<BudgetEntry> entries = BudgetEntryDbManager.getEntriesInRange(context, "1970-01-01", "2999-01-01");

        FileWriter fileWriter = new FileWriter(filePath);
        for(BudgetEntry entry : entries) {
            String formattedAmount = CurrencyFormatter.format(context, entry.amount);
            fileWriter.write(entry.date + "\t" + entry.category.replace('\t', ' ') + "\t" + formattedAmount + "\n");
        }
        fileWriter.close();
    }
}
