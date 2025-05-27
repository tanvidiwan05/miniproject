package com.example.credify;

public class Teacher {
    public String userId, fullName, email, collegeName, department, designation;

    public Teacher() {
        // Required for Firebase
    }

    public Teacher(String userId, String fullName, String email, String collegeName, String department, String designation) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.collegeName = collegeName;
        this.department = department;
        this.designation = designation;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public String getDepartmentName() {
        return department;
    }

    public String getDesignation() {
        return designation;
    }
}
