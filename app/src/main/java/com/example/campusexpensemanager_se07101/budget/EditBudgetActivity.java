package com.example.campusexpensemanager_se07101.budget;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager_se07101.MenuActivity;
import com.example.campusexpensemanager_se07101.R;
import com.example.campusexpensemanager_se07101.database.BudgetRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditBudgetActivity extends AppCompatActivity {

    EditText edtBudgetName, edtBugetMoney, edtDescription;
    TextView tvStartDate, tvEndDate;
    Spinner spinnerCategory;
    Button btnUpdate, btnBack, btnDelete;

    BudgetRepository repository;
    ArrayList<String> categoryList;
    ArrayAdapter<String> adapter;
    Calendar calendar;
    int userId;

    private int ID_BUDGET = 0;
    private String NAME_BUDGET = null;
    private String DESC_BUDGET = null;
    private int MONEY_BUDGET = 0;
    private String CATEGORY_BUDGET = null;
    private String START_DATE = null;
    private String END_DATE = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_budget);

        // Lấy user_id từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        // Ánh xạ view
        edtBudgetName = findViewById(R.id.edtBudgetName);
        edtBugetMoney = findViewById(R.id.edtBudgetMoney);
        edtDescription = findViewById(R.id.edtDescription);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnUpdate = findViewById(R.id.btnUpdateBudget);
        btnBack = findViewById(R.id.btnBackBudget);
        btnDelete = findViewById(R.id.btnDeleteBudget);

        repository = new BudgetRepository(this);

        // Lấy ngày hiện tại
        calendar = Calendar.getInstance();

        // Nhận dữ liệu từ Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ID_BUDGET = bundle.getInt("ID_BUDGET", 0);
            NAME_BUDGET = bundle.getString("NAME_BUDGET", "");
            MONEY_BUDGET = bundle.getInt("MONEY_BUDGET", 0);
            DESC_BUDGET = bundle.getString("DESCRIPTION_BUDGET", "");
            CATEGORY_BUDGET = bundle.getString("CATEGORY_BUDGET", "");
            START_DATE = bundle.getString("START_DATE", "");
            END_DATE = bundle.getString("END_DATE", "");
        }

        // Load category list
        categoryList = new ArrayList<>(repository.getAllCategoriesByUser(userId));
        categoryList.add("➕ Add new category");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Set category đã chọn trước
        if (CATEGORY_BUDGET != null) {
            int index = categoryList.indexOf(CATEGORY_BUDGET);
            if (index >= 0) {
                spinnerCategory.setSelection(index);
            } else {
                // Nếu danh mục không còn tồn tại trong danh sách, thêm vào
                categoryList.add(0, CATEGORY_BUDGET);
                adapter.notifyDataSetChanged();
                spinnerCategory.setSelection(0);
            }
        }

        // Gán giá trị cũ vào view
        edtBudgetName.setText(NAME_BUDGET);
        edtBugetMoney.setText(String.valueOf(MONEY_BUDGET));
        edtDescription.setText(DESC_BUDGET);
        tvStartDate.setText("Start Date: " + START_DATE);
        tvEndDate.setText("End Date: " + END_DATE);

        // Sự kiện chọn ngày
        tvStartDate.setOnClickListener(v -> showDatePicker(tvStartDate, true));
        tvEndDate.setOnClickListener(v -> showDatePicker(tvEndDate, false));

        // Thêm danh mục mới
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

        btnUpdate.setOnClickListener(v -> updateBudget());
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(EditBudgetActivity.this, MenuActivity.class));
            finish();
        });

        btnDelete.setOnClickListener(v -> {
            int result = repository.deleteBudgetById(ID_BUDGET);
            if (result == 0) {
                Toast.makeText(this, "Không thể xóa", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditBudgetActivity.this, MenuActivity.class));
                finish();
            }
        });
    }

    private void updateBudget() {
        String nameBudget = edtBudgetName.getText().toString().trim();
        String moneyStr = edtBugetMoney.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        CATEGORY_BUDGET = spinnerCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(nameBudget)) {
            edtBudgetName.setError("Enter name Budget, please");
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

        int result = repository.updateBudgetById(
                nameBudget,
                moneyBudget,
                description,
                CATEGORY_BUDGET,
                START_DATE,
                END_DATE,
                ID_BUDGET
        );

        if (result == -1 || result == 0) {
            Toast.makeText(this, "Cannot update budget, please try again", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Budget updated successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MenuActivity.class));
            finish();
        }
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

    private void showDatePicker(TextView targetView, boolean isStart) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String selectedDate = sdf.format(calendar.getTime());

            if (isStart) {
                START_DATE = selectedDate;
                tvStartDate.setText("Start Date: " + START_DATE);
            } else {
                END_DATE = selectedDate;
                tvEndDate.setText("End Date: " + END_DATE);
            }
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
