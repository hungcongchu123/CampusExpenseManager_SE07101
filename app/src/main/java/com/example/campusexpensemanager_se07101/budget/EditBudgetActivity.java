package com.example.campusexpensemanager_se07101.budget;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager_se07101.MenuActivity;
import com.example.campusexpensemanager_se07101.R;
import com.example.campusexpensemanager_se07101.database.BudgetRepository;
import com.example.campusexpensemanager_se07101.database.CategoryRepository;
import com.example.campusexpensemanager_se07101.model.Budget;
import com.example.campusexpensemanager_se07101.model.Category;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditBudgetActivity extends AppCompatActivity {
    EditText edtBudgetName, edtBudgetMoney, edtDescription;
    Button btnSave, btnBack;
    Spinner spinnerCategory;
    TextView tvStartDate, tvEndDate;
    BudgetRepository budgetRepository;
    CategoryRepository categoryRepository;
    private int budgetIdToEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget); // Tái sử dụng layout add budget

        // Ánh xạ view
        btnSave = findViewById(R.id.btnSaveBudget);
        btnBack = findViewById(R.id.btnBackBudget);
        edtBudgetName = findViewById(R.id.edtBudgetName);
        edtBudgetMoney = findViewById(R.id.edtBudgetMoney);
        edtDescription = findViewById(R.id.edtDescriptions);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        tvStartDate = findViewById(R.id.tvSelectedDate);
        tvEndDate = findViewById(R.id.tvEndDate);

        budgetRepository = new BudgetRepository(this);
        categoryRepository = new CategoryRepository(this);

        // Lấy ID của budget từ Intent
        budgetIdToEdit = getIntent().getIntExtra("budget_id", -1);

        if (budgetIdToEdit != -1) {
            // Tải dữ liệu budget từ database
            loadBudgetDetails(budgetIdToEdit);
        } else {
            Toast.makeText(this, "Không tìm thấy ngân sách để chỉnh sửa", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Setup Spinner
        List<Category> categories = categoryRepository.getAllCategories();
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Set các listener cho DatePicker
        Calendar calendar = Calendar.getInstance();
        tvStartDate.setOnClickListener(v -> showDatePicker(calendar, tvStartDate));
        tvEndDate.setOnClickListener(v -> showDatePicker(calendar, tvEndDate));

        btnSave.setOnClickListener(v -> updateBudget());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadBudgetDetails(int budgetId) {
        Budget budget = budgetRepository.getBudgetById(budgetId);
        if (budget != null) {
            edtBudgetName.setText(budget.getName());
            edtBudgetMoney.setText(String.valueOf(budget.getMoney()));
            edtDescription.setText(budget.getDescription());
            tvStartDate.setText(budget.getStartDate());
            tvEndDate.setText(budget.getEndDate());

            // Chọn đúng danh mục trên Spinner
            List<Category> categories = categoryRepository.getAllCategories();
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == budget.getCategoryId()) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        }
    }

    private void updateBudget() {
        try {
            String nameBudget = edtBudgetName.getText().toString().trim();
            String moneyStr = edtBudgetMoney.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
            int categoryId = selectedCategory.getId();
            String startDate = tvStartDate.getText().toString().trim();
            String endDate = tvEndDate.getText().toString().trim();

            if (TextUtils.isEmpty(nameBudget) || TextUtils.isEmpty(moneyStr)) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int moneyBudget;
            try {
                moneyBudget = Integer.parseInt(moneyStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            Budget updatedBudget = new Budget(budgetIdToEdit, moneyBudget, nameBudget, description, categoryId, startDate, endDate);
            long result = budgetRepository.updateBudget(updatedBudget);

            if(result > 0) {
                Toast.makeText(this, "Ngân sách đã được cập nhật thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Không thể cập nhật ngân sách", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi cập nhật ngân sách: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void showDatePicker(Calendar calendar, TextView targetView) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            targetView.setText(sdf.format(calendar.getTime()));
        };
        new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}