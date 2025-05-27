package com.example.credify;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class admin_login extends AppCompatActivity {



    private EditText emailInput, passwordInput;
    private Button loginButton;


  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminlogin);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (username.equals("admin") && password.equals("password")) {
                    // Correct credentials, go to HomeActivity
                    Intent intent = new Intent(admin_login.this, admin_home.class);
                    startActivity(intent);
                    finish(); // Optional: to close LoginActivity
                } else {
                    // Wrong credentials
                    Toast.makeText(admin_login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}