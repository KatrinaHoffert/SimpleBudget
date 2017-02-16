package katrinahoffert.simplebudget;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import katrinahoffert.simplebudget.database.CategoryDbManager;
import katrinahoffert.simplebudget.model.Category;

public class CategoriesActivity extends AppCompatActivity {
    protected List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categories =  CategoryDbManager.getCategories(this);

        TableLayout table = (TableLayout) findViewById(R.id.categoryEditTable);
        table.removeAllViews();

        for(Category category : categories) {
            TableRow row = new TableRow(this);
            TextView categoryName = new TextView(this);
            categoryName.setText(category.category);
            row.addView(categoryName);
            Button editButton = new Button(this);
            editButton.setText("Edit");
            row.addView(editButton);
            Button deleteButton = new Button(this);
            deleteButton.setText("Remove");
            row.addView(deleteButton);
            table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

}
