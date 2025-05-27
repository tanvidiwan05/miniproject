package com.example.credify;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import okhttp3.*;

public class teacher_upload extends AppCompatActivity {

    EditText workshopTitle, duration, venue, sponsoredBy;
    Spinner typeSpinner;
    TextView fromDate, toDate;
    ImageView certificateImage;
    Button uploadImageButton, submitButton;

    Uri imageUri;
    String teacherId;

    FirebaseDatabase database;
    DatabaseReference teacherRef;

    private final String IMGBB_API_KEY = "bbcf4559e21894aee84a1e72b3d04a59";

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
        setContentView(R.layout.activity_teacher_upload); // Change to your actual layout name

        workshopTitle = findViewById(R.id.workshop_title);
        duration = findViewById(R.id.duration);
        venue = findViewById(R.id.venue);
        sponsoredBy = findViewById(R.id.sponsored_by);
        typeSpinner = findViewById(R.id.type_spinner);
        fromDate = findViewById(R.id.from_date);
        toDate = findViewById(R.id.to_date);
        certificateImage = findViewById(R.id.certificate_image);
        uploadImageButton = findViewById(R.id.upload_image_button);
        submitButton = findViewById(R.id.submit_certificate_button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                 R.array.certificate_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        teacherRef = database.getReference("Teachers");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            teacherId = user.getUid();
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fromDate.setOnClickListener(v -> pickDate(fromDate));
        toDate.setOnClickListener(v -> pickDate(toDate));

        uploadImageButton.setOnClickListener(v -> openImageChooser());
        submitButton.setOnClickListener(v -> submitCertificate());
    }

    private void pickDate(TextView textView) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> textView.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void submitCertificate() {
        String title = workshopTitle.getText().toString().trim();
        String durationText = duration.getText().toString().trim();
        String type = typeSpinner.getSelectedItem().toString();
        String from = fromDate.getText().toString().trim();
        String to = toDate.getText().toString().trim();
        String venueText = venue.getText().toString().trim();
        String sponsored = sponsoredBy.getText().toString().trim();

        if (title.isEmpty() || durationText.isEmpty() || type.isEmpty() ||
                from.isEmpty() || to.isEmpty() || venueText.isEmpty() || sponsored.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImageToImgBB(imageUri, title, durationText, type, from, to, venueText, sponsored);
    }

    private void uploadImageToImgBB(Uri imageUri, String title, String duration, String type, String from, String to, String venue, String sponsoredBy) {
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
                    runOnUiThread(() -> Toast.makeText(teacher_upload.this, "Image upload failed!", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseData = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseData);
                            String imageUrl = jsonResponse.getJSONObject("data").getString("url");

                            storeTeacherCertificate(title, duration, type, from, to, venue, sponsoredBy, imageUrl);
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

    private void storeTeacherCertificate(String title, String duration, String type, String from, String to, String venue, String sponsoredBy, String imageUrl) {
        Map<String, Object> certData = new HashMap<>();
        certData.put("workshop_title", title);
        certData.put("duration", duration);
        certData.put("type", type);
        certData.put("from_date", from);
        certData.put("to_date", to);
        certData.put("venue", venue);
        certData.put("sponsored_by", sponsoredBy);
        certData.put("certificate_url", imageUrl);

        teacherRef.child(teacherId).child("Certificates").push().setValue(certData)
                .addOnSuccessListener(aVoid -> runOnUiThread(() -> {
                    Toast.makeText(teacher_upload.this, "Certificate uploaded successfully!", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> runOnUiThread(() -> {
                    Toast.makeText(teacher_upload.this, "Failed to upload certificate", Toast.LENGTH_SHORT).show();
                }));
    }
}
