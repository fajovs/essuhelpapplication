package com.ensias.essuhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, idEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView loginTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private boolean isAdminExists = false;
    private boolean isCreatingAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        nameEditText = findViewById(R.id.editTextName);
        idEditText = findViewById(R.id.editTextStudentId);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        registerButton = findViewById(R.id.buttonRegister);
        loginTextView = findViewById(R.id.textViewLogin);

        // Check if admin already exists
        checkAdminExists();

        // Set click listeners
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to login screen
                finish();
            }
        });
    }

    private void checkAdminExists() {
        mDatabase.child("users").orderByChild("role").equalTo("admin").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        isAdminExists = dataSnapshot.exists();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegisterActivity.this, "Database error: " +
                                databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUser() {
        final String name = nameEditText.getText().toString().trim();
        final String studentId = idEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty() || studentId.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return;
        }

        // Determine if this is an admin registration (for example, if student ID starts with "ADMIN")
        isCreatingAdmin = studentId.toUpperCase().startsWith("ADMIN");

        // Check if trying to create admin when one already exists
        if (isCreatingAdmin && isAdminExists) {
            Toast.makeText(this, "Admin account already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress (you can add a progress dialog here)

        // Create user with Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Hide progress

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Save additional user data to Firebase Realtime Database
                            User userObject = new User(
                                    name,
                                    studentId,
                                    email,
                                    isCreatingAdmin ? "admin" : "student"
                            );

                            mDatabase.child("users").child(user.getUid()).setValue(userObject)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterActivity.this,
                                                        "Registration successful", Toast.LENGTH_SHORT).show();

                                                // Navigate to dashboard
                                                Intent intent = new Intent(RegisterActivity.this,
                                                        DashboardActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(RegisterActivity.this,
                                                        "Failed to save user data", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user
                            Toast.makeText(RegisterActivity.this, "Registration failed: " +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
