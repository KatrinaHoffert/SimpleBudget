package katrinahoffert.simplebudget;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No upgrades yet...
    }
}
