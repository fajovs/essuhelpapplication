package com.ensias.essuhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private Button emergencyButton, profileButton, viewEmergenciesButton, logoutButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userRole = "student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        welcomeTextView = findViewById(R.id.textViewWelcome);
        emergencyButton = findViewById(R.id.buttonEmergency);
        profileButton = findViewById(R.id.buttonProfile);
        viewEmergenciesButton = findViewById(R.id.buttonViewEmergencies);
        logoutButton = findViewById(R.id.buttonLogout);

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User not logged in, redirect to login
            navigateToLogin();
            return;
        }

        // Get user data from database
        mDatabase.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                welcomeTextView.setText("Welcome, " + user.getName());
                                userRole = user.getRole();

                                // Show/hide buttons based on role
                                if (userRole.equals("admin")) {
                                    emergencyButton.setVisibility(View.GONE);
                                    viewEmergenciesButton.setVisibility(View.VISIBLE);
                                } else {
                                    emergencyButton.setVisibility(View.VISIBLE);
                                    viewEmergenciesButton.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(DashboardActivity.this, "Database error: " +
                                databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Set click listeners
        emergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, EmergencyActivity.class);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        viewEmergenciesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, EmergencyListActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                navigateToLogin();
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
