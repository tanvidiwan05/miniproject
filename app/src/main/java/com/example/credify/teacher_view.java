package com.example.credify;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class teacher_view extends AppCompatActivity {

    private LinearLayout certificateContainer;
    private DatabaseReference teacherRef;
    private String teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_view);

        certificateContainer = findViewById(R.id.certificateContainer);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            teacherId = user.getUid();
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        teacherRef = FirebaseDatabase.getInstance().getReference("Teachers")
                .child(teacherId).child("Certificates");

        fetchCertificates();
    }

    private void fetchCertificates() {
        teacherRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                certificateContainer.removeAllViews();

                for (DataSnapshot data : snapshot.getChildren()) {
                    CertificateTeacher certificate = data.getValue(CertificateTeacher.class);
                    if (certificate != null) {
                        addCertificateToLayout(certificate);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
                Toast.makeText(teacher_view.this, "Failed to load certificates!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCertificateToLayout(CertificateTeacher certificate) {
        View certificateView = LayoutInflater.from(this).inflate(R.layout.item_certificate_teacher, certificateContainer, false);

        TextView title = certificateView.findViewById(R.id.title_workshop);
        TextView duration = certificateView.findViewById(R.id.duration);
        TextView venue = certificateView.findViewById(R.id.venue);
        TextView sponsoredBy = certificateView.findViewById(R.id.sponsored_by);
        ImageView certificateImage = certificateView.findViewById(R.id.certificate_image);

        title.setText("Title: " + certificate.workshop_title);
        duration.setText("Duration: " + certificate.duration);
        venue.setText("Venue: " + certificate.venue);
        sponsoredBy.setText("Sponsored By: " + certificate.sponsored_by);

        Glide.with(this)
                .load(certificate.certificate_url)
                .placeholder(R.drawable.ic_launcher_background)
                .into(certificateImage);

        certificateContainer.addView(certificateView);
    }

}
