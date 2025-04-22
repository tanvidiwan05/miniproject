package com.example.credify;

public class Teacher {
    public String userId, fullName, email, collegeName, department;

    public Teacher() {
        // Default constructor required for calls to DataSnapshot.getValue(Teacher.class)
    }

    public Teacher(String userId, String fullName, String email, String collegeName, String department) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.collegeName = collegeName;
        this.department = department;
    }
}
