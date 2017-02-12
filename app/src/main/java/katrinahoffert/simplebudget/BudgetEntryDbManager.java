package katrinahoffert.simplebudget;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

import static katrinahoffert.simplebudget.DbContract.BudgetEntry;

public class BudgetEntryDbManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SimpleBudget.db";
    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + BudgetEntry.TABLE_NAME + " (" +
            BudgetEntry._ID + " INTEGER PRIMARY KEY," +
            BudgetEntry.COLUMN_NAME_CATEGORY_ID + " INTEGER," +
            BudgetEntry.COLUMN_NAME_AMOUNT + " INTEGER," +
            BudgetEntry.COLUMN_NAME_DATE + " TEXT)";

    public BudgetEntryDbManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Inserts an entry to the database, representing some spending (or income).
     * @param amount The amount in *cents*. Can be negative to represent income.
     * @param category The category name that this entry is related to.
     * @param date ISO-8601 date (eg, "2017-02-11") for this entry to be tied to.
     */
    public static void addEntry(Context context, int amount, String category, String date) {
        int categoryId = CategoryDbManager.getCategoryId(context, category);

        BudgetEntryDbManager dbManager = new BudgetEntryDbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BudgetEntry.COLUMN_NAME_AMOUNT, amount);
        values.put(BudgetEntry.COLUMN_NAME_CATEGORY_ID, categoryId);
        values.put(BudgetEntry.COLUMN_NAME_DATE, date);
        db.insert(BudgetEntry.TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No upgrades yet...
    }
}
