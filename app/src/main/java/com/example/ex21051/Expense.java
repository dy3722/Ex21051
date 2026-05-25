package com.example.ex21051;

/**
 * @author David Yusupov <dy3722@bs.amalnet.k12.il>
 * @version 1.0
 * @since 18/5/2026
 * Expense Model Class
 * <p>
 * Represents a single expense entity with properties for ID, description, amount, category, and date.
 */
public class Expense {
    private String id;
    private String description;
    private double amount;
    private String category;
    private String date;

    /**
     * Default constructor
     */
    public Expense() {}

    /**
     * Constructor for creating a new Expense without an ID (e.g., before inserting to DB).
     *
     * @param description The description of the expense.
     * @param amount The monetary amount of the expense.
     * @param category The category to which the expense belongs.
     * @param date The date of the expense (format: YYYY-MM-DD).
     */
    public Expense(String description, double amount, String category, String date) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    /**
     * Constructor for creating an Expense with an ID (e.g., when retrieving from DB).
     *
     * @param id The unique database identifier.
     * @param description The description of the expense.
     * @param amount The monetary amount of the expense.
     * @param category The category to which the expense belongs.
     * @param date The date of the expense (format: YYYY-MM-DD).
     */
    public Expense(String id, String description, double amount, String category, String date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
