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

public class ViewCertificatesActivity extends AppCompatActivity {
    private LinearLayout certificateContainer;
    private DatabaseReference studentsRef;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_certificate);

        certificateContainer = findViewById(R.id.certificateContainer);

        // Get Firebase User
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            studentId = user.getUid();
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Firebase reference
        studentsRef = FirebaseDatabase.getInstance().getReference("Students")
                .child(studentId).child("Certificates");

        fetchCertificates();
    }

    private void fetchCertificates() {
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                certificateContainer.removeAllViews(); // Clear previous views

                for (DataSnapshot data : snapshot.getChildren()) {
                    CertificateModel certificate = data.getValue(CertificateModel.class);
                    if (certificate != null) {
                        certificate.setCertificateId(data.getKey()); // Needed for delete functionality
                        addCertificateToLayout(certificate);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
                Toast.makeText(ViewCertificatesActivity.this, "Failed to load certificates!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCertificateToLayout(CertificateModel certificate) {
        View certificateView = LayoutInflater.from(this).inflate(R.layout.item_certificate, certificateContainer, false);

        TextView certificateName = certificateView.findViewById(R.id.certificate_name);
        TextView issuedBy = certificateView.findViewById(R.id.issued_by);
        TextView issueDate = certificateView.findViewById(R.id.issue_date);
        TextView category = certificateView.findViewById(R.id.category);
        ImageView certificateImage = certificateView.findViewById(R.id.certificate_image);
        ImageView deleteButton = certificateView.findViewById(R.id.delete_certificate);
        ImageButton downloadButton = certificateView.findViewById(R.id.download_button);

        certificateName.setText(certificate.getName());
        issuedBy.setText("Issued By: " + certificate.getIssued_by());
        issueDate.setText("Date: " + certificate.getIssue_date());
        category.setText("Category: " + certificate.getCategory());

        // Load image with Glide
        Glide.with(this)
                .load(certificate.getCertificate_url())
                .placeholder(R.drawable.ic_launcher_background)
                .into(certificateImage);

        // Set delete listener
        deleteButton.setOnClickListener(v -> deleteCertificate(certificate.getCertificateId()));

        // Set download listener
        downloadButton.setOnClickListener(v -> downloadCertificate(certificate.getCertificate_url(), certificate.getName()));

        certificateContainer.addView(certificateView);
    }

    private void deleteCertificate(String certificateId) {
        DatabaseReference certRef = studentsRef.child(certificateId);
        certRef.removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(ViewCertificatesActivity.this, "Certificate deleted", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(ViewCertificatesActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
        });
    }

    private void downloadCertificate(String url, String name) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle("Downloading Certificate");
            request.setDescription("Downloading " + name);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name + "_certificate.jpg");

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
