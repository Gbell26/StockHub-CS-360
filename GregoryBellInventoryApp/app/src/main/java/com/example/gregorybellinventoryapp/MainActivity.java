package com.example.gregorybellinventoryapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText editUsername, editPassword;
    private Button buttonLogin, buttonCreateAccount;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.button);
        buttonCreateAccount = findViewById(R.id.button2);

        dbHelper = new DatabaseHelper(this);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();

                if (dbHelper.checkUser(username, password)) {
                    SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
                    boolean isPermissionRequested = preferences.getBoolean("SMSPermissionRequested", false);

                    if (!isPermissionRequested) {

                        Intent intent = new Intent(MainActivity.this, SMSRequestActivity.class);
                        startActivity(intent);
                    } else {

                        Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
                        startActivity(intent);
                    }

                    finish();
                } else {
                    // Failed login
                    Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean success = dbHelper.addUser(username, password);
                if (success) {
                    Toast.makeText(MainActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}