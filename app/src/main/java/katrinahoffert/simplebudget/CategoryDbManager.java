package katrinahoffert.simplebudget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static katrinahoffert.simplebudget.DbContract.CategoryTable;

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
        values.put(CategoryTable.COLUMN_NAME_CATEGORY_NAME, category);
        db.insert(CategoryTable.TABLE_NAME, null, values);
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
                CategoryTable.TABLE_NAME,
                new String[]{CategoryTable.COLUMN_NAME_CATEGORY_NAME},
                null,
                null,
                null,
                null,
                CategoryTable.COLUMN_NAME_CATEGORY_NAME + " ASC"
        );

        List<String> categories = new ArrayList<>();
        while(cursor.moveToNext()) {
            categories.add(cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_NAME_CATEGORY_NAME)));
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
                CategoryTable.TABLE_NAME,
                new String[]{CategoryTable._ID},
                CategoryTable.COLUMN_NAME_CATEGORY_NAME + " = ?",
                new String[]{category},
                null,
                null,
                null
        );

        int categoryId = -1;
        while(cursor.moveToNext()) {
            categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(CategoryTable._ID));
        }
        cursor.close();
        db.close();

        if(categoryId == -1) throw new IllegalArgumentException("No category: " + category);

        return categoryId;
    }
}
