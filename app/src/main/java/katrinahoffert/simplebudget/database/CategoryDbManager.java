package katrinahoffert.simplebudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import katrinahoffert.simplebudget.model.Category;

import static katrinahoffert.simplebudget.database.DbContract.CategoryTable;

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
    public static List<Category> getCategories(Context context) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getReadableDatabase();

        Cursor cursor = db.query(
                CategoryTable.TABLE_NAME,
                new String[]{CategoryTable._ID, CategoryTable.COLUMN_NAME_CATEGORY_NAME},
                null,
                null,
                null,
                null,
                CategoryTable.COLUMN_NAME_CATEGORY_NAME + " ASC"
        );

        List<Category> categories = new ArrayList<>();
        while(cursor.moveToNext()) {
            Category category = new Category();
            category._id = cursor.getInt(cursor.getColumnIndexOrThrow(CategoryTable._ID));
            category.category = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_NAME_CATEGORY_NAME));
            categories.add(category);
        }
        cursor.close();
        db.close();

        return categories;
    }
}
