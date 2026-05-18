package com.example.ex21051;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * @author David Yusupov <dy3722@bs.amalnet.k12.il>
 * @version 1.0
 * @since 18/5/2026
 * Database Helper Class
 * <p>
 * A helper class to manage database creation and version management for the Expenses app.
 */
public class HelperDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "data_base_expenses.db";
    private static final int DATABASE_VERSION = 1;
    String strCreate, strDelete;

    /**
     * Constructor for the HelperDB class.
     *
     * @param context The context to use for locating paths to the the database.
     */
    public HelperDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     * <p>
     * This method executes the SQL query to build the Expenses table with its predefined columns.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        strCreate = "CREATE TABLE " + Expenses.TABLE_EXPENSES;
        strCreate += " (" + Expenses.KEY_ID + " INTEGER PRIMARY KEY,";
        strCreate += " " + Expenses.DESCRIPTION + " TEXT,";
        strCreate += " " + Expenses.AMOUNT + " REAL,";
        strCreate += " " + Expenses.CATEGORY + " TEXT,";
        strCreate += " " + Expenses.DATE + " TEXT";
        strCreate += ");";

        Log.i("SQL_LOG", "onCreate Query: " + strCreate);

        sqLiteDatabase.execSQL(strCreate);
    }

    /**
     * Called when the database needs to be upgraded.
     * <p>
     * This method drops the existing Expenses table and recreates it to apply schema changes.
     *
     * @param sqLiteDatabase The database.
     * @param i The old database version.
     * @param i1 The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        strDelete = "DROP TABLE IF EXISTS " + Expenses.TABLE_EXPENSES;
        sqLiteDatabase.execSQL(strDelete);

        Log.i("SQL_LOG", "onUpgrade Query: " + strDelete);

        onCreate(sqLiteDatabase);
    }
}
