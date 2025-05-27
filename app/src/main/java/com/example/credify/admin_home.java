package com.example.credify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import androidx.appcompat.app.AppCompatActivity;

public class admin_home extends AppCompatActivity {

    private CardView facultyCard, studentCard;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_home);

        facultyCard = findViewById(R.id.faculty_card);
        studentCard = findViewById(R.id.student_card);
        profileImage = findViewById(R.id.profileImage);

        facultyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin_home.this, FacultydataActivity.class);
                startActivity(intent);
            }
        });

        studentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin_home.this, StudentdataActivity.class);
                startActivity(intent);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(admin_home.this, AdminProfileActivity.class));
            }
        });
    }
}



