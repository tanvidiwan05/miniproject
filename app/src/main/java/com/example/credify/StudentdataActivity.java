package com.example.credify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentdataActivity extends AppCompatActivity {

    private LinearLayout studentContainer;
    private Spinner branchSpinner, yearSpinner;
    private DatabaseReference studentsRef;
    private List<Student> allStudents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_studentdata);

        studentContainer = findViewById(R.id.StudentContainer);
        branchSpinner = findViewById(R.id.branchSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);

        studentsRef = FirebaseDatabase.getInstance().getReference("Students");

        setupSpinners();
        fetchStudents();
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> branchAdapter = ArrayAdapter.createFromResource(
                this, R.array.branch_names, android.R.layout.simple_spinner_item);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchSpinner.setAdapter(branchAdapter);

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(
                this, R.array.years, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        branchSpinner.setOnItemSelectedListener(new SpinnerSelectionListener());
        yearSpinner.setOnItemSelectedListener(new SpinnerSelectionListener());
    }

    private void fetchStudents() {
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allStudents.clear();
                studentContainer.removeAllViews();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Student student = data.getValue(Student.class);
                    if (student != null) {
                        allStudents.add(student);
                    }
                }

                applyFilters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
                Toast.makeText(StudentdataActivity.this, "Failed to load student list!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        String selectedBranch = branchSpinner.getSelectedItem().toString();
        String selectedYear = yearSpinner.getSelectedItem().toString();

        studentContainer.removeAllViews();
        boolean anyMatch = false;

        for (Student student : allStudents) {
            boolean matchesBranch = selectedBranch.equals("Select") ||
                    (student.getBranchName() != null && student.getBranchName().equalsIgnoreCase(selectedBranch));
            boolean matchesYear = selectedYear.equals("Select") ||
                    (student.getYear() != null && student.getYear().equalsIgnoreCase(selectedYear));

            if (matchesBranch && matchesYear) {
                addStudentToLayout(student);
                anyMatch = true;
            }
        }

        // Optional: Show a message if no students match
        if (!anyMatch) {
            TextView noResult = new TextView(this);
            noResult.setText("No students found.");
            studentContainer.addView(noResult);
        }
    }

    private void addStudentToLayout(Student student) {
        View studentView = LayoutInflater.from(this).inflate(R.layout.student_detail, studentContainer, false);

        TextView nameText = studentView.findViewById(R.id.Student_name);
        TextView prnText = studentView.findViewById(R.id.PRN);
        TextView yearText = studentView.findViewById(R.id.Year);
        TextView branchText = studentView.findViewById(R.id.Branch);
        View viewButton = studentView.findViewById(R.id.viewButton);

        nameText.setText(student.getFullName());
        prnText.setText("PRN: " + student.getId());
        yearText.setText("Year: " + student.getYear());
        branchText.setText("Branch: " + student.getBranchName());

        viewButton.setOnClickListener(v -> {
            if (student.getId() == null || student.getId().isEmpty()) {
                Toast.makeText(StudentdataActivity.this, "Invalid student ID!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(StudentdataActivity.this, ViewStudentCertificatesAdmin.class);
            intent.putExtra("studentId", student.getId());
            startActivity(intent);
        });

        studentContainer.addView(studentView);
    }

    private class SpinnerSelectionListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
            applyFilters();
        }

        @Override
        public void onNothingSelected(android.widget.AdapterView<?> parent) {
            // Do nothing
        }
    }
}
