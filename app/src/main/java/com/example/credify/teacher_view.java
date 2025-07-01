package com.example.credify;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
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
                        certificate.setCertificateId(data.getKey());
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
        ImageButton downloadButton = certificateView.findViewById(R.id.download_button);
        ImageView deleteButton = certificateView.findViewById(R.id.delete_certificate); // ✅ Properly initialized

        title.setText("Title: " + certificate.workshop_title);
        duration.setText("Duration: " + certificate.duration);
        venue.setText("Venue: " + certificate.venue);
        sponsoredBy.setText("Sponsored By: " + certificate.sponsored_by);


        TextView statusBadge = certificateView.findViewById(R.id.status_badge);

// Show status only, not clickable for students
        String status = certificate.getVerificationStatus();
        switch (status) {
            case "verified":
                statusBadge.setText("✅ Verified");
                break;
            case "rejected":
                statusBadge.setText("❌ Rejected");
                break;
            default:
                statusBadge.setText("⏳ Pending");
                break;
        }

        Glide.with(this)
                .load(certificate.certificate_url)
                .placeholder(R.drawable.ic_launcher_background)
                .into(certificateImage);

        downloadButton.setOnClickListener(v ->
                downloadImage(certificate.certificate_url, certificate.workshop_title));

        deleteButton.setOnClickListener(v -> deleteCertificate(certificate.getCertificateId()));

        certificateContainer.addView(certificateView);
    }

    private void deleteCertificate(String certificateId) {
        if (certificateId == null) {
            Toast.makeText(this, "Invalid certificate ID!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference certRef = teacherRef.child(certificateId);
        certRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Certificate deleted", Toast.LENGTH_SHORT).show();
                    fetchCertificates(); // refresh the UI after deletion
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Deletion error: " + e.getMessage());
                });
    }

    private void downloadImage(String imageUrl, String title) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
            request.setTitle("Downloading Certificate");
            request.setDescription("Downloading " + title);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title + "_certificate.jpg");

            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager != null) {
                manager.enqueue(request);
                Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Download manager not available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Download failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
