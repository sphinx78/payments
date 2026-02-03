package com.expensetracker.ui.dashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.expensetracker.R;
import com.expensetracker.database.AllInOneDAO;
import com.expensetracker.model.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * MainActivity - Dashboard
 * 
 * Main screen showing:
 * - Group selector
 * - Statistics (total expenses, members, pending settlements)
 * - Recent expenses list
 * - Who owes whom list
 */
public class MainActivity extends AppCompatActivity {

    private AllInOneDAO dao;
    private Spinner groupSpinner;
    private TextView totalExpensesText;
    private TextView totalMembersText;
    private TextView pendingAmountText;
    private RecyclerView expensesRecyclerView;
    private RecyclerView settlementsRecyclerView;
    private FloatingActionButton fab;

    private List<Group> allGroups = new ArrayList<>();
    private int selectedGroupId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DAO
        dao = new AllInOneDAO(this);

        // Initialize views
        groupSpinner = findViewById(R.id.groupSpinner);
        totalExpensesText = findViewById(R.id.totalExpensesText);
        totalMembersText = findViewById(R.id.totalMembersText);
        pendingAmountText = findViewById(R.id.pendingAmountText);
        expensesRecyclerView = findViewById(R.id.expensesRecyclerView);
        settlementsRecyclerView = findViewById(R.id.settlementsRecyclerView);
        fab = findViewById(R.id.fab);

        // Setup RecyclerViews
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        settlementsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load groups
        loadGroups();

        // Setup group spinner listener
        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && allGroups.size() > position - 1) {
                    selectedGroupId = allGroups.get(position - 1).getGroupId();
                    loadGroupData(selectedGroupId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // FAB click listener
        fab.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this,
                    "Add Expense feature - Connect to AddExpenseActivity",
                    Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Load all groups and populate spinner
     */
    private void loadGroups() {
        allGroups = dao.getAllGroups();

        List<String> groupNames = new ArrayList<>();
        groupNames.add("Select a group");
        for (Group group : allGroups) {
            groupNames.add(group.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                groupNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapter);

        // Auto-select first group
        if (allGroups.size() > 0) {
            groupSpinner.setSelection(1);
        }
    }

    /**
     * Load data for selected group
     */
    private void loadGroupData(int groupId) {
        // Load statistics
        loadStatistics(groupId);

        // Load recent expenses
        loadExpenses(groupId);

        // Load settlements
        loadSettlements(groupId);
    }

    /**
     * Load and display statistics
     */
    private void loadStatistics(int groupId) {
        // Total expenses
        double totalExpenses = dao.getTotalExpensesForGroup(groupId);
        totalExpensesText.setText(formatCurrency(totalExpenses));

        // Total members
        List<User> members = dao.getGroupMembers(groupId);
        totalMembersText.setText(String.valueOf(members.size()));

        // Pending settlements
        double pendingAmount = dao.getPendingSettlementAmount(groupId);
        pendingAmountText.setText(formatCurrency(pendingAmount));
    }

    /**
     * Load and display expenses
     */
    private void loadExpenses(int groupId) {
        List<Expense> expenses = dao.getGroupExpenses(groupId);

        ExpenseAdapter adapter = new ExpenseAdapter(expenses, dao);
        expensesRecyclerView.setAdapter(adapter);
    }

    /**
     * Load and display settlements
     */
    private void loadSettlements(int groupId) {
        List<Settlement> settlements = dao.getPendingSettlements(groupId);

        SettlementAdapter adapter = new SettlementAdapter(settlements, dao);
        settlementsRecyclerView.setAdapter(adapter);
    }

    /**
     * Format currency with ₹ symbol
     */
    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        return formatter.format(amount);
    }
}
