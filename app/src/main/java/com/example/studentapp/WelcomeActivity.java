package com.example.studentapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.studentapp.adapters.EnrolledSubjectAdapter;
import com.example.studentapp.models.Student;
import com.example.studentapp.models.Subject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView studentName;
    private TextView studentEmail;
    private TextView totalCredits;
    private TextView enrollmentDate;
    private RecyclerView enrolledSubjectsRecyclerView;
    private MaterialButton viewSubjectsButton;
    private MaterialButton logoutButton;
    private EnrolledSubjectAdapter adapter;
    private List<Subject> enrolledSubjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        studentName = findViewById(R.id.studentName);
        studentEmail = findViewById(R.id.studentEmail);
        totalCredits = findViewById(R.id.totalCredits);
        enrollmentDate = findViewById(R.id.enrollmentDate);
        enrolledSubjectsRecyclerView = findViewById(R.id.enrolledSubjectsRecyclerView);
        viewSubjectsButton = findViewById(R.id.viewSubjectsButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Initialize RecyclerView
        enrolledSubjects = new ArrayList<>();
        adapter = new EnrolledSubjectAdapter(enrolledSubjects);
        enrolledSubjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        enrolledSubjectsRecyclerView.setAdapter(adapter);

        // Set up click listeners
        viewSubjectsButton.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, EnrollmentActivity.class));
        });

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            finish();
        });

        findViewById(R.id.refreshButton).setOnClickListener(v -> loadStudentData());

        // Load student data
        loadStudentData();
    }

    private void loadStudentData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        db.collection("students").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Student student = documentSnapshot.toObject(Student.class);
                        if (student != null) {
                            // Display student info
                            studentName.setText(student.getName());
                            studentEmail.setText(student.getEmail());

                            // Display enrollment info if available
                            if (student.getEnrollment() != null) {
                                totalCredits.setText(String.format("Total Credits: %d", 
                                    student.getEnrollment().getTotalCredits()));
                                
                                SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                                String date = sdf.format(new Date(student.getEnrollment().getEnrollmentDate()));
                                enrollmentDate.setText(String.format("Enrolled on: %s", date));
                                
                                // Load enrolled subjects
                                loadEnrolledSubjects(student.getEnrollment().getEnrolledSubjects());
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(WelcomeActivity.this, 
                        "Error loading student data: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
    }

    private void loadEnrolledSubjects(List<String> subjectIds) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            return;
        }

        db.collection("subjects")
                .whereIn("id", subjectIds)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    enrolledSubjects.clear();
                    enrolledSubjects.addAll(queryDocumentSnapshots.toObjects(Subject.class));
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading subjects: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // If no user is signed in, go back to login
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            finish();
        }
    }
}
