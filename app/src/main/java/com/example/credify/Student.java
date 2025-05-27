package com.example.credify;


import androidx.annotation.Keep;

@Keep
public class Student {
    private String id;
    private String fullName;
    private String email;
    private String collegeName;
    private String branchName;
    private String year;

    // Default constructor required for calls to DataSnapshot.getValue(Student.class)
    public Student() {}

    public Student(String id, String fullName, String email, String collegeName, String branchName, String year) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.collegeName = collegeName;
        this.branchName = branchName;
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}