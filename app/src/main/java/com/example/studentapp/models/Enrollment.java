package com.example.studentapp.models;

import java.util.List;

public class Enrollment {
    private String userId;
    private List<String> enrolledSubjects;
    private int totalCredits;
    private long enrollmentDate;

    public Enrollment() {
        // Required empty constructor for Firebase
    }

    public Enrollment(String userId, List<String> enrolledSubjects, int totalCredits) {
        this.userId = userId;
        this.enrolledSubjects = enrolledSubjects;
        this.totalCredits = totalCredits;
        this.enrollmentDate = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<String> getEnrolledSubjects() { return enrolledSubjects; }
    public void setEnrolledSubjects(List<String> enrolledSubjects) { this.enrolledSubjects = enrolledSubjects; }

    public int getTotalCredits() { return totalCredits; }
    public void setTotalCredits(int totalCredits) { this.totalCredits = totalCredits; }

    public long getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(long enrollmentDate) { this.enrollmentDate = enrollmentDate; }
}
