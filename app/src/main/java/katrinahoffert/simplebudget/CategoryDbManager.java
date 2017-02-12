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

    /** The default categories that are inserted in the DB on first run. */
    private static final String[] defaultCategories = {
            "Food",
            "Housing",
            "Utilities",
            "Clothing",
            "Transportation",
            "Entertainment",
            "Income"
    };

    /**
     * Adds a category to the database.
     * @param context The application context.
     * @param category The category to insert.
     */
    public static void addCategory(Context context, String category) {
        CategoryDbManager dbManager = new CategoryDbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Category.COLUMN_NAME_CATEGORY_NAME, category);
        db.insert(Category.TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Lists all categories in alphabetical order.
     * @param context The application context.
     * @return A list of all categories in the DB.
     */
    public static List<String> getCategories(Context context) {
        CategoryDbManager dbManager = new CategoryDbManager(context);
        SQLiteDatabase db = dbManager.getReadableDatabase();

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
        db.close();

        return categories;
    }

    /**
     * Gets the ID of a category with a given name.
     * @param context The application context.
     * @param category The category we want the ID of.
     * @return The ID of said category.
     * @throws IllegalArgumentException If the category doesn't exist.
     */
    public static int getCategoryId(Context context, String category) {
        CategoryDbManager dbManager = new CategoryDbManager(context);
        SQLiteDatabase db = dbManager.getReadableDatabase();

        Cursor cursor = db.query(
                Category.TABLE_NAME,
                new String[]{Category._ID},
                Category.COLUMN_NAME_CATEGORY_NAME + " = ?",
                new String[]{category},
                null,
                null,
                null
        );

        int categoryId = -1;
        while(cursor.moveToNext()) {
            categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(Category._ID));
        }
        cursor.close();
        db.close();

        if(categoryId == -1) throw new IllegalArgumentException("No category: " + category);

        return categoryId;
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
