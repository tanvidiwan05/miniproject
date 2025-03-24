package com.example.credify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerText;
    private LinearLayout emailContainer, passwordContainer;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        emailContainer = findViewById(R.id.emailContainer);
        passwordContainer = findViewById(R.id.passwordContainer);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);

        // Handle focus change for email input
        emailInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                emailContainer.setBackgroundResource(R.drawable.rounded_edittext_border);
            } else {
                emailContainer.setBackgroundResource(R.drawable.rounded_edittext_border);
            }
        });

        // Handle focus change for password input
        passwordInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                passwordContainer.setBackgroundResource(R.drawable.rounded_edittext_border);
            } else {
                passwordContainer.setBackgroundResource(R.drawable.rounded_edittext_border);
            }
        });

        // Login button click listener
        loginButton.setOnClickListener(v -> loginUser());

        // Register text click listener
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, StudentPageActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return;
        }
        String studentName="";
        // Authenticate user with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, StudentHomepage.class);
                            intent.putExtra("STUDENT_NAME", studentName); // studentName is the input name
                            startActivity(intent);

                            finish();  // Close login activity to prevent returning to it
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
