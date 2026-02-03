package com.expensetracker.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.expensetracker.R;
import com.expensetracker.database.AllInOneDAO;
import com.expensetracker.model.Settlement;
import com.expensetracker.model.User;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for Settlements
 */
public class SettlementAdapter extends RecyclerView.Adapter<SettlementAdapter.ViewHolder> {

    private List<Settlement> settlements;
    private AllInOneDAO dao;

    public SettlementAdapter(List<Settlement> settlements, AllInOneDAO dao) {
        this.settlements = settlements;
        this.dao = dao;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_settlement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Settlement settlement = settlements.get(position);

        // Get user names
        User fromUser = dao.getUserByPhone(settlement.getFromUser());
        User toUser = dao.getUserByPhone(settlement.getToUser());

        String fromName = fromUser != null ? fromUser.getName() : "Unknown";
        String toName = toUser != null ? toUser.getName() : "Unknown";

        holder.fromUserText.setText(fromName);
        holder.toUserText.setText(toName);

        // Format amount
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        holder.amountText.setText(formatter.format(settlement.getNetBalance()));
    }

    @Override
    public int getItemCount() {
        return settlements.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fromUserText;
        TextView toUserText;
        TextView amountText;

        ViewHolder(View itemView) {
            super(itemView);
            fromUserText = itemView.findViewById(R.id.fromUserName);
            toUserText = itemView.findViewById(R.id.toUserName);
            amountText = itemView.findViewById(R.id.settlementAmount);
        }
    }
}
