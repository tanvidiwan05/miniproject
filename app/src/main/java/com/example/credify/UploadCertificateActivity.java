package com.example.credify;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadCertificateActivity extends AppCompatActivity {
    EditText certificateName, issuedBy, issueDate;
    RadioGroup categoryGroup;
    ImageView certificateImage;
    Button uploadImageButton, submitButton;
    Uri imageUri;

    FirebaseDatabase database;
    DatabaseReference studentsRef;
    String studentId;

    private final String IMGBB_API_KEY = "bbcf4559e21894aee84a1e72b3d04a59";  //Replace with your ImgBB API Key

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        certificateImage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_certificate);

        // Initialize UI elements
        certificateName = findViewById(R.id.certificate_name);
        issuedBy = findViewById(R.id.issued_by);
        issueDate = findViewById(R.id.issue_date);
        categoryGroup = findViewById(R.id.category_group);
        certificateImage = findViewById(R.id.certificate_image);
        uploadImageButton = findViewById(R.id.upload_image_button);
        submitButton = findViewById(R.id.submit_certificate_button);

        // Firebase Initialization
        database = FirebaseDatabase.getInstance();
        studentsRef = database.getReference("Students");

        // Get the current user ID from Firebase Authentication
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            studentId = user.getUid();
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Open Image Picker on button click
        uploadImageButton.setOnClickListener(v -> openImageChooser());

        // Submit Certificate
        submitButton.setOnClickListener(v -> submitCertificate());
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void submitCertificate() {
        String name = certificateName.getText().toString().trim();
        String issuer = issuedBy.getText().toString().trim();
        String date = issueDate.getText().toString().trim();
        int selectedId = categoryGroup.getCheckedRadioButtonId();

        if (name.isEmpty() || issuer.isEmpty() || date.isEmpty() || selectedId == -1 || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedCategory = findViewById(selectedId);
        String category = selectedCategory.getText().toString();

        // Upload image to ImgBB and get URL
        uploadImageToImgBB(imageUri, name, issuer, date, category);
    }

    private void uploadImageToImgBB(Uri imageUri, String name, String issuer, String date, String category) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String base64Image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            String uploadUrl = "https://api.imgbb.com/1/upload?key=" + IMGBB_API_KEY;

            RequestBody requestBody = new FormBody.Builder()
                    .add("image", base64Image)
                    .build();

            Request request = new Request.Builder()
                    .url(uploadUrl)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(UploadCertificateActivity.this, "Image upload failed!", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseData = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseData);
                            String imageUrl = jsonResponse.getJSONObject("data").getString("url");

                            // Store image URL and certificate details in Firebase
                            storeCertificateInFirebase(name, issuer, date, category, imageUrl);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void storeCertificateInFirebase(String name, String issuer, String date, String category, String imageUrl) {
        Map<String, Object> certificateData = new HashMap<>();
        certificateData.put("name", name);
        certificateData.put("issued_by", issuer);
        certificateData.put("issue_date", date);
        certificateData.put("category", category);
        certificateData.put("certificate_url", imageUrl);

        studentsRef.child(studentId).child("Certificates").push().setValue(certificateData)
                .addOnSuccessListener(aVoid -> runOnUiThread(() -> {
                    Toast.makeText(UploadCertificateActivity.this, "Certificate uploaded successfully!", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> runOnUiThread(() -> {
                    Toast.makeText(UploadCertificateActivity.this, "Failed to upload certificate", Toast.LENGTH_SHORT).show();
                }));
    }
}
