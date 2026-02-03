package com.expensetracker.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.expensetracker.R;
import com.expensetracker.database.AllInOneDAO;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for Expenses
 */
public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private List<Expense> expenses;
    private AllInOneDAO dao;

    public ExpenseAdapter(List<Expense> expenses, AllInOneDAO dao) {
        this.expenses = expenses;
        this.dao = dao;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds expense data to the view holder.
     * Fetches the payer's name using the phone number stored in the expense record.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = expenses.get(position);

        holder.descriptionText.setText(expense.getDescription());

        // Get payer name from DAO using the phone number
        User payer = dao.getUserByPhone(expense.getPaidBy());
        String payerName = payer != null ? payer.getName() : "Unknown";
        holder.paidByText.setText(String.format("Paid by %s", payerName));

        // Format amount as currency (₹)
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        holder.amountText.setText(formatter.format(expense.getAmount()));
    }

    @Override
    public int getItemCount() {
        return expenses != null ? expenses.size() : 0;
    }

    /**
     * ViewHolder for Expense items.
     * Holds references to the views for description, payer, and amount.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView descriptionText;
        final TextView paidByText;
        final TextView amountText;

        ViewHolder(View itemView) {
            super(itemView);
            descriptionText = itemView.findViewById(R.id.expenseDescription);
            paidByText = itemView.findViewById(R.id.expensePaidBy);
            amountText = itemView.findViewById(R.id.expenseAmount);
        }
    }
}
