package com.example.credify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentHomepage extends AppCompatActivity {

    CardView uploadCertificate,viewcertifiacte;
    TextView welcomeText;
    ImageView profile;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_homepage);

        uploadCertificate = findViewById(R.id.upload_certificate_card);
        viewcertifiacte = findViewById(R.id.view_certificate_card);
        welcomeText = findViewById(R.id.welcomeText);
        profile = findViewById(R.id.profileImage);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Students").child(userId);

            // Fetch user name from Firebase
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("fullName").getValue(String.class);

                        if (name != null && !name.isEmpty()) {
                            welcomeText.setText("Welcome, " + name + "!");
                        } else {
                            welcomeText.setText("Welcome, User!");
                        }
                    } else {
                        welcomeText.setText("Welcome, Guest!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(StudentHomepage.this, "Failed to fetch name!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            welcomeText.setText("Welcome, Guest!");
        }


//        profile section
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentHomepage.this, profile.class);
                startActivity(intent);
            }
        });

        uploadCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentHomepage.this, UploadCertificateActivity.class);
                startActivity(intent);
            }
        });
        viewcertifiacte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentHomepage.this, ViewCertificatesActivity.class);
                startActivity(intent);
            }
        });

    }
}
