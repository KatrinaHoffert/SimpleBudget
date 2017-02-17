package katrinahoffert.simplebudget.database;

import android.provider.BaseColumns;

public class DbContract {
    public static class BudgetEntryTable implements BaseColumns {
        public static final String TABLE_NAME = "budget_entry";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_DATE = "date";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_CATEGORY_ID + " INTEGER," +
                COLUMN_NAME_AMOUNT + " INTEGER," +
                COLUMN_NAME_DATE + " TEXT)";
    }

    public static class CategoryTable implements BaseColumns {
        public static final String TABLE_NAME = "category";
        public static final String COLUMN_NAME_CATEGORY_NAME = "category_name";
        public static final String COLUMN_NAME_IS_DELETED = "is_deleted";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_IS_DELETED + " INTEGER DEFAULT 0," +
                COLUMN_NAME_CATEGORY_NAME + " TEXT)";

        /** The default categories that are inserted in the DB on first run. */
        public static final String[] DEFAULT_CATEGORIES = {
            "Food",
            "Housing",
            "Utilities",
            "Clothing",
            "Transportation",
            "Entertainment",
            "Income"
        };
    }
}
