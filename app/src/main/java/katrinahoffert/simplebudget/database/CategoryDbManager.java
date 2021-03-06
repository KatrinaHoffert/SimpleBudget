package katrinahoffert.simplebudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import katrinahoffert.simplebudget.model.BudgetEntry;
import katrinahoffert.simplebudget.model.Category;

import static katrinahoffert.simplebudget.database.DbContract.CategoryTable;
import static katrinahoffert.simplebudget.database.DbContract.BudgetEntryTable;

public class CategoryDbManager {
    /**
     * Adds a category to the database. This fails if the category already exists (and is not deleted).
     * If the category is deleted, then it is undeleted.
     * @param context The application context.
     * @param category The category to insert.
     */
    public static void addCategory(Context context, String category) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        // Don't allow adding if there's already a non-deleted category with that name
        Cursor cursor = db.query(
                CategoryTable.TABLE_NAME,
                new String[]{CategoryTable._ID},
                CategoryTable.COLUMN_NAME_IS_DELETED + " = 0 AND " + CategoryTable.COLUMN_NAME_CATEGORY_NAME + " = ?",
                new String[]{category},
                null,
                null,
                null
        );

        if(cursor.getCount() > 0) {
            cursor.close();
            throw new IllegalArgumentException("Category with name already exists");
        }
        cursor.close();

        // Check if there's a deleted category that can be re-enabled
        ContentValues updateValues = new ContentValues();
        updateValues.put(CategoryTable.COLUMN_NAME_IS_DELETED, 0);

        int count = db.update(
                DbContract.CategoryTable.TABLE_NAME,
                updateValues,
                CategoryTable.COLUMN_NAME_CATEGORY_NAME + " = ?",
                new String[]{category}
        );
        if(count > 0) return;

        // Otherwise add it as normal
        ContentValues insertValues = new ContentValues();
        insertValues.put(CategoryTable.COLUMN_NAME_CATEGORY_NAME, category);
        db.insert(CategoryTable.TABLE_NAME, null, insertValues);
        db.close();
    }

    /**
     * Updates a category's name in the database.
     * @param context The application context.
     * @param category The category we're updating.
     */
    public static void updateCategoryName(Context context, Category category) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CategoryTable.COLUMN_NAME_CATEGORY_NAME, category.category);

        db.update(
                DbContract.CategoryTable.TABLE_NAME,
                values,
                DbContract.CategoryTable._ID + " = ?",
                new String[]{Integer.toString(category._id)}
        );
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
                CategoryTable.COLUMN_NAME_IS_DELETED + " = 0",
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

    /**
     * Sets a category as deleted. This doesn't actually delete it, but flips a flag that prevents
     * its usage in new entries.
     * @param context The application context.
     * @param id The ID of the category we're deleting.
     */
    public static void deleteCategoryName(Context context, int id) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CategoryTable.COLUMN_NAME_IS_DELETED, 1);

        db.update(
                DbContract.CategoryTable.TABLE_NAME,
                values,
                DbContract.CategoryTable._ID + " = ?",
                new String[]{Integer.toString(id)}
        );
        db.close();
    }

    /**
     * Gets the most recently used categories.
     * @param context Application context.
     * @param maxCategories The maximum number of categories we want.
     * @return A list of categories that have been used most recently (date wise only, not based on
     * when the user added the entry).
     */
    public static List<Category> getRecentlyUsedCategories(Context context, int maxCategories) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        String query = String.format("SELECT %s, %s FROM %s JOIN %s ON %s.%s = %s.%s WHERE %s = 0 GROUP BY %s ORDER BY %s DESC LIMIT %d",
                CategoryTable.COLUMN_NAME_CATEGORY_NAME, BudgetEntryTable.COLUMN_NAME_CATEGORY_ID, BudgetEntryTable.TABLE_NAME,
                CategoryTable.TABLE_NAME, BudgetEntryTable.TABLE_NAME, BudgetEntryTable.COLUMN_NAME_CATEGORY_ID, CategoryTable.TABLE_NAME,
                CategoryTable._ID, CategoryTable.COLUMN_NAME_IS_DELETED, CategoryTable.COLUMN_NAME_CATEGORY_NAME,
                BudgetEntryTable.COLUMN_NAME_DATE, maxCategories);

        Cursor cursor = db.rawQuery(query, new String[]{});

        List<Category> categories = new ArrayList<>();

        while(cursor.moveToNext()) {
            Category category = new Category();
            category.category = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_NAME_CATEGORY_NAME));
            category._id = cursor.getInt(cursor.getColumnIndexOrThrow(BudgetEntryTable.COLUMN_NAME_CATEGORY_ID));
            categories.add(category);
        }
        cursor.close();
        db.close();

        return categories;
    }
}
