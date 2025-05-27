package com.example.credify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class teacher_register extends AppCompatActivity {

    private EditText fullNameInput, emailInput, passwordInput, collegeNameInput;
    private Spinner departmentSpinner, designationSpinner;
    private Button registerButton;
    private TextView loginRedirect;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_register);

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Teachers");

        // View bindings
        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        collegeNameInput = findViewById(R.id.collegeNameInput);
        departmentSpinner = findViewById(R.id.departmentSpinner);
        designationSpinner = findViewById(R.id.designationSpinner);
        registerButton = findViewById(R.id.registerButton);
        loginRedirect = findViewById(R.id.registerText);

        // Setup spinner values
        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(
                this, R.array.branch_names, android.R.layout.simple_spinner_item);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(departmentAdapter);

        ArrayAdapter<CharSequence> designationAdapter = ArrayAdapter.createFromResource(
                this, R.array.designations_array, android.R.layout.simple_spinner_item);
        designationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        designationSpinner.setAdapter(designationAdapter);

        registerButton.setOnClickListener(v -> registerTeacher());

        loginRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(teacher_register.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void registerTeacher() {
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String collegeName = collegeNameInput.getText().toString().trim();
        String department = departmentSpinner.getSelectedItem().toString();
        String designation = designationSpinner.getSelectedItem().toString();

        // Validation
        if (TextUtils.isEmpty(fullName)) {
            fullNameInput.setError("Full Name is required");
            return;
        }
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Valid Email is required");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return;
        }
        if (TextUtils.isEmpty(collegeName)) {
            collegeNameInput.setError("College Name is required");
            return;
        }
        if (department.equals("Select")) {
            Toast.makeText(this, "Please select a department", Toast.LENGTH_SHORT).show();
            return;
        }
        if (designation.equals("Select Designation")) {
            Toast.makeText(this, "Please select a designation", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    Teacher teacher = new Teacher(userId, fullName, email, collegeName, department, designation);
                    databaseReference.child(userId).setValue(teacher).addOnCompleteListener(dbTask -> {
                        if (dbTask.isSuccessful()) {
                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, teacher_homepage.class);
                            intent.putExtra("TEACHER_NAME", fullName);
                            startActivity(intent);
                            clearInputs();
                        } else {
                            Toast.makeText(this, "Failed to save data: " + dbTask.getException(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Registration Failed: " + task.getException(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearInputs() {
        fullNameInput.setText("");
        emailInput.setText("");
        passwordInput.setText("");
        collegeNameInput.setText("");
        departmentSpinner.setSelection(0);
        designationSpinner.setSelection(0);
    }
}
