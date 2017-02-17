package katrinahoffert.simplebudget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import katrinahoffert.simplebudget.util.Functional;
import katrinahoffert.simplebudget.util.GuiUtil;

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

    /** Opens a prompt for a category name and upon receiving one, adds that category. */
    private void addCategory() {
        GuiUtil.generateTextInputAlert(this,
                getString(R.string.add_category_title),
                getString(R.string.category_add_confirm_button),
                getString(R.string.category_cancel_button),
                new Functional.Action1<String>() {
                    @Override
                    public void action(String input) {
                        try {
                            if (!input.equals("")) {
                                CategoryDbManager.addCategory(CategoriesActivity.this, input);
                            } else {
                                GuiUtil.generateSimpleAlert(CategoriesActivity.this, getString(R.string.category_empty_error));
                            }
                        } catch (IllegalArgumentException e) {
                            GuiUtil.generateSimpleAlert(CategoriesActivity.this, getString(R.string.category_exists_error_message));
                        }

                        // Redraw the table
                        categories = CategoryDbManager.getCategories(CategoriesActivity.this);
                        initializeCategoryTable();
                    }
                },
                null);
    }

    /**
     * Initializes the category table, recreating it from scratch with rows containing the category
     * name and buttons to edit and remove that category. Event handlers are attached to those buttons.
     * The edit button opens a prompt to rename the category (optionally cancelling). The remove
     * button opens a confirmation prompt and on confirmation, removes the category.
     */
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
                    GuiUtil.generateTextInputAlert(CategoriesActivity.this,
                            getString(R.string.edit_category_title),
                            getString(R.string.category_save_button),
                            getString(R.string.category_cancel_button),
                            new Functional.Action1<String>() {
                                @Override
                                public void action(String input) {
                                    category.category = input;
                                    CategoryDbManager.updateCategoryName(CategoriesActivity.this, category);

                                    // Redraw the table
                                    categories = CategoryDbManager.getCategories(CategoriesActivity.this);
                                    initializeCategoryTable();
                                }
                            },
                            category.category);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GuiUtil.generateConfirmationPrompt(
                            CategoriesActivity.this,
                            getString(R.string.category_remove_title),
                            String.format(getString(R.string.category_remove_confirmation), category.category),
                            getString(R.string.category_remove_confirm_button),
                            getString(R.string.category_cancel_button),
                            new Functional.Action() {
                                @Override
                                public void action() {
                                    CategoryDbManager.deleteCategoryName(CategoriesActivity.this, category._id);

                                    // Redraw the table
                                    categories = CategoryDbManager.getCategories(CategoriesActivity.this);
                                    initializeCategoryTable();
                                }
                            }
                    );
                }
            });
        }
    }
}
