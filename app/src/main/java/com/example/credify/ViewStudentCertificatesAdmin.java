package com.example.credify;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.*;

public class ViewStudentCertificatesAdmin extends AppCompatActivity {
    private LinearLayout certificateContainer;
    private DatabaseReference studentsRef;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_student_certificate_admin);

        certificateContainer = findViewById(R.id.certificateContainer);

        Intent intent = getIntent();
        studentId = intent.getStringExtra("studentId");

        if (studentId == null || studentId.isEmpty()) {
            Toast.makeText(this, "Student ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        studentsRef = FirebaseDatabase.getInstance().getReference("Students")
                .child(studentId).child("Certificates");

        fetchCertificates();
    }

    private void fetchCertificates() {
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                certificateContainer.removeAllViews();

                if (!snapshot.exists()) {
                    Toast.makeText(ViewStudentCertificatesAdmin.this, "No certificates found for this student.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot data : snapshot.getChildren()) {
                    CertificateModel certificate = data.getValue(CertificateModel.class);
                    if (certificate != null) {
                        certificate.setCertificateId(data.getKey()); // Set the certificate ID for deletion
                        addCertificateToLayout(certificate);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
                Toast.makeText(ViewStudentCertificatesAdmin.this, "Failed to load certificates!", Toast.LENGTH_SHORT).show();
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
        ImageView deleteButton = certificateView.findViewById(R.id.delete_certificate); // Add delete button reference
        ImageButton downloadButton = certificateView.findViewById(R.id.download_button);

        certificateName.setText(certificate.getName());
        issuedBy.setText("Issued By: " + certificate.getIssued_by());
        issueDate.setText("Date: " + certificate.getIssue_date());
        category.setText("Category: " + certificate.getCategory());


        if (!isDestroyed() && !isFinishing()) {
            Glide.with(this)
                    .load(certificate.getCertificate_url())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(certificateImage);
        }

        // Set delete button click listener
        deleteButton.setOnClickListener(v -> deleteCertificate(certificate.getCertificateId()));

        // Set download button click listener
        downloadButton.setOnClickListener(v -> downloadImage(certificate.getCertificate_url(), certificate.getName()));

        certificateContainer.addView(certificateView);
        TextView statusBadge = certificateView.findViewById(R.id.status_badge);

// Set badge text based on status
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

// Make badge clickable only if status is pending
        if ("pending".equalsIgnoreCase(status)) {
            statusBadge.setOnClickListener(v -> showStatusPopup(v, certificate.getCertificateId()));
        }



    }

    private void showStatusPopup(View anchor, String certId) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.status_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            String newStatus = "";
            if (item.getItemId() == R.id.action_verify) {
                newStatus = "verified";
            } else if (item.getItemId() == R.id.action_reject) {
                newStatus = "rejected";
            }

            if (!newStatus.isEmpty()) {
                studentsRef.child(certId).child("verificationStatus").setValue(newStatus)
                        .addOnSuccessListener(unused -> Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show());
            }

            return true;
        });

        popup.show();
    }


    private void deleteCertificate(String certificateId) {
        DatabaseReference certRef = studentsRef.child(certificateId);
        certRef.removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(ViewStudentCertificatesAdmin.this, "Certificate deleted", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(ViewStudentCertificatesAdmin.this, "Failed to delete", Toast.LENGTH_SHORT).show();
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
