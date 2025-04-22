package com.example.credify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class teacher_profile extends AppCompatActivity {

    private ImageView profileImage;
    private TextView teacherName, teacherEmail, teacherCollege, teacherBranch;
    private Button logout;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        // Initialize UI Components
        logout = findViewById(R.id.logout);
        profileImage = findViewById(R.id.profileImage);
        teacherName = findViewById(R.id.teacherName);
        teacherEmail = findViewById(R.id.teacherEmail);
        teacherCollege = findViewById(R.id.teacherCollege);   // same as studentCollege
        teacherBranch = findViewById(R.id.teacherBranch);    // same as studentBranch

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Teachers");

        // Fetch Teacher Info
        if (user != null) {
            loadTeacherData(user.getUid());
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Logout Button
        logout.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(teacher_profile.this, "Logging Out", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(teacher_profile.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void loadTeacherData(String userId) {
        databaseReference.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DataSnapshot snapshot = task.getResult();

                // Debug Log
                System.out.println("Teacher Snapshot: " + snapshot.getValue());

                String name = snapshot.hasChild("fullName") ? snapshot.child("fullName").getValue(String.class) : "N/A";
                String email = snapshot.hasChild("email") ? snapshot.child("email").getValue(String.class) : "N/A";
                String college = snapshot.hasChild("collegeName") ? snapshot.child("collegeName").getValue(String.class) : "N/A";
                String branch = snapshot.hasChild("department") ? snapshot.child("department").getValue(String.class) : "N/A";

                teacherName.setText(name);
                teacherEmail.setText("Email: " + email);
                teacherCollege.setText("College: " + college);
                teacherBranch.setText("Branch: " + branch);
            } else {
                Toast.makeText(this, "Failed to load teacher data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
