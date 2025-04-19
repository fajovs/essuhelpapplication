package com.ensias.essuhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView, idTextView, emailTextView;
    private EditText medicalInfoEditText;
    private Button saveButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }

        userId = currentUser.getUid();

        // Initialize UI elements
        nameTextView = findViewById(R.id.textViewName);
        idTextView = findViewById(R.id.textViewStudentId);
        emailTextView = findViewById(R.id.textViewEmail);
        medicalInfoEditText = findViewById(R.id.editTextMedicalInfo);
        saveButton = findViewById(R.id.buttonSave);

        // Load user data
        loadUserData();

        // Set click listener for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMedicalInfo();
            }
        });
    }

    private void loadUserData() {
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                nameTextView.setText("Name: " + user.getName());
                                idTextView.setText("Student ID: " + user.getStudentId());
                                emailTextView.setText("Email: " + user.getEmail());
                                medicalInfoEditText.setText(user.getMedicalInfo());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ProfileActivity.this, "Database error: " +
                                databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveMedicalInfo() {
        String medicalInfo = medicalInfoEditText.getText().toString().trim();

        mDatabase.child("users").child(userId).child("medicalInfo").setValue(medicalInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this,
                                    "Medical information saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    "Failed to save medical information", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
