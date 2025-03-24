package com.example.credify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentPageActivity extends AppCompatActivity {
    private EditText fullNameInput, emailInput, passwordInput, collegeNameInput, branchNameInput, yearInput;
    private Button registerButton;
    private TextView loginRedirect;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_page);

        // Initialize FirebaseAuth and DatabaseReference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Students");

        // Initialize views
        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        collegeNameInput = findViewById(R.id.collegeNameInput);
        branchNameInput = findViewById(R.id.branchNameInput);
        yearInput = findViewById(R.id.yearInput);
        registerButton = findViewById(R.id.registerButton);
        loginRedirect = findViewById(R.id.registerText);

        // Register button click listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerStudent();
            }
        });

        // Login redirect click listener
        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentPageActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerStudent() {
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String collegeName = collegeNameInput.getText().toString().trim();
        String branchName = branchNameInput.getText().toString().trim();
        String year = yearInput.getText().toString().trim();

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
        if (TextUtils.isEmpty(branchName)) {
            branchNameInput.setError("Branch Name is required");
            branchNameInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(year)) {
            yearInput.setError("Year is required");
            yearInput.requestFocus();
            return;
        }
        String studentName="";
        // Register user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            Student student = new Student(userId, fullName, email, collegeName, branchName, year);

                            // Save student data in Firebase Realtime Database
                            databaseReference.child(userId).setValue(student).addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful()) {
                                    Toast.makeText(StudentPageActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(StudentPageActivity.this, StudentHomepage.class);
                                    intent.putExtra("STUDENT_NAME", studentName); // studentName is the input name
                                    startActivity(intent);

                                    clearInputs();
                                } else {
                                    Toast.makeText(StudentPageActivity.this, "Failed to Save Data: " + dbTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(StudentPageActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void clearInputs() {
        fullNameInput.setText("");
        emailInput.setText("");
        passwordInput.setText("");
        collegeNameInput.setText("");
        branchNameInput.setText("");
        yearInput.setText("");
    }
}
