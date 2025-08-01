package com.example.campusexpensemanager_se07101;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager_se07101.budget.AddBudgetActivity;
import com.example.campusexpensemanager_se07101.database.UserModel;
import com.example.campusexpensemanager_se07101.database.UserRepository;
public class LoginActivity extends AppCompatActivity {
    EditText edtUserName, edtPassword;
    Button btnLogin;
    UserRepository repository;
    TextView tvRegister;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegisterAccount);
        repository = new UserRepository(LoginActivity.this);
        repository.insertTestAccountIfNotExists();
        checkUserLogin();
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    private void checkUserLogin(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUserName.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if(TextUtils.isEmpty(username))
                {
                    edtUserName.setError("nhap ten di");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    edtPassword.setError("nhap mk");
                    return;
                }
                // kiem tra tk co ton tai ko
                UserModel user = repository.getInfoUserByUserName(username, password);
                assert user != null;
                if(user.getId() > 0 && user.getUsername() != null ){
                    //dang nhap thanh cong
                    //  Lưu user_id vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("user_id", user.getId()); // user là object lấy được từ DB
                    editor.apply();
                    Intent intent = new Intent(LoginActivity.this, AddBudgetActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    // dang nhap that bai
                    Toast.makeText(LoginActivity.this, "Account invals",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

    }
}

