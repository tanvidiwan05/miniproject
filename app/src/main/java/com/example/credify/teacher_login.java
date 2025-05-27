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

public class teacher_login extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerText, dontHaveAccountText;
    private LinearLayout emailContainer, passwordContainer;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login); // Make sure the layout file name is correct

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        emailContainer = findViewById(R.id.emailContainer);
        passwordContainer = findViewById(R.id.passwordContainer);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);
        dontHaveAccountText = findViewById(R.id.dontHaveAccountText);

        // Handle focus change for email input
        emailInput.setOnFocusChangeListener((v, hasFocus) -> {
            emailContainer.setBackgroundResource(R.drawable.rounded_edittext_border);
        });

        // Handle focus change for password input
        passwordInput.setOnFocusChangeListener((v, hasFocus) -> {
            passwordContainer.setBackgroundResource(R.drawable.rounded_edittext_border);
        });

        // Login button click listener
        loginButton.setOnClickListener(v -> loginTeacher());

        // Register text click listener
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(teacher_login.this, teacher_register.class); // Redirect to teacher registration page
            startActivity(intent);
        });
    }

    private void loginTeacher() {
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

        // Authenticate user with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(teacher_login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(teacher_login.this, teacher_homepage.class); // Redirect to teacher homepage
                            intent.putExtra("TEACHER_UID", user.getUid()); // Pass user UID (or teacher data)
                            startActivity(intent);
                            finish(); // Close the login activity to prevent returning to it
                        }
                    } else {
                        Toast.makeText(teacher_login.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
