package com.example.credify;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.credify.R;
import com.example.credify.admin_dashboard;

public class admin_login extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login); // Make sure your XML filename is activity_admin_login.xml

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        // Set onClick listener
        loginButton.setOnClickListener(v -> {
            String enteredEmail = emailInput.getText().toString().trim();
            String enteredPassword = passwordInput.getText().toString().trim();

            if (enteredEmail.equals("admin") && enteredPassword.equals("admin123")) {
                Toast.makeText(admin_login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                // TODO: Replace with your admin dashboard activity
                Intent intent = new Intent(admin_login.this, admin_dashboard.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(admin_login.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
