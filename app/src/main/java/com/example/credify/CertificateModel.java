package com.example.credify;

public class CertificateModel {
    private String name;
    private String issued_by;
    private String issue_date;
    private String category;
    private String certificate_url;
    private String certificateId; // Firebase key
    private String verificationStatus = "pending"; // default

    // 🔹 Required no-arg constructor
    public CertificateModel() {}

    public CertificateModel(String name, String issued_by, String issue_date, String category, String certificate_url) {
        this.name = name;
        this.issued_by = issued_by;
        this.issue_date = issue_date;
        this.category = category;
        this.certificate_url = certificate_url;
        this.verificationStatus = "pending";
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

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
}
