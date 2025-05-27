package com.example.credify;

public class studentList {

    private String name;
    private String prn;
    private String year;
    private String branch;

    public studentList() {
        // Needed for Firebase
    }

    public studentList(String name, String prn, String year, String branch) {
        this.name = name;
        this.prn = prn;
        this.year = year;
        this.branch = branch;
    }

    public String getName() { return name; }
    public String getPrn() { return prn; }
    public String getYear() { return year; }
    public String getBranch() { return branch; }


}



