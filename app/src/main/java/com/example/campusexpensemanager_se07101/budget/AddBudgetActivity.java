package com.example.campusexpensemanager_se07101.budget;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.widget.Button;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager_se07101.MenuActivity;
import com.example.campusexpensemanager_se07101.R;
import com.example.campusexpensemanager_se07101.database.BudgetRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddBudgetActivity extends AppCompatActivity {
    EditText edtButgetName, edtBugetMoney, edtDescription;
    Button btnSave, btnBack;
    Spinner spinnerCategory;
    TextView tvSelectedDate, tvEndDate;
    BudgetRepository repository;
    ArrayList<String> categoryList;
    ArrayAdapter<String> adapter;
    int userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        // Lấy user_id từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        // Ánh xạ view
        btnSave = findViewById(R.id.btnSaveBudget);
        btnBack = findViewById(R.id.btnBackBudget);
        edtButgetName = findViewById(R.id.edtBudgetName);
        edtBugetMoney = findViewById(R.id.edtBudgetMoney);
        edtDescription = findViewById(R.id.edtDescriptions);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvEndDate = findViewById(R.id.tvEndDate);

        repository = new BudgetRepository(this);

        // Tải danh sách category từ DB theo user
        categoryList = new ArrayList<>(repository.getAllCategoriesByUser(userId));
        categoryList.add("➕ Add new category");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Bắt sự kiện chọn mục trong Spinner để thêm danh mục
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirstLoad = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstLoad) {
                    isFirstLoad = false;
                    return;
                }

                String selected = categoryList.get(position);
                if (selected.equals("➕ Add new category")) {
                    showAddCategoryDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();

        // Bắt sự kiện chọn ngày
        tvSelectedDate.setOnClickListener(v -> showDatePicker(calendar, tvSelectedDate));
        tvEndDate.setOnClickListener(v -> showDatePicker(calendar, tvEndDate));

        btnSave.setOnClickListener(v -> saveBudget());
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(AddBudgetActivity.this, MenuActivity.class);
            startActivity(intent);
        });
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter new category");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newCategory = input.getText().toString().trim();
            if (!newCategory.isEmpty()) {
                if (!categoryList.contains(newCategory)) {
                    categoryList.add(categoryList.size() - 1, newCategory);
                    adapter.notifyDataSetChanged();
                    spinnerCategory.setSelection(categoryList.indexOf(newCategory));
                } else {
                    Toast.makeText(this, "Category already exists", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Category cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveBudget() {
        try {
            String nameBudget = edtButgetName.getText().toString().trim();
            String moneyStr = edtBugetMoney.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();
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
            int originalBudget = moneyBudget;
            int remainingBudget = moneyBudget;
            long insertBudget = repository.AddNewBudget(
                    userId,
                    nameBudget,
                    originalBudget,
                    remainingBudget,
                    description,
                    category,
                    startDate,
                    endDate
            );

            if (insertBudget == -1) {
                Toast.makeText(this, "Cannot create budget, please try again", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Budget created successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

        new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
}
