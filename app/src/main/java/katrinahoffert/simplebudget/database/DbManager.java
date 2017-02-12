package katrinahoffert.simplebudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static katrinahoffert.simplebudget.database.DbContract.CategoryTable;
import static katrinahoffert.simplebudget.database.DbContract.BudgetEntryTable;

public class DbManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SimpleBudget.db";

    public DbManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BudgetEntryTable.SQL_CREATE_TABLE);

        db.execSQL(CategoryTable.SQL_CREATE_TABLE);

        // Populate with defaults
        for (String category : CategoryTable.DEFAULT_CATEGORIES) {
            ContentValues values = new ContentValues();
            values.put(CategoryTable.COLUMN_NAME_CATEGORY_NAME, category);
            db.insert(CategoryTable.TABLE_NAME, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No upgrades yet...
    }
}
