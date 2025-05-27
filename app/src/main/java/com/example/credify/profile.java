package com.example.credify;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class profile extends AppCompatActivity {

    private ImageView profileImage;
    private TextView studentName, studentEmail, studentCollege, studentBranch;
    private Button logout;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI Components
        logout = findViewById(R.id.logout);
        profileImage = findViewById(R.id.profileImage);
        studentName = findViewById(R.id.studentName);
        studentEmail = findViewById(R.id.studentEmail);
        studentCollege = findViewById(R.id.studentCollege);
        studentBranch = findViewById(R.id.studentBranch);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Students");

        // Fetch Student Info
        if (user != null) {
            loadStudentData(user.getUid());
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Logout Button Click Listener
        logout.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(profile.this, "Logging Out", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(profile.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void loadStudentData(String userId) {
        databaseReference.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DataSnapshot snapshot = task.getResult();

                // Debug: Print snapshot data
                System.out.println("Snapshot Data: " + snapshot.getValue());

                // Fetch Data (Updated Keys)
                String name = snapshot.hasChild("fullName") ? snapshot.child("fullName").getValue(String.class) : "N/A";
                String email = snapshot.hasChild("email") ? snapshot.child("email").getValue(String.class) : "N/A";
                String college = snapshot.hasChild("collegeName") ? snapshot.child("collegeName").getValue(String.class) : "N/A";
                String branch = snapshot.hasChild("branchName") ? snapshot.child("branchName").getValue(String.class) : "N/A";

                // Set Data to Views
                studentName.setText(name);
                studentEmail.setText("Email: " + email);
                studentCollege.setText("College: " + college);
                studentBranch.setText("Branch: " + branch);

            } else {
                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
