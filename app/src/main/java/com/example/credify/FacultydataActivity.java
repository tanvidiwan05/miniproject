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

public class FacultydataActivity extends AppCompatActivity {
    private LinearLayout facultyContainer;
    private Spinner departmentSpinner, designationSpinner;
    private DatabaseReference facultyRef;
    private List<Teacher> allTeachers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_facultydata);

        facultyContainer = findViewById(R.id.FacultyContainer);
        departmentSpinner = findViewById(R.id.departmentSpinner);
        designationSpinner = findViewById(R.id.designationSpinner);

        facultyRef = FirebaseDatabase.getInstance().getReference("Teachers");

        setupSpinners();
        fetchFaculty();
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(
                this, R.array.branch_names, android.R.layout.simple_spinner_item);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(departmentAdapter);

        ArrayAdapter<CharSequence> designationAdapter = ArrayAdapter.createFromResource(
                this, R.array.designations_array, android.R.layout.simple_spinner_item);
        designationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        designationSpinner.setAdapter(designationAdapter);

        departmentSpinner.setOnItemSelectedListener(new SpinnerSelectionListener());
        designationSpinner.setOnItemSelectedListener(new SpinnerSelectionListener());
    }

    private void fetchFaculty() {
        facultyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allTeachers.clear();
                facultyContainer.removeAllViews();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Teacher teacher = data.getValue(Teacher.class);
                    if (teacher != null) {
                        allTeachers.add(teacher);
                    }
                }

                applyFilters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
                Toast.makeText(FacultydataActivity.this, "Failed to load faculty list!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        String selectedDepartment = departmentSpinner.getSelectedItem().toString();
        String selectedDesignation = designationSpinner.getSelectedItem().toString();

        facultyContainer.removeAllViews();

        for (Teacher teacher : allTeachers) {
            boolean matchesDepartment = selectedDepartment.equals("Select") ||
                    (teacher.getDepartmentName() != null && teacher.getDepartmentName().equalsIgnoreCase(selectedDepartment));

            boolean matchesDesignation = selectedDesignation.equals("Select Designation") ||
                    (teacher.getDesignation() != null &&
                            teacher.getDesignation().equalsIgnoreCase(selectedDesignation));

            if (matchesDepartment && matchesDesignation) {
                addFacultyToLayout(teacher);
            }
        }
    }

    private void addFacultyToLayout(Teacher teacher) {
        View facultyView = LayoutInflater.from(this).inflate(R.layout.faculty_detail, facultyContainer, false);

        TextView nameText = facultyView.findViewById(R.id.Faculty_name);
        TextView branchText = facultyView.findViewById(R.id.Department);
        TextView designationText = facultyView.findViewById(R.id.Designation);
        View viewButton = facultyView.findViewById(R.id.viewButton);

        nameText.setText(teacher.getFullName());
        branchText.setText("Department: " + teacher.getDepartmentName());
        designationText.setText("Designation: " + (teacher.getDesignation() != null ? teacher.getDesignation() : "N/A"));

        viewButton.setOnClickListener(v -> {
            if (teacher.getUserId() == null || teacher.getUserId().isEmpty()) {
                Toast.makeText(FacultydataActivity.this, "Invalid faculty ID!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(FacultydataActivity.this, ViewFacultyCertificatesAdmin.class);
            intent.putExtra("facultyId", teacher.getUserId());
            startActivity(intent);
        });

        facultyContainer.addView(facultyView);
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
