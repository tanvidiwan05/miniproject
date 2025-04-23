package com.example.credify;  // Adjust the package name if needed

public class Certificate {
    public String certificateName;
    public String issuedBy;
    public String issueDate;
    public String category;
    public String imageUrl;

    // Default constructor (MANDATORY for Firebase)
    public Certificate() {}

    // Parameterized constructor
    public Certificate(String certificateName, String issuedBy, String issueDate, String category, String imageUrl) {
        this.certificateName = certificateName;
        this.issuedBy = issuedBy;
        this.issueDate = issueDate;
        this.category = category;
        this.imageUrl = imageUrl;
    }
}
