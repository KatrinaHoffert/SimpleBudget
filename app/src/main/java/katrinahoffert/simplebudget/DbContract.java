package katrinahoffert.simplebudget;

import android.provider.BaseColumns;

public class DbContract {
    public static class BudgetEntry implements BaseColumns {
        public static final String TABLE_NAME = "budget_entry";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_DATE = "date";
    }

    public static class Category implements BaseColumns {
        public static final String TABLE_NAME = "category";
        public static final String COLUMN_NAME_CATEGORY_NAME = "category_name";
    }
}
