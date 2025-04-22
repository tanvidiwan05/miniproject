package com.example.credify;

public class CertificateTeacher {
    public String workshop_title;
    public String duration;
    public String venue;
    public String sponsored_by;
    public String certificate_url;

    public CertificateTeacher() {}

    public CertificateTeacher(String workshop_title, String duration, String venue, String sponsored_by, String certificate_url) {
        this.workshop_title = workshop_title;
        this.duration = duration;
        this.venue = venue;
        this.sponsored_by = sponsored_by;
        this.certificate_url = certificate_url;
    }
}
