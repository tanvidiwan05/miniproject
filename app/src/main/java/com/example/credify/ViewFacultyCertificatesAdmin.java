package com.example.credify;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.firebase.database.*;

public class ViewFacultyCertificatesAdmin extends AppCompatActivity {

    private LinearLayout certificateContainer;
    private DatabaseReference facultyRef;
    private String facultyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_faculty_certificate_admin);

        certificateContainer = findViewById(R.id.certificateContainer);
        facultyId = getIntent().getStringExtra("facultyId");

        if (facultyId == null || facultyId.isEmpty()) {
            Toast.makeText(this, "Faculty ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        facultyRef = FirebaseDatabase.getInstance().getReference("Teachers")
                .child(facultyId).child("Certificates");

        fetchCertificates();
    }

    private void fetchCertificates() {
        facultyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                certificateContainer.removeAllViews();

                if (!snapshot.exists()) {
                    Toast.makeText(ViewFacultyCertificatesAdmin.this, "No certificates found for this faculty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot data : snapshot.getChildren()) {
                    CertificateTeacher cert = data.getValue(CertificateTeacher.class);
                    if (cert != null) {
                        cert.setCertificateId(data.getKey()); // Set certificate ID for deletion
                        addCertificateToLayout(cert);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFacultyCertificatesAdmin.this, "Failed to load certificates!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCertificateToLayout(CertificateTeacher cert) {
        View certView = LayoutInflater.from(this).inflate(R.layout.item_certificate_teacher, certificateContainer, false);

        TextView title = certView.findViewById(R.id.title_workshop);
        TextView duration = certView.findViewById(R.id.duration);
        TextView venue = certView.findViewById(R.id.venue);
        TextView sponsored = certView.findViewById(R.id.sponsored_by);
        ImageView image = certView.findViewById(R.id.certificate_image);
        ImageButton downloadButton = certView.findViewById(R.id.download_button);
        ImageView deleteButton = certView.findViewById(R.id.delete_certificate); // Add delete button reference

        title.setText(cert.workshop_title);
        duration.setText("Duration: " + cert.duration);
        venue.setText("Venue: " + cert.venue);
        sponsored.setText("Sponsored by: " + cert.sponsored_by);

        Glide.with(this)
                .load(cert.certificate_url)
                .placeholder(R.drawable.ic_launcher_background)
                .into(image);

        // Set download button click listener
        downloadButton.setOnClickListener(v -> downloadImage(cert.certificate_url, cert.workshop_title));

        // Set delete button click listener
        deleteButton.setOnClickListener(v -> deleteCertificate(cert.getCertificateId()));

        certificateContainer.addView(certView);
    }

    private void deleteCertificate(String certificateId) {
        DatabaseReference certRef = facultyRef.child(certificateId);
        certRef.removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(ViewFacultyCertificatesAdmin.this, "Certificate deleted", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(ViewFacultyCertificatesAdmin.this, "Failed to delete", Toast.LENGTH_SHORT).show();
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
