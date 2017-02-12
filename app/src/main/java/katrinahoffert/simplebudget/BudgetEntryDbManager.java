package katrinahoffert.simplebudget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import katrinahoffert.simplebudget.model.BudgetEntry;

import static katrinahoffert.simplebudget.DbContract.BudgetEntryTable;
import static katrinahoffert.simplebudget.DbContract.CategoryTable;

public class BudgetEntryDbManager {
    /**
     * Inserts an entry to the database, representing some spending (or income).
     * @param amount The amount in *cents*. Can be negative to represent income.
     * @param category The category name that this entry is related to.
     * @param date ISO-8601 date (eg, "2017-02-11") for this entry to be tied to.
     */
    public static void addEntry(Context context, int amount, String category, String date) {
        int categoryId = CategoryDbManager.getCategoryId(context, category);

        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BudgetEntryTable.COLUMN_NAME_AMOUNT, amount);
        values.put(BudgetEntryTable.COLUMN_NAME_CATEGORY_ID, categoryId);
        values.put(BudgetEntryTable.COLUMN_NAME_DATE, date);
        db.insert(BudgetEntryTable.TABLE_NAME, null, values);
        db.close();
    }

    public static List<BudgetEntry> getEntriesInRange(Context context, String startDate, String endDate) {
        String query = "SELECT * FROM " + BudgetEntryTable.TABLE_NAME + " INNER JOIN " +
                CategoryTable.TABLE_NAME + " ON " + BudgetEntryTable.TABLE_NAME + "." +
                BudgetEntryTable.COLUMN_NAME_CATEGORY_ID + " = " + CategoryTable.TABLE_NAME +
                "." + CategoryTable._ID + " WHERE " + BudgetEntryTable.COLUMN_NAME_DATE +
                " BETWEEN ? AND ?";

        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{startDate, endDate});

        List<BudgetEntry> entries = new ArrayList<>();
        while(cursor.moveToNext()) {
            BudgetEntry entry = new BudgetEntry();
            entry.category = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_NAME_CATEGORY_NAME));
            entry.date = cursor.getString(cursor.getColumnIndexOrThrow(BudgetEntryTable.COLUMN_NAME_DATE));
            entry.amount = cursor.getInt(cursor.getColumnIndexOrThrow(BudgetEntryTable.COLUMN_NAME_AMOUNT));
            entries.add(entry);
        }
        cursor.close();
        db.close();

        return entries;
    }
}
