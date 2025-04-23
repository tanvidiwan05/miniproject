package com.example.credify;

public class Student {
    private String id, fullName, email, collegeName, branchName, year;

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

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getYear() {
        return year;
    }
}
