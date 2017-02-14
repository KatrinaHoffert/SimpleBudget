package katrinahoffert.simplebudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import katrinahoffert.simplebudget.model.BudgetEntry;

import static katrinahoffert.simplebudget.database.DbContract.BudgetEntryTable;
import static katrinahoffert.simplebudget.database.DbContract.CategoryTable;

public class BudgetEntryDbManager {
    /**
     * Inserts an entry to the database, representing some spending (or income).
     * @param context The application context.
     * @param entry The entry to insert. The ID will be ignored.
     */
    public static void addEntry(Context context, BudgetEntry entry) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BudgetEntryTable.COLUMN_NAME_AMOUNT, entry.amount);
        values.put(BudgetEntryTable.COLUMN_NAME_CATEGORY_ID, entry.categoryId);
        values.put(BudgetEntryTable.COLUMN_NAME_DATE, entry.date);
        db.insert(BudgetEntryTable.TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Updates an entry in the database.
     * @param context The application context.
     * @param entry The entry we're updating.
     */
    public static void updateEntry(Context context, BudgetEntry entry) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BudgetEntryTable.COLUMN_NAME_AMOUNT, entry.amount);
        values.put(BudgetEntryTable.COLUMN_NAME_CATEGORY_ID, entry.categoryId);
        values.put(BudgetEntryTable.COLUMN_NAME_DATE, entry.date);

        db.update(
                BudgetEntryTable.TABLE_NAME,
                values,
                BudgetEntryTable._ID + " = ?",
                new String[] { Integer.toString(entry._id) }
        );
        db.close();
    }

    /**
     * Obtains the budget entry items for all days within the date range (inclusive).
     * @param context The application context.
     * @param startDate The lower bound of the date range.
     * @param endDate The upper bound of the date range.
     * @return BudgetEntry items within the date range.
     */
    public static List<BudgetEntry> getEntriesInRange(Context context, String startDate, String endDate) {
        String query = "SELECT b." + BudgetEntryTable._ID + ", b." + BudgetEntryTable.COLUMN_NAME_AMOUNT +
                ", b." + BudgetEntryTable.COLUMN_NAME_DATE + ", c." + CategoryTable.COLUMN_NAME_CATEGORY_NAME +
                ", b." + BudgetEntryTable.COLUMN_NAME_CATEGORY_ID + " FROM " + BudgetEntryTable.TABLE_NAME +
                " b INNER JOIN " + CategoryTable.TABLE_NAME + " c ON b." + BudgetEntryTable.COLUMN_NAME_CATEGORY_ID
                + " = c." + CategoryTable._ID + " WHERE " + BudgetEntryTable.COLUMN_NAME_DATE + " BETWEEN ? AND ?";

        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{startDate, endDate});

        List<BudgetEntry> entries = new ArrayList<>();
        while(cursor.moveToNext()) {
            BudgetEntry entry = new BudgetEntry();
            entry._id = cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.BudgetEntryTable._ID));
            entry.category = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.CategoryTable.COLUMN_NAME_CATEGORY_NAME));
            entry.categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(BudgetEntryTable.COLUMN_NAME_CATEGORY_ID));
            entry.date = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.BudgetEntryTable.COLUMN_NAME_DATE));
            entry.amount = cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.BudgetEntryTable.COLUMN_NAME_AMOUNT));
            entries.add(entry);
        }
        cursor.close();
        db.close();

        return entries;
    }

    /**
     * Deletes an entry.
     * @param context The application context.
     * @param entryId The ID of the entry to delete.
     */
    public static void deleteEntry(Context context, int entryId) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.delete(BudgetEntryTable.TABLE_NAME,
                BudgetEntryTable._ID + " = ?",
                new String[] { Integer.toString(entryId) });
    }
}
