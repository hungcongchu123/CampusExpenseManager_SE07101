package com.example.campusexpensemanager_se07101.budget;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager_se07101.R;

public class AddBudgetActivity extends AppCompatActivity {
    EditText edtButgetName, edtBugetMoney, edtDescription;
    Button btnSave, btnBack;
    Spinner spinnerCategory;
    TextView tvSelectedDate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);
        btnSave = findViewById(R.id.btnSaveBudget);
        btnBack = findViewById(R.id.btnBackBudget);
        edtButgetName = findViewById(R.id.edtBudgetName);
        edtBugetMoney = findViewById(R.id.edtBudgetMoney);
        edtDescription = findViewById(R.id.edtDescriptions);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameBudget = edtButgetName.getText().toString().trim();
                int moneyBudget = Integer.parseInt(edtBugetMoney.getText().toString().trim());
                String description = edtDescription.getText().toString().trim();
                if(TextUtils.isEmpty(nameBudget))
                {
                    edtButgetName.setError("Enter name Budget, pelase");
                }
                if(moneyBudget <= 0)
                {
                    edtBugetMoney.setError("mời nhập lại mệnh giá tiền");
                }
                Toast.makeText(AddBudgetActivity.this, "Saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
                

            }
        });


    }
}
