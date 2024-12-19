package com.example.studentapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.studentapp.adapters.SubjectAdapter;
import com.example.studentapp.models.Enrollment;
import com.example.studentapp.models.Student;
import com.example.studentapp.models.Subject;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentActivity extends AppCompatActivity {
    private static final int MAX_CREDITS = 24;
    private static final int MIN_CREDITS = 12;
    private RecyclerView subjectsRecyclerView;
    private MaterialButton enrollButton;
    private TextView creditsInfoText;
    private TextView headerText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<Subject> subjects;
    private List<Subject> selectedSubjects;
    private SubjectAdapter adapter;
    private int totalCredits = 0;
    private boolean isEnrolled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        subjectsRecyclerView = findViewById(R.id.subjectsRecyclerView);
        enrollButton = findViewById(R.id.enrollButton);
        creditsInfoText = findViewById(R.id.creditsInfoText);
        headerText = findViewById(R.id.headerText);

        // Initialize lists
        subjects = new ArrayList<>();
        selectedSubjects = new ArrayList<>();

        // Set up RecyclerView
        adapter = new SubjectAdapter(subjects, (subject, isSelected) -> {
            if (isEnrolled) {  // Don't allow changes if already enrolled
                return false;
            }
            
            if (isSelected) {
                if (totalCredits + subject.getCredits() <= MAX_CREDITS) {
                    selectedSubjects.add(subject);
                    totalCredits += subject.getCredits();
                    updateCreditsInfo();
                    return true;
                } else {
                    Toast.makeText(this, "Maximum credits exceeded!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                selectedSubjects.remove(subject);
                totalCredits -= subject.getCredits();
                updateCreditsInfo();
                return true;
            }
        });
        subjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        subjectsRecyclerView.setAdapter(adapter);

        // Initialize sample subjects if needed
        initializeSampleSubjects();
        
        // Check enrollment status and load appropriate data
        checkEnrollmentStatus();

        // Set up enrollment button
        enrollButton.setOnClickListener(v -> handleEnrollment(selectedSubjects));
    }

    private void initializeSampleSubjects() {
        // Check if subjects collection exists
        db.collection("subjects").get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots.isEmpty()) {
                    // Add sample subjects
                    List<Subject> sampleSubjects = new ArrayList<>();
                    sampleSubjects.add(new Subject("MC101", "Microcontroller Programming", 3, "Embedded Systems"));
                    sampleSubjects.add(new Subject("IOT101", "IoT Programming", 3, "IoT"));
                    sampleSubjects.add(new Subject("ES101", "Embedded System Design", 4, "Embedded Systems"));
                    sampleSubjects.add(new Subject("ROB101", "Introduction to Robotics", 4, "Robotics"));
                    sampleSubjects.add(new Subject("AI101", "Artificial Intelligence", 3, "Computer Science"));
                    sampleSubjects.add(new Subject("ML101", "Machine Learning", 4, "Computer Science"));
                    sampleSubjects.add(new Subject("DF101", "Digital Forensics", 3, "Security"));
                    sampleSubjects.add(new Subject("IPR101", "Image Processing and Recognition", 3, "AI"));

                    // Add each subject to Firestore
                    for (Subject subject : sampleSubjects) {
                        db.collection("subjects").document(subject.getId())
                            .set(subject)
                            .addOnFailureListener(e -> 
                                Log.e("EnrollmentActivity", "Error adding subject: " + e.getMessage()));
                    }
                }
            });
    }

    private void checkEnrollmentStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("students").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Student student = documentSnapshot.toObject(Student.class);
                            if (student != null && student.getEnrollment() != null) {
                                // Student is already enrolled
                                isEnrolled = true;
                                headerText.setText("Enrolled Subjects");
                                enrollButton.setVisibility(View.GONE);
                                creditsInfoText.setText(String.format("Total Credits: %d", 
                                    student.getEnrollment().getTotalCredits()));
                                
                                // Load only enrolled subjects
                                loadEnrolledSubjects(student.getEnrollment().getEnrolledSubjects());
                            } else {
                                // Student is not enrolled
                                loadAllSubjects();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error checking enrollment: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadEnrolledSubjects(List<String> enrolledSubjectIds) {
        db.collection("subjects")
                .whereIn("id", enrolledSubjectIds)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    subjects.clear();
                    for (Subject subject : queryDocumentSnapshots.toObjects(Subject.class)) {
                        subjects.add(subject);
                        selectedSubjects.add(subject);  // Mark all subjects as selected
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading enrolled subjects: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
    }

    private void loadAllSubjects() {
        db.collection("subjects")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    subjects.clear();
                    subjects.addAll(queryDocumentSnapshots.toObjects(Subject.class));
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading subjects: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
    }

    private void updateCreditsInfo() {
        if (!isEnrolled) {
            creditsInfoText.setText(String.format("Selected Credits: %d/%d (Min: %d)", 
                totalCredits, MAX_CREDITS, MIN_CREDITS));
        }
    }

    private void handleEnrollment(List<Subject> selectedSubjects) {
        if (isEnrolled) {
            Toast.makeText(this, "You are already enrolled", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedSubjects.isEmpty()) {
            Toast.makeText(this, "Please select at least one subject", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate total credits
        int totalCredits = 0;
        List<String> subjectIds = new ArrayList<>();
        for (Subject subject : selectedSubjects) {
            totalCredits += subject.getCredits();
            subjectIds.add(subject.getId());
        }

        // Check minimum credits requirement
        if (totalCredits < MIN_CREDITS) {
            Toast.makeText(this, "Please select subjects with at least " + MIN_CREDITS + " credits", 
                Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(currentUser.getUid());
        enrollment.setEnrolledSubjects(subjectIds);
        enrollment.setTotalCredits(totalCredits);
        enrollment.setEnrollmentDate(System.currentTimeMillis());

        // Create or update student with enrollment
        Student student = new Student();
        student.setId(currentUser.getUid());
        student.setEmail(currentUser.getEmail());
        student.setName(currentUser.getDisplayName());
        student.setEnrollment(enrollment);

        // Save to Firestore
        db.collection("students").document(currentUser.getUid())
                .set(student)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Enrollment successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EnrollmentActivity.this, WelcomeActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Enrollment failed: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
    }
}
