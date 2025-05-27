package com.example.credify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private Button logout;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_profile);

        profileImage = findViewById(R.id.profileImage);
        logout = findViewById(R.id.logout);

        auth = FirebaseAuth.getInstance();

        // Logout Button
        logout.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(AdminProfileActivity.this, "Logging Out", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(AdminProfileActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }
}
