package com.example.campusexpensemanager_se07101.budget;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager_se07101.MenuActivity;
import com.example.campusexpensemanager_se07101.R;
import com.example.campusexpensemanager_se07101.database.BudgetRepository;
import com.example.campusexpensemanager_se07101.model.Category;
import com.example.campusexpensemanager_se07101.database.CategoryRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddBudgetActivity extends AppCompatActivity {
    EditText edtButgetName, edtBugetMoney, edtDescription;
    Button btnSave, btnBack;
    Spinner spinnerCategory;
    TextView tvSelectedDate, tvEndDate;
    BudgetRepository budgetRepository;
    CategoryRepository categoryRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        // Ánh xạ view
        btnSave = findViewById(R.id.btnSaveBudget);
        btnBack = findViewById(R.id.btnBackBudget);
        edtButgetName = findViewById(R.id.edtBudgetName);
        edtBugetMoney = findViewById(R.id.edtBudgetMoney);
        edtDescription = findViewById(R.id.edtDescriptions);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvEndDate = findViewById(R.id.tvEndDate);

        budgetRepository = new BudgetRepository(this);
        categoryRepository = new CategoryRepository(this);

        // Lấy danh mục từ database và gán vào Spinner
        List<Category> categories = categoryRepository.getAllCategories();
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();

        tvSelectedDate.setOnClickListener(v -> showDatePicker(calendar, tvSelectedDate));
        tvEndDate.setOnClickListener(v -> showDatePicker(calendar, tvEndDate));

        btnSave.setOnClickListener(v -> {
            try {
                String nameBudget = edtButgetName.getText().toString().trim();
                String moneyStr = edtBugetMoney.getText().toString().trim();
                String description = edtDescription.getText().toString().trim();
                String startDate = tvSelectedDate.getText().toString().trim();
                String endDate = tvEndDate.getText().toString().trim();

                if (TextUtils.isEmpty(nameBudget)) {
                    edtButgetName.setError("Enter name Budget, please");
                    return;
                }
                if (TextUtils.isEmpty(moneyStr)) {
                    edtBugetMoney.setError("Please enter budget amount");
                    return;
                }

                int moneyBudget;
                try {
                    moneyBudget = Integer.parseInt(moneyStr);
                    if (moneyBudget <= 0) {
                        edtBugetMoney.setError("Amount must be greater than 0");
                        return;
                    }
                } catch (NumberFormatException e) {
                    edtBugetMoney.setError("Invalid number format");
                    return;
                }

                Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
                if (selectedCategory == null) {
                    Toast.makeText(AddBudgetActivity.this, "Please select a category", Toast.LENGTH_SHORT).show();
                    return;
                }
                int categoryId = selectedCategory.getId();

                long insertBudget = budgetRepository.AddNewBudget(userId, nameBudget, moneyBudget, description,
                        categoryId, startDate, endDate);

                if (insertBudget == -1) {
                    Toast.makeText(AddBudgetActivity.this, "Cannot create budget, please try again", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddBudgetActivity.this, "Budget created successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddBudgetActivity.this, MenuActivity.class);
                    startActivity(intent);
                }

            } catch (Exception e) {
                Toast.makeText(AddBudgetActivity.this, "Lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(AddBudgetActivity.this, MenuActivity.class);
            startActivity(intent);
        });
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