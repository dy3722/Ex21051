package com.example.ex21051;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * @author David Yusupov <dy3722@bs.amalnet.k12.il>
 * @version 1.0
 * @since 18/5/2026
 * Custom Adapter Class
 * <p>
 * A custom BaseAdapter to display a list of Expense objects in a ListView.
 * It inflates a custom layout (custom_adapter.xml) for each row and populates it with expense data.
 */
public class CustomAdapter extends BaseAdapter {
    private Context context;
    private List<Expense> expenseList;
    private LayoutInflater inflater;

    /**
     * Constructor for the CustomAdapter.
     *
     * @param context The current context.
     * @param expenseList The list of Expense objects to represent in the ListView.
     */
    public CustomAdapter(Context context, List<Expense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
        inflater = (LayoutInflater.from(context));
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return expenseList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param i Position of the item whose data we want within the adapter's data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int i) {
        return expenseList.get(i);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param i The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     *
     * @param i The position of the item within the adapter's data set.
     * @param view The old view to reuse, if possible.
     * @param viewGroup The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_adapter, viewGroup, false);

        TextView tvDescription = (TextView) view.findViewById(R.id.tvDescriptionInCustAdp);
        TextView tvCategory = (TextView) view.findViewById(R.id.tvCategoryInCustAdp);
        TextView tvAmount = (TextView) view.findViewById(R.id.tvAmountInCustAdp);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDateInCustAdp);

        tvDescription.setText(expenseList.get(i).getDescription());
        tvCategory.setText(expenseList.get(i).getCategory());
        tvAmount.setText(MainActivity.formatClearNumber(expenseList.get(i).getAmount()) + "₪");
        tvDate.setText(expenseList.get(i).getDate());

        return view;
    }
}
