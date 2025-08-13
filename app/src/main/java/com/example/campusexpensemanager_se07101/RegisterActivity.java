package com.example.campusexpensemanager_se07101;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager_se07101.database.UserRepository;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RegisterActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword,edtMail, edtPhone;
    Button btnSignUp;
    UserRepository repository;
    TextView tvSignIn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        repository = new UserRepository(RegisterActivity.this);
        edtUsername = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        edtMail = findViewById(R.id.edtMail);
        edtPhone = findViewById(R.id.edtPhone);
        tvSignIn = findViewById(R.id.tvSignIn);
        signUpAccount();//saving accout user to database
        registerUserAccount();
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });

    }
    private void registerUserAccount(){
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // su kien dang ky tai khoan nguoi dung
                String user = edtUsername.getText().toString().trim();
                if(TextUtils.isEmpty(user)){
                    edtUsername.setError("enter name please");
                    return;
                }
                String password = edtPassword.getText().toString().trim();
                if(TextUtils.isEmpty(password)){
                    edtPassword.setError("enter password please");
                    return;
                }
                String email = edtMail.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    edtMail.setError("enter email please");
                    return;
                }
                String phone = edtPhone.getText().toString().trim();
                //save account to database
                long insert = repository.saveUserAccount(user, password, email, phone);
                if(insert == -1){
                    //fail
                    Toast.makeText(RegisterActivity.this, "save account fail", Toast.LENGTH_SHORT).show();

                }else {
                    // thanh cong
                    Toast.makeText(RegisterActivity.this, " thanh cong", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);

                }
            }
        });
    }
    private void signUpAccount()
    {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if(TextUtils.isEmpty(user))
                {
                    edtUsername.setError("khong duoc de trong");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    edtPassword.setError("khong dc de trong mk");
                    return;
                }
                // save dato to file
                FileOutputStream fileOutput = null;
                try {
                    user = user + "|";
                    fileOutput = openFileOutput("user.txt", Context.MODE_APPEND);// ghi tiep noi dang sau , ten tu quy dinh nhe
                    fileOutput.write(user.getBytes(StandardCharsets.UTF_8));
                    fileOutput.write(password.getBytes(StandardCharsets.UTF_8));
                    fileOutput.write('\n');
                    fileOutput.close();
                    edtUsername.setText("");
                    edtPassword.setText("");
                    Toast.makeText(RegisterActivity.this,"Sign up account thanh cong", Toast.LENGTH_SHORT).show();

                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }
}

