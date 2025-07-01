package com.example.credify;

public class CertificateTeacher {
    public String workshop_title;
    public String duration;
    public String venue;
    public String sponsored_by;
    public String certificate_url;
    private String certificateId;
    private String verificationStatus = "pending"; // Default status

    // Default constructor (required for Firebase)
    public CertificateTeacher() {}

    // Constructor without status (for initial save)
    public CertificateTeacher(String workshop_title, String duration, String venue, String sponsored_by, String certificate_url, String certificateId) {
        this.workshop_title = workshop_title;
        this.duration = duration;
        this.venue = venue;
        this.sponsored_by = sponsored_by;
        this.certificate_url = certificate_url;
        this.certificateId = certificateId;
        this.verificationStatus = "pending";
    }

    // Getter and Setter for certificateId
    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    // Getter and Setter for verificationStatus
    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    @Override
    public String toString() {
        return "CertificateTeacher{" +
                "workshop_title='" + workshop_title + '\'' +
                ", duration='" + duration + '\'' +
                ", venue='" + venue + '\'' +
                ", sponsored_by='" + sponsored_by + '\'' +
                ", certificate_url='" + certificate_url + '\'' +
                ", certificateId='" + certificateId + '\'' +
                ", verificationStatus='" + verificationStatus + '\'' +
                '}';
    }
}
