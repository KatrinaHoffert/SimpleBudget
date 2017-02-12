package katrinahoffert.simplebudget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static katrinahoffert.simplebudget.DbContract.Category;

public class CategoryDbManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "SimpleBudget.db";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + Category.TABLE_NAME + " (" +
            Category._ID + " INTEGER PRIMARY KEY," +
            Category.COLUMN_NAME_CATEGORY_NAME + " TEXT)";

    private static final String[] defaultCategories = {
            "Food",
            "Housing",
            "Utilities",
            "Clothing",
            "Transportation",
            "Entertainment"
    };

    public static void addCategory(Context context, String name) {
        CategoryDbManager dbManager = new CategoryDbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Category.COLUMN_NAME_CATEGORY_NAME, name);
        db.insert(Category.TABLE_NAME, null, values);
    }

    public static List<String> getCategories(Context context) {
        CategoryDbManager dbManager = new CategoryDbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        Cursor cursor = db.query(
                Category.TABLE_NAME,
                new String[]{Category.COLUMN_NAME_CATEGORY_NAME},
                null,
                null,
                null,
                null,
                Category.COLUMN_NAME_CATEGORY_NAME + " ASC"
        );

        List<String> categories = new ArrayList<>();
        while(cursor.moveToNext()) {
            categories.add(cursor.getString(cursor.getColumnIndexOrThrow(Category.COLUMN_NAME_CATEGORY_NAME)));
        }
        cursor.close();

        return categories;
    }

    public CategoryDbManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);

        // Populate with defaults
        for (String category : defaultCategories) {
            ContentValues values = new ContentValues();
            values.put(Category.COLUMN_NAME_CATEGORY_NAME, category);
            db.insert(Category.TABLE_NAME, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No upgrades yet...
    }
}
