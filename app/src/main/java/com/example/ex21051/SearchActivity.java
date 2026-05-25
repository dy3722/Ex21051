package com.example.ex21051;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author David Yusupov <dy3722@bs.amalnet.k12.il>
 * @version 1.0
 * @since 18/5/2026
 * Search Activity
 */
public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private Intent siCred, siAddExpense;
    private final String[] categories = {"All", "Restaurant", "Recreation", "Shopping", "Transferring money", "Buying online", "Other..."};
    private Spinner spCategoryFilter;
    private String strSelectedCategory;
    private EditText etMaxAmountFilter, etMinAmountFilter, etDescriptionSearch;
    private TextView tvInfoToUserSearch;
    private List<Expense> toSortExpenseList;
    private List<String> toSortExpenseKeysList;
    private ToggleButton tbSortType;
    private CustomAdapter customAdapter;
    private ListView lvSorted;
    private double maxAmount, minAmount;

    /**
     * Initializes the Search activity.
     * <p>
     * This method sets up the UI components, initializes the database helper,
     * and configures the category filtering spinner with its adapter.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     * being shut down then this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        siCred = new Intent(this, CreditsActivity.class);
        siAddExpense = new Intent(this, AddExpenseActivity.class);
        spCategoryFilter = findViewById(R.id.spCategoryFilter);
        etMaxAmountFilter = findViewById(R.id.etMaxAmountFilter);
        etMinAmountFilter = findViewById(R.id.etMinAmountFilter);
        tvInfoToUserSearch = findViewById(R.id.tvInfoToUserSearch);
        etDescriptionSearch = findViewById(R.id.etDescriptionSearch);
        tbSortType = findViewById(R.id.tbSortType);
        lvSorted = findViewById(R.id.lvSorted);

        toSortExpenseList = new ArrayList<>();
        toSortExpenseKeysList = new ArrayList<>();

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categories);
        spCategoryFilter.setAdapter(adp);
        spCategoryFilter.setOnItemSelectedListener(this);
    }

    /**
     * Sorts the filtered expense list by amount in descending order.
     * <p>
     * This method uses the Gnome sort algorithm to rearrange the items in the list
     * based on their monetary value (Amount).
     */
    public void startToSortExpenseListByAmount()
    {
        int pos = 0;
        int size = toSortExpenseList.size();

        while (pos < size)
        {
            if (pos == 0 || toSortExpenseList.get(pos).getAmount() <= toSortExpenseList.get(pos -1).getAmount())
            {
                pos++;
            }
            else
            {
                Collections.swap(toSortExpenseList, pos, pos-1);
                Collections.swap(toSortExpenseKeysList, pos, pos-1);
                pos--;
            }
        }
    }

    /**
     * Sorts the filtered expense list by date in descending order (newest to oldest).
     * <p>
     * This method parses the date strings (YYYY-MM-DD) into comparable integers
     * and uses the Gnome sort algorithm to rearrange the items.
     */
    private void startToSortExpenseListByDate()
    {
        int pos = 0;
        int size = toSortExpenseList.size();

        if (size == 1) return;

        while (pos < size)
        {
            if (pos == 0)
            {
                pos++;
            }
            String strDatePos = toSortExpenseList.get(pos).getDate();
            String strDateDecPos = toSortExpenseList.get(pos-1).getDate();

            if (Integer.parseInt(strDatePos.substring(0,4) + strDatePos.substring(5,7) + strDatePos.substring(8)) <= Integer.parseInt(strDateDecPos.substring(0,4) + strDateDecPos.substring(5,7) + strDateDecPos.substring(8)))
            {
                pos++;
            }
            else
            {
                Collections.swap(toSortExpenseList, pos, pos-1);
                Collections.swap(toSortExpenseKeysList, pos, pos-1);
                pos--;
            }
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     * <p>
     * This method inflates the menu resource (R.menu.main) into the provided Menu
     * object and adds the items to the action bar.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed; if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * <p>
     * This implementation checks if the selected item is the "Credits" or "AddExpense" or "Main" menu item
     * and, if so, starts the activity defined by the Intent 'si'.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed,
     * true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuMain)
        {
            finish();
        }
        else if (id == R.id.menuCredits)
        {
            finish();
            startActivity(siCred);
        }
        else if (id == R.id.menuAddExpense)
        {
            finish();
            startActivity(siAddExpense);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method to be invoked when a category in the Spinner has been selected.
     * <p>
     * Updates the selected category string and automatically triggers a new search.
     *
     * @param adapterView The AdapterView where the selection happened.
     * @param view The view within the AdapterView that was clicked.
     * @param i The position of the view in the adapter.
     * @param l The row id of the item that is selected.
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        strSelectedCategory = categories[i];
        onSearch(new View(this));
    }

    /**
     * Callback method to be invoked when the selection disappears from this view.
     *
     * @param adapterView The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.i("Spinner","Nothing selected");
    }

    /**
     * Filters, sorts, and displays the expenses based on the user's criteria.
     * <p>
     * This method validates the minimum and maximum amount inputs. If valid, it queries
     * all records from the database, filters them by the specified amount range, category,
     * and description text. It then applies the chosen sorting method and updates the ListView.
     *
     * @param view The view (Button or Spinner) that was clicked to trigger this method.
     */
    public void onSearch(View view) {
        if (etMaxAmountFilter.getText().toString().isEmpty() || etMinAmountFilter.getText().toString().isEmpty())
        {
            tvInfoToUserSearch.setText("You must fill the [Min] and [Max] values!");
        }
        else if (!AddExpenseActivity.validEditText(etMinAmountFilter.getText().toString()) || !AddExpenseActivity.validEditText(etMaxAmountFilter.getText().toString()))
        {
            tvInfoToUserSearch.setText("Invalid number (/numbers)!");
        }
        else if (Double.parseDouble(etMinAmountFilter.getText().toString()) > Double.parseDouble(etMaxAmountFilter.getText().toString()))
        {
            tvInfoToUserSearch.setText("The [Min] value can't be bigger than the [Max] value!");
        }
        else
        {
            maxAmount = Double.parseDouble(etMaxAmountFilter.getText().toString());
            minAmount = Double.parseDouble(etMinAmountFilter.getText().toString());

            tvInfoToUserSearch.setText("");

            toSortExpenseList.clear();
            toSortExpenseKeysList.clear();

            ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Connecting...");
            pd.setMessage("Please wait");
            pd.show();

            FBref.refExpenses.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    toSortExpenseList.clear();
                    toSortExpenseKeysList.clear();

                    pd.dismiss();

                    for (DataSnapshot data : snapshot.getChildren())
                    {
                        String stKey = (String) data.getKey();
                        toSortExpenseKeysList.add(stKey);

                        Expense exp = data.getValue(Expense.class);
                        toSortExpenseList.add(exp);

                        for (int i = 0 ; i < toSortExpenseList.size() ; i++)
                        {
                            if (toSortExpenseList.get(i).getAmount() < minAmount || toSortExpenseList.get(i).getAmount() > maxAmount)
                            {
                                toSortExpenseList.remove(i);
                                i--;
                            }
                        }

                        if (tbSortType.isChecked()) // Sort by date
                        {
                            startToSortExpenseListByDate();
                        }
                        else // Sort by amount
                        {
                            startToSortExpenseListByAmount();
                        }

                        for (int i = 0 ; i < toSortExpenseList.size() ; i++)
                        {
                            if (strSelectedCategory.equals("All"))
                            {
                                break;
                            }
                            else if (!toSortExpenseList.get(i).getCategory().equals(strSelectedCategory))
                            {
                                toSortExpenseList.remove(i);
                                i--;
                            }
                        }

                        if (!etDescriptionSearch.getText().toString().isEmpty())
                        {
                            for (int i = 0 ; i < toSortExpenseList.size() ; i++)
                            {

                                String description = etDescriptionSearch.getText().toString();

                                if (!toSortExpenseList.get(i).getDescription().contains(description))
                                {
                                    toSortExpenseList.remove(i);
                                    i--;
                                }
                            }
                        }

                        customAdapter = new CustomAdapter(SearchActivity.this, toSortExpenseList);
                        lvSorted.setAdapter(customAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SearchActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}