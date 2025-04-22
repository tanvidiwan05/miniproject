package com.example.credify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class teacher_register extends AppCompatActivity {
    private EditText fullNameInput, emailInput, passwordInput, collegeNameInput, departmentInput;
    private Button registerButton;
    private TextView loginRedirect;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_register); // Make sure you have this layout

        // Initialize FirebaseAuth and DatabaseReference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Teachers");

        // Initialize views
        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        collegeNameInput = findViewById(R.id.collegeNameInput);
        departmentInput = findViewById(R.id.branchNameInput);
        registerButton = findViewById(R.id.registerButton);
        loginRedirect = findViewById(R.id.registerText);

        // Register button click listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTeacher();
            }
        });

        // Login redirect click listener
        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(teacher_register.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerTeacher() {
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String collegeName = collegeNameInput.getText().toString().trim();
        String department = departmentInput.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(fullName)) {
            fullNameInput.setError("Full Name is required");
            fullNameInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Valid Email is required");
            emailInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            passwordInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(collegeName)) {
            collegeNameInput.setError("College Name is required");
            collegeNameInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(department)) {
            departmentInput.setError("Department is required");
            departmentInput.requestFocus();
            return;
        }

        String teacherName = fullName;

        // Register user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            Teacher teacher = new Teacher(userId, fullName, email, collegeName, department);

                            // Save teacher data in Firebase Realtime Database
                            databaseReference.child(userId).setValue(teacher).addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful()) {
                                    Toast.makeText(teacher_register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(teacher_register.this, teacher_homepage.class);
                                    intent.putExtra("TEACHER_NAME", teacherName);
                                    startActivity(intent);
                                    clearInputs();
                                } else {
                                    Toast.makeText(teacher_register.this, "Failed to Save Data: " + dbTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(teacher_register.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void clearInputs() {
        fullNameInput.setText("");
        emailInput.setText("");
        passwordInput.setText("");
        collegeNameInput.setText("");
        departmentInput.setText("");
    }
}
