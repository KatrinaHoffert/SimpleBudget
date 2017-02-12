package katrinahoffert.simplebudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class CategoryDbManager {
    /**
     * Adds a category to the database.
     * @param context The application context.
     * @param category The category to insert.
     */
    public static void addCategory(Context context, String category) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.CategoryTable.COLUMN_NAME_CATEGORY_NAME, category);
        db.insert(DbContract.CategoryTable.TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Lists all categories in alphabetical order.
     * @param context The application context.
     * @return A list of all categories in the DB.
     */
    public static List<String> getCategories(Context context) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getReadableDatabase();

        Cursor cursor = db.query(
                DbContract.CategoryTable.TABLE_NAME,
                new String[]{DbContract.CategoryTable.COLUMN_NAME_CATEGORY_NAME},
                null,
                null,
                null,
                null,
                DbContract.CategoryTable.COLUMN_NAME_CATEGORY_NAME + " ASC"
        );

        List<String> categories = new ArrayList<>();
        while(cursor.moveToNext()) {
            categories.add(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.CategoryTable.COLUMN_NAME_CATEGORY_NAME)));
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
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getReadableDatabase();

        Cursor cursor = db.query(
                DbContract.CategoryTable.TABLE_NAME,
                new String[]{DbContract.CategoryTable._ID},
                DbContract.CategoryTable.COLUMN_NAME_CATEGORY_NAME + " = ?",
                new String[]{category},
                null,
                null,
                null
        );

        int categoryId = -1;
        while(cursor.moveToNext()) {
            categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.CategoryTable._ID));
        }
        cursor.close();
        db.close();

        if(categoryId == -1) throw new IllegalArgumentException("No category: " + category);

        return categoryId;
    }
}
