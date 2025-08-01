package com.example.campusexpensemanager_se07101.budget;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager_se07101.R;

import java.util.Calendar;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText edtAmount, edtDescription;
    private Spinner spinnerBudget;
    private TextView tvExpenseDate;
    private Button btnSave;

    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Gán view
        edtAmount = findViewById(R.id.edtExpenseAmount);
        edtDescription = findViewById(R.id.edtExpenseDesc);
        spinnerBudget = findViewById(R.id.spinnerBudget);
        tvExpenseDate = findViewById(R.id.tvExpenseDate);
        btnSave = findViewById(R.id.btnSaveExpense);

        // Setup Spinner giả (sau này lấy từ database)
        ArrayAdapter<String> budgetAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Item 1", "Item 2", "Item 3"}
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
                    selectedDate = d + "/" + (m + 1) + "/" + y;
                    tvExpenseDate.setText(selectedDate);
                }, year, month, day);
        dialog.show();
    }

    private void saveExpense() {
        String amountStr = edtAmount.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String selectedBudget = spinnerBudget.getSelectedItem().toString();

        if (amountStr.isEmpty() || description.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        // TODO: Lưu vào SQLite hoặc Room (chưa làm)
        Toast.makeText(this, "Saved: " + amount + ", " + description + ", " + selectedBudget + ", " + selectedDate, Toast.LENGTH_LONG).show();

        // Sau khi lưu có thể kết thúc Activity hoặc clear field
        finish();
    }
}
