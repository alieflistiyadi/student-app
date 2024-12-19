package com.example.studentapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set up toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        TextView registerLink = findViewById(R.id.registerLink);

        // Set up click listeners
        loginButton.setOnClickListener(v -> handleLogin());
        registerLink.setOnClickListener(v -> handleForgotPassword());
        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, go to welcome page
            startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
            finish();
        }
    }

    private void handleLogin() {
        if (loginButton != null) {
            loginButton.setEnabled(false);
        }

        try {
            String email = emailInput != null ? emailInput.getText().toString().trim() : "";
            String password = passwordInput != null ? passwordInput.getText().toString().trim() : "";

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                startActivity(new Intent(LoginActivity.this, EnrollmentActivity.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed: " + 
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "Error in handleLogin: " + e.getMessage(), e);
            Toast.makeText(this, "Error processing login: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (loginButton != null) {
                loginButton.setEnabled(true);
            }
        }
    }

    private void handleGoogleSignIn() {
        // TODO: Implement Google Sign-in
        Toast.makeText(this, "Google Sign-in coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void handleForgotPassword() {
        // TODO: Implement forgot password functionality
    }
}
