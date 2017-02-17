package katrinahoffert.simplebudget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

        Button addCategoryButton = (Button) findViewById(R.id.addCategoryButton);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });

        initializeCategoryTable();
    }

    private void addCategory() {
        final EditText categoryNameInput = new EditText(this);
        categoryNameInput.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Just the text edit is too squished -- add some padding
        final LinearLayout layout = new LinearLayout(this);
        int padding = getResources().getDimensionPixelOffset(R.dimen.textAlertDialogHorizontalPadding);
        layout.setPadding(padding, 0, padding, 0);
        layout.addView(categoryNameInput);

        new AlertDialog.Builder(this)
                .setTitle(R.string.add_category_title)
                .setView(layout)
                .setPositiveButton(R.string.category_add_confirm_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        CategoryDbManager.addCategory(CategoriesActivity.this, categoryNameInput.getText().toString().trim());

                        // Redraw the table
                        categories =  CategoryDbManager.getCategories(CategoriesActivity.this);
                        initializeCategoryTable();
                    }
                })
                .setNegativeButton(R.string.category_cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void initializeCategoryTable() {
        TableLayout table = (TableLayout) findViewById(R.id.categoryEditTable);
        table.removeAllViews();

        for(final Category category : categories) {
            TableRow row = new TableRow(this);
            TextView categoryName = new TextView(this);
            categoryName.setText(category.category);
            row.addView(categoryName);
            Button editButton = new Button(this);
            editButton.setText(R.string.category_edit_button);
            row.addView(editButton);
            Button deleteButton = new Button(this);
            deleteButton.setText(R.string.category_remove_button);
            row.addView(deleteButton);
            table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText categoryNameInput = new EditText(CategoriesActivity.this);
                    categoryNameInput.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    categoryNameInput.setText(category.category);

                    // Just the text edit is too squished -- add some padding
                    final LinearLayout layout = new LinearLayout(CategoriesActivity.this);
                    int padding = getResources().getDimensionPixelOffset(R.dimen.textAlertDialogHorizontalPadding);
                    layout.setPadding(padding, 0, padding, 0);
                    layout.addView(categoryNameInput);

                    new AlertDialog.Builder(CategoriesActivity.this)
                            .setTitle(R.string.edit_category_title)
                            .setView(layout)
                            .setPositiveButton(R.string.category_save_button, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    category.category = categoryNameInput.getText().toString();
                                    CategoryDbManager.updateCategoryName(CategoriesActivity.this, category);

                                    // Redraw the table
                                    categories =  CategoryDbManager.getCategories(CategoriesActivity.this);
                                    initializeCategoryTable();
                                }
                            })
                            .setNegativeButton(R.string.category_cancel_button, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(CategoriesActivity.this)
                            .setTitle(R.string.category_remove_title)
                            .setMessage(String.format(getString(R.string.category_remove_confirmation), category.category))
                            .setPositiveButton(R.string.category_remove_confirm_button, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    CategoryDbManager.deleteCategoryName(CategoriesActivity.this, category._id);

                                    // Redraw the table
                                    categories =  CategoryDbManager.getCategories(CategoriesActivity.this);
                                    initializeCategoryTable();
                                }
                            })
                            .setNegativeButton(R.string.category_cancel_button, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            });
        }
    }
}
