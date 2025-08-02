package com.example.campusexpensemanager_se07101.budget;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager_se07101.R;
import com.example.campusexpensemanager_se07101.database.BudgetRepository;
import com.example.campusexpensemanager_se07101.database.ExpenseRepository;
import com.example.campusexpensemanager_se07101.model.Budget;
import com.example.campusexpensemanager_se07101.model.Expense;

import java.util.Calendar;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText edtAmount, edtDescription;
    private Spinner spinnerBudget;
    private TextView tvExpenseDate;
    private Button btnSave;

    private String selectedDate = "";
    private BudgetRepository budgetRepository;
    private ExpenseRepository expenseRepository;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        // Gán view
        edtAmount = findViewById(R.id.edtExpenseAmount);
        edtDescription = findViewById(R.id.edtExpenseDesc);
        spinnerBudget = findViewById(R.id.spinnerBudget);
        tvExpenseDate = findViewById(R.id.tvExpenseDate);
        btnSave = findViewById(R.id.btnSaveExpense);

        budgetRepository = new BudgetRepository(this);
        expenseRepository = new ExpenseRepository(this);

        // Lấy danh sách ngân sách từ database và gán vào Spinner
        List<Budget> budgets = budgetRepository.getAllBudgetsByUserId(userId);
        ArrayAdapter<Budget> budgetAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                budgets
        );
        budgetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBudget.setAdapter(budgetAdapter);

        // Chọn ngày
        tvExpenseDate.setOnClickListener(view -> showDatePicker());

        // Xử lý nút Save
        btnSave.setOnClickListener(view -> saveExpense());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    selectedDate = y + "-" + (m + 1) + "-" + d; // Định dạng yyyy-MM-dd
                    tvExpenseDate.setText(selectedDate);
                }, year, month, day);
        dialog.show();
    }

    private void saveExpense() {
        String amountStr = edtAmount.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        Budget selectedBudget = (Budget) spinnerBudget.getSelectedItem();

        if (amountStr.isEmpty() || description.isEmpty() || selectedDate.isEmpty() || selectedBudget == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        int budgetId = selectedBudget.getId();

        Expense newExpense = new Expense(0, userId, budgetId, description, amount, selectedDate);
        long result = expenseRepository.addExpense(newExpense);

        if (result != -1) {
            Toast.makeText(this, "Expense saved successfully", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save expense", Toast.LENGTH_LONG).show();
        }
    }
}