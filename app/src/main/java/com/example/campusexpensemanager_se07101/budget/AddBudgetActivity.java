package com.example.campusexpensemanager_se07101.budget;

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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager_se07101.MainActivity;
import com.example.campusexpensemanager_se07101.R;
import com.example.campusexpensemanager_se07101.budgetFragment;
import com.example.campusexpensemanager_se07101.database.BudgetRepository;

public class AddBudgetActivity extends AppCompatActivity {
    EditText edtButgetName, edtBugetMoney, edtDescription;
    Button btnSave, btnBack;
    Spinner spinnerCategory;
    TextView tvSelectedDate;
    BudgetRepository repository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);
        //  Lấy user_id từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1); // Nếu chưa có thì -1
        // Ánh xạ view
        btnSave = findViewById(R.id.btnSaveBudget);
        btnBack = findViewById(R.id.btnBackBudget);
        edtButgetName = findViewById(R.id.edtBudgetName);
        edtBugetMoney = findViewById(R.id.edtBudgetMoney);
        edtDescription = findViewById(R.id.edtDescriptions);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        repository = new BudgetRepository(AddBudgetActivity.this);
        // ⚠️ Thêm đoạn này để tránh spinner bị null
        String[] categories = {"Food", "Transport", "Shopping", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                AddBudgetActivity.this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Lấy dữ liệu từ người dùng nhập vào
                    String nameBudget = edtButgetName.getText().toString().trim();
                    String moneyStr = edtBugetMoney.getText().toString().trim();
                    String description = edtDescription.getText().toString().trim();
                    String category = spinnerCategory.getSelectedItem().toString();
                    String startDate = "2025-07-31"; // tạm thời mặc định hoặc lấy từ DatePicker
                    String endDate = "2025-08-01";   // tạm thời mặc định hoặc lấy từ DatePicker
                    // Kiểm tra tên ngân sách
                    if (TextUtils.isEmpty(nameBudget)) {
                        edtButgetName.setError("Enter name Budget, please");
                        return;
                    }

                    // Kiểm tra số tiền có nhập không
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

                    // Gọi repository để thêm vào database
                    long insertBudget = repository.AddNewBudget(userId,nameBudget, moneyBudget, description,
                            category, startDate, endDate);

                    if (insertBudget == -1) {
                        Toast.makeText(AddBudgetActivity.this, "Cannot create budget, please try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddBudgetActivity.this, "Budget created successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddBudgetActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                } catch (Exception e) {
                    Toast.makeText(AddBudgetActivity.this, "Lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace(); // In lỗi ra Logcat
                }
            }
        });
    }
}
