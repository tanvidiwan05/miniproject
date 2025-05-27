package com.example.credify;

public class studentlistmodel {
    private String name;
    private String prn;
    private String year;
    private String branch;

    // Default constructor required for Firebase
    public studentlistmodel() {}

    // Parameterized constructor
    public studentlistmodel(String name, String prn, String year, String branch) {
        this.name = name;
        this.prn = prn;
        this.year = year;
        this.branch = branch;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getPrn() {
        return prn;
    }

    public String getYear() {
        return year;
    }

    public String getBranch() {
        return branch;
    }
}
