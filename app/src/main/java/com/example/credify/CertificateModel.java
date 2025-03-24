package com.example.credify;

public class CertificateModel {
    private String name, issued_by, issue_date, category, certificate_url;

    public CertificateModel() {
        // Default constructor required for Firebase
    }

    public CertificateModel(String name, String issued_by, String issue_date, String category, String certificate_url) {
        this.name = name;
        this.issued_by = issued_by;
        this.issue_date = issue_date;
        this.category = category;
        this.certificate_url = certificate_url;
    }

    public String getName() {
        return name;
    }

    public String getIssued_by() {
        return issued_by;
    }

    public String getIssue_date() {
        return issue_date;
    }

    public String getCategory() {
        return category;
    }

    public String getCertificate_url() {
        return certificate_url;
    }
}
