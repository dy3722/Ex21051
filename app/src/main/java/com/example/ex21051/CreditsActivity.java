package com.example.ex21051;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author David Yusupov <dy3722@bs.amalnet.k12.il>
 * @version 1.0
 * @since 18/5/2026
 * Credits Activity
 */
public class CreditsActivity extends AppCompatActivity {
    private Intent siAddExpense, siSearch;

    /**
     * Initializes the Credits activity.
     * <p>
     * This method sets the activity's layout to the credits screen
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     * being shut down then this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        siAddExpense = new Intent(this,AddExpenseActivity.class);
        siSearch = new Intent(this,SearchActivity.class);
    }

    /**
     * Initialize the contents of the Activity's standard options menu for the Credits screen.
     * <p>
     * This method inflates the menu resource (R.menu.main) and populates the menu
     * for this activity, allowing users to access options from the credits page.
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
     * Handles action bar item clicks for the Credits activity.
     * <p>
     * This method intercepts clicks on the menu items. If the "Main" or "AddExpense" or "Search" menu item
     * is selected, it triggers the Intent to navigate back to the MainActivity.
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
        else if (id == R.id.menuAddExpense)
        {
            finish();
            startActivity(siAddExpense);
        }
        else if (id == R.id.menuSearch)
        {
            finish();
            startActivity(siSearch);
        }

        return super.onOptionsItemSelected(item);
    }
}