package com.example.ex21051;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author David Yusupov <dy3722@bs.amalnet.k12.il>
 * @version 1.0
 * @since 18/5/2026
 * Main Activity
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, View.OnCreateContextMenuListener, AdapterView.OnItemSelectedListener {
    private Intent siCred, siAddExpense, siSearch;
    private int clickPos;
    private ListView lvExpenses;
    private List<Expense> expensesList;
    private SQLiteDatabase db;
    private HelperDB hlp;
    private Cursor crsr;
    private ArrayAdapter adp;
    private TextView tvAmount;
    private final Calendar calendar = Calendar.getInstance();
    private double monthAmount = 0;
    private CustomAdapter customAdapter;
    private final String[] categories = {"Restaurant", "Recreation", "Shopping", "Transferring money", "Buying online", "Other..."};
    private String strSelectedCategory;

    /**
     * Initializes the activity and sets up the UI components.
     * <p>
     * This method sets the content view to activity_main, initializes the Intent for
     * the credits screen, and links the layout and TextView variables to their XML IDs.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     * being shut down then this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expensesList = new ArrayList<>();

        hlp = new HelperDB(this);
        db = hlp.getWritableDatabase();
        db.close();

        siCred = new Intent(this, CreditsActivity.class);
        siAddExpense = new Intent(this, AddExpenseActivity.class);
        siSearch = new Intent(this, SearchActivity.class);

        tvAmount = findViewById(R.id.tvAmount);
        lvExpenses = findViewById(R.id.lvExpenses);
        lvExpenses.setOnItemLongClickListener(this);
        lvExpenses.setOnCreateContextMenuListener(this);
        lvExpenses.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    /**
     * Called when the activity will start interacting with the user.
     * <p>
     * This method clears the current expense list, reads all records from the database,
     * calculates the total expenses for the current month, and updates the ListView
     * and the total amount TextView accordingly.
     */
    @Override
    protected void onResume() {
        super.onResume();

        expensesList.clear();
        monthAmount = 0;

        db = hlp.getReadableDatabase();

        crsr = db.query(Expenses.TABLE_EXPENSES, null, null, null, null, null, null);

        int colId = crsr.getColumnIndex(Expenses.KEY_ID);
        int colAmount = crsr.getColumnIndex(Expenses.AMOUNT);
        int colDate = crsr.getColumnIndex(Expenses.DATE);
        int colCategory = crsr.getColumnIndex(Expenses.CATEGORY);
        int colDescription = crsr.getColumnIndex(Expenses.DESCRIPTION);

        crsr.moveToFirst();
        while (!crsr.isAfterLast())
        {
            int id = crsr.getInt(colId);
            double amount = crsr.getDouble(colAmount);
            String date = crsr.getString(colDate);
            String category = crsr.getString(colCategory);
            String description = crsr.getString(colDescription);

            if (Integer.parseInt(date.substring(5,7))-1 == calendar.get(Calendar.MONTH))
            {
                monthAmount += amount;
            }

            Expense expense = new Expense(id, description, amount, category, date);

            expensesList.add(expense);

            crsr.moveToNext();
        }

        crsr.close();
        db.close();

        tvAmount.setText("The amount of your expenses:\n" + formatClearNumber(monthAmount) + "₪");

        customAdapter = new CustomAdapter(this, expensesList);
        lvExpenses.setAdapter(customAdapter);
    }

    /**
     * Formats a double value into a clear, readable string representation.
     * <p>
     * This method checks if the number is within a standard readable range (between 0.001 and 1,000,000).
     * If so, it returns a standard decimal string. For very large or very small numbers, it replaces
     * the scientific notation 'E' with a clearer mathematical representation (e.g., " * 10^").
     *
     * @param num The double value to be formatted.
     * @return A formatted string representing the number, optimized for readability.
     */
    public static String formatClearNumber(double num) {
        if (num == 0) return "0";

        double absNum = Math.abs(num);

        if (absNum >= 0.001 && absNum < 1000000) {
            java.text.DecimalFormat normalDf = new java.text.DecimalFormat("0.#####");
            return normalDf.format(num);
        }
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.#####E0");
        String formatted = df.format(num);

        if (!formatted.contains("E")) {
            return formatted;
        }

        String cleanString = formatted.replace("E", " * 10^");

        return cleanString;
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
     * This implementation checks if the selected item is the "Credits" or "AddExpense" or "Search" menu item
     * and, if so, starts the activity defined by the Intent 'si'.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed,
     * true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuCredits)
        {
            startActivity(siCred);
        }
        else if (id == R.id.menuAddExpense)
        {
            startActivity(siAddExpense);
        }
        else if (id == R.id.menuSearch)
        {
            startActivity(siSearch);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when a context menu for the view is about to be shown.
     * <p>
     * This method sets the title of the context menu and adds the "Delete"
     * and "Edit" options for the long-clicked expense item.
     *
     * @param menu The context menu that is being built.
     * @param v The view for which the context menu is being built.
     * @param menuInfo Extra information about the item for which the context menu should be shown.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Options:");
        menu.add("Delete");
        menu.add("Edit");
    }

    /**
     * This hook is called whenever an item in a context menu is selected.
     * <p>
     * This method handles the "Delete" and "Edit" actions. If "Delete" is chosen,
     * it removes the expense from the database. If "Edit" is chosen, it opens an
     * AlertDialog with a dynamic layout to update the expense details in the database.
     *
     * @param item The context menu item that was selected.
     * @return boolean Return false to allow normal context menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String op = item.getTitle().toString();
        if (op.equals("Delete"))
        {
            int idToDelete = expensesList.get(clickPos).getId();

            Log.i("SQL_LOG", "Deleting Expense with ID: " + idToDelete);

            db = hlp.getWritableDatabase();
            db.delete(Expenses.TABLE_EXPENSES, Expenses.KEY_ID + "=?", new String[] {Integer.toString(idToDelete)});
            db.close();

            onResume();
        }
        else if (op.equals("Edit"))
        {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);

            adb.setCancelable(false);
            adb.setTitle("Edit the expense:");

            final LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText etAlertAmount = new EditText(this);
            etAlertAmount.setHint("the amount of the expense [₪]");
            etAlertAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            etAlertAmount.setText(formatClearNumber(expensesList.get(clickPos).getAmount()));
            layout.addView(etAlertAmount);

            final EditText etAlertDescription = new EditText(this);
            etAlertDescription.setHint("Description");
            etAlertDescription.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
            etAlertDescription.setText(expensesList.get(clickPos).getDescription());
            layout.addView(etAlertDescription);

            final Spinner spAlertCategory = new Spinner(this);
            ArrayAdapter<String> adp = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categories);
            spAlertCategory.setAdapter(adp);
            spAlertCategory.setOnItemSelectedListener(this);
            for (int i = 0; i < categories.length; i++)
            {
                if (categories[i].equals(expensesList.get(clickPos).getCategory()))
                {
                    spAlertCategory.setSelection(i);
                    break;
                }
            }
            layout.addView(spAlertCategory);

            final Button btnAlertDate = new Button(this);
            btnAlertDate.setText(expensesList.get(clickPos).getDate());
            btnAlertDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar calendar = Calendar.getInstance();
                    DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            String strDate;
                            strDate = (year < 10)? "000" + year : (year < 100)? "00" + year : (year < 1000)? "0" + year : "" + year;
                            month++;
                            strDate += "-" + ((month < 10)? "0" + month : "" + month);
                            strDate += "-" + ((day < 10)? "0" + day : "" + day);

                            btnAlertDate.setText(strDate);
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
                }
            });
            layout.addView(btnAlertDate);

            adb.setView(layout);

            adb.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (etAlertAmount.getText().toString().isEmpty())
                    {
                        Toast.makeText(MainActivity.this, "Please enter amount!", Toast.LENGTH_SHORT).show();
                    }
                    else if (!AddExpenseActivity.validEditText(etAlertAmount.getText().toString()))
                    {
                        Toast.makeText(MainActivity.this, "Invalid number!", Toast.LENGTH_SHORT).show();
                    }
                    else if (etAlertDescription.getText().toString().isEmpty())
                    {
                        Toast.makeText(MainActivity.this, "Please enter description!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        double amount = Double.parseDouble(etAlertAmount.getText().toString());
                        String description = etAlertDescription.getText().toString();
                        String date = btnAlertDate.getText().toString();

                        ContentValues cv = new ContentValues();

                        cv.put(Expenses.AMOUNT, amount);
                        cv.put(Expenses.CATEGORY, strSelectedCategory);
                        cv.put(Expenses.DATE, date);
                        cv.put(Expenses.DESCRIPTION, description);

                        db = hlp.getWritableDatabase();
                        db.update(Expenses.TABLE_EXPENSES, cv, Expenses.KEY_ID + "=?", new String[] {Integer.toString(expensesList.get(clickPos).getId())});
                        db.close();

                        onResume();
                    }
                }
            });

            adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            AlertDialog ad = adb.create();
            ad.show();
        }

        onResume();

        return super.onContextItemSelected(item);
    }

    /**
     * callback method to be invoked when an item in this AdapterView has been clicked and held.
     * <p>
     * This implementation captures the position of the item being long-clicked
     * into the 'clickPos' variable, which is later used by the Context Menu actions.
     *
     * @param parent   The AbsListView where the click happened.
     * @param view     The view within the AbsListView that was clicked.
     * @param position The position of the view in the list.
     * @param id       The row id of the item that was clicked.
     * @return boolean Return false to allow the context menu to be created.
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        clickPos = position;

        return false;
    }

    /**
     * Callback method to be invoked when an item in the Spinner has been selected.
     * <p>
     * This method updates the selected category string when the user changes the category
     * inside the Edit AlertDialog.
     *
     * @param adapterView The AdapterView where the selection happened.
     * @param view The view within the AdapterView that was clicked.
     * @param i The position of the view in the adapter.
     * @param l The row id of the item that is selected.
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        strSelectedCategory = categories[i];
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
}