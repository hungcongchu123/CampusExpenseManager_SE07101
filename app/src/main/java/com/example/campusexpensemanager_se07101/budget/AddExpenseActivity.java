package com.example.campusexpensemanager_se07101.budget;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager_se07101.R;
import com.example.campusexpensemanager_se07101.database.BudgetRepository;
import com.example.campusexpensemanager_se07101.database.ExpenseRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText edtAmount, edtDescription, edtExpenseName;
    private Spinner spinnerBudget;
    private TextView tvExpenseDate;
    private Button btnSave,btnBack;

    private String selectedDate = "";
    private String selectedCategory = "";

    private BudgetRepository budgetRepo;
    private ExpenseRepository expenseRepo;
    private BudgetAlertHelper budgetAlertHelper;

    private int userId;

    private List<String> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // ✅ Lấy userId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not found. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }



        // Ánh xạ view
        edtExpenseName = findViewById(R.id.edtExpenseName);
        edtAmount = findViewById(R.id.edtExpenseAmount);
        edtDescription = findViewById(R.id.edtExpenseDesc);
        spinnerBudget = findViewById(R.id.spinnerBudget);
        tvExpenseDate = findViewById(R.id.tvExpenseDate);
        btnSave = findViewById(R.id.btnSaveExpense);
        btnBack = findViewById(R.id.btnBack);
        budgetRepo = new BudgetRepository(this);
        expenseRepo = new ExpenseRepository(this);
        budgetAlertHelper = new BudgetAlertHelper(this);

        loadBudgetCategories();

        tvExpenseDate.setOnClickListener(v -> showDatePicker());

        spinnerBudget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, android.view.View view, int position, long l) {
                selectedCategory = categoryList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedCategory = "";
            }
        });

        btnSave.setOnClickListener(v -> saveExpense());
        btnBack.setOnClickListener(v -> finish()); // Quay lại màn trước
    }

    private void loadBudgetCategories() {
        // lay danh muc theo user id từ budget
        categoryList = budgetRepo.getAllCategoriesByUser(userId);
        if (categoryList.isEmpty()) {
            Toast.makeText(this, "No budgets found. Please create a budget first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBudget.setAdapter(adapter);
        selectedCategory = categoryList.get(0); // mặc định chọn category đầu tiên
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            selectedDate = y + "-" + (m + 1) + "-" + d;
            tvExpenseDate.setText(selectedDate);
        }, year, month, day);
        dialog.show();
    }

    private void saveExpense() {
        String name = edtExpenseName.getText().toString().trim();
        String amountStr = edtAmount.getText().toString().trim();
        String desc = edtDescription.getText().toString().trim();

        if (name.isEmpty()||amountStr.isEmpty() || desc.isEmpty() || selectedDate.isEmpty() || selectedCategory.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            edtAmount.setError("Invalid amount");
            return;
        }

        //  Kiểm tra trùng lặp expense trước khi lưu
        if (expenseRepo.isExpenseDuplicateNew(userId, name)) {
            Toast.makeText(this, "Expense with same name already exists!", Toast.LENGTH_LONG).show();
            edtExpenseName.setError("Duplicate expense detected");
            return;
        }

        //  Kiểm tra vượt quá ngân sách trước khi lưu
        if (budgetAlertHelper.isBudgetExceeded(userId, selectedCategory, amount)) {
            Toast.makeText(this, "Vượt quá ngân sách! Không thể thêm chi tiêu này.", Toast.LENGTH_LONG).show();
            return; // Thoát, không lưu gì cả
        }

        //  Kiểm tra ngân sách trước khi lưu (cảnh báo gần ngưỡng)
        budgetAlertHelper.checkBudgetWithCallback(userId, selectedCategory, amount, () -> {
            // Callback này sẽ chạy sau khi user đóng dialog
            saveExpenseToDatabase(name, amount, desc);
        });
    }

    private void saveExpenseToDatabase(String name, int amount, String desc) {
        // Lấy budgetId dựa trên category và user
        int budgetId = budgetRepo.getBudgetIdByCategoryAndUser(selectedCategory, userId);
        if (budgetId == -1) {
            Toast.makeText(this, "Invalid budget selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thêm vào bảng expense
        long insertedId = expenseRepo.addExpense(userId, budgetId, name, selectedCategory, amount, desc, selectedDate);

        if (insertedId != -1) {
            // ✅ Trừ tiền khỏi ngân sách
            expenseRepo.subtractFromBudget(budgetId, amount);
            Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add expense", Toast.LENGTH_SHORT).show();
        }
    }


}
