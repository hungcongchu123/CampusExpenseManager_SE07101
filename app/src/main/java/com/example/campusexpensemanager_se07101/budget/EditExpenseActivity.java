package com.example.campusexpensemanager_se07101.budget;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager_se07101.MenuActivity;
import com.example.campusexpensemanager_se07101.R;
import com.example.campusexpensemanager_se07101.database.ExpenseModel;
import com.example.campusexpensemanager_se07101.database.ExpenseRepository;
import com.example.campusexpensemanager_se07101.budget.BudgetAlertHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditExpenseActivity extends AppCompatActivity {
    private EditText etAmount, etDescription, etExpenseName;
    private Spinner spinnerCategory;
    private TextView tvDate;
    private Button btnUpdate,btnDelete, btnBack;
    private ExpenseRepository expenseRepository;
    private BudgetAlertHelper budgetAlertHelper;
    private int expenseId;
    private int userId;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        // ✅ Lấy userId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not found. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }



        // Ánh xạ các view từ layout
        etExpenseName = findViewById(R.id.edtExpenseName);
        etAmount = findViewById(R.id.edtExpenseAmount);
        etDescription = findViewById(R.id.edtExpenseDesc);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        tvDate = findViewById(R.id.tvExpenseDate);
        btnUpdate = findViewById(R.id.btnUpdateExpense);
        btnDelete = findViewById(R.id.btnDeleteExpense);
        btnBack = findViewById(R.id.btnBack);

        // Khởi tạo repository
        expenseRepository = new ExpenseRepository(this);
        budgetAlertHelper = new BudgetAlertHelper(this);
        
        // Thiết lập Spinner với danh sách danh mục từ cơ sở dữ liệu
        List<String> categories = expenseRepository.getAllBudgetCategories();
        if (categories.isEmpty()) {
            categories.add("Other");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Lấy expense_id từ Intent
        expenseId = getIntent().getIntExtra("expense_id", -1);

        // Load dữ liệu expense
        ExpenseModel expense = expenseRepository.getExpenseById(expenseId);
        if (expense != null) {
            etExpenseName.setText(expense.getExpenseName());
            etAmount.setText(String.valueOf(expense.getAmount()));
            etDescription.setText(expense.getDescription());
            tvDate.setText(expense.getDate());
        } else {
            Toast.makeText(this, "Expense not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Khởi tạo calendar để chọn ngày
        calendar = Calendar.getInstance();
        tvDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        tvDate.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Xử lý nút Update
        btnUpdate.setOnClickListener(v -> {
            // Lấy dữ liệu từ form
            String expenseName = etExpenseName.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String type = spinnerCategory.getSelectedItem().toString();
            String date = tvDate.getText().toString().trim();

            // Kiểm tra dữ liệu
            if (expenseName.isEmpty()||amountStr.isEmpty() || description.isEmpty() || type.isEmpty() || date.equals("Select date")) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int amount = Integer.parseInt(amountStr);

                // ✅ Kiểm tra ngân sách trước khi cập nhật
                budgetAlertHelper.checkBudgetWithCallback(userId, type, amount, () -> {
                    // Callback này sẽ chạy sau khi user đóng dialog
                    updateExpenseInDatabase(expenseName, amount, description, date, type);
                });
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            }
        });
        btnDelete.setOnClickListener(v -> {
            if (expenseId != -1) {
                // Lấy lại expense từ DB để lấy budgetId và amount
                ExpenseModel expenseToDelete = expenseRepository.getExpenseById(expenseId);

                if (expenseToDelete != null) {
                    int budgetId = expenseToDelete.getBudgetId();
                    int amount = expenseToDelete.getAmount();

                    // ✅ Cộng tiền lại vào ngân sách
                    expenseRepository.addBackToBudget(budgetId, amount);

                    // ✅ Xoá expense
                    int deleted = expenseRepository.deleteExpenseById(expenseId);
                    if (deleted > 0) {
                        Toast.makeText(this, "Expense deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Expense data not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(EditExpenseActivity.this, MenuActivity.class));
            finish();
        });

    }

    private void updateExpenseInDatabase(String expenseName, int amount, String description, String date, String type) {
        // Cập nhật expense trong cơ sở dữ liệu
        int updated = expenseRepository.updateExpense(
                expenseId,
                expenseName,
                amount,
                description,
                date,
                type // lấy từ spinner mới chọn
        );

        if (updated > 0) {
            Toast.makeText(this, "Expense updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

}