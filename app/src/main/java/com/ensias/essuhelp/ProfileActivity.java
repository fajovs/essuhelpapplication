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

    private EditText editTextName, editTextStudentId, editTextMedicalInfo;
    private TextView textViewEmail;
    private TextView labelStudentId;
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
        editTextName = findViewById(R.id.editTextName);
        editTextStudentId = findViewById(R.id.editTextStudentId);
        editTextMedicalInfo = findViewById(R.id.editTextMedicalInfo);
        textViewEmail = findViewById(R.id.textViewEmail);
        labelStudentId = findViewById(R.id.labelStudentId);
        saveButton = findViewById(R.id.buttonSave);

        // Load user data
        loadUserData();

        // Save changes
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
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
                                editTextName.setText(user.getName());
                                editTextStudentId.setText(user.getStudentId());
                                editTextMedicalInfo.setText(user.getMedicalInfo());
                                textViewEmail.setText("Email: " + user.getEmail());

                                if ("admin".equalsIgnoreCase(user.getRole())) {
                                    // Hide student ID field for admins
                                    editTextStudentId.setVisibility(View.GONE);
                                    labelStudentId.setVisibility(View.GONE);
                                }
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

    private void saveProfileData() {
        String name = editTextName.getText().toString().trim();
        String studentId = editTextStudentId.getText().toString().trim();
        String medicalInfo = editTextMedicalInfo.getText().toString().trim();

        // Update name
        mDatabase.child("users").child(userId).child("name").setValue(name);
        // Update student ID only if field is visible
        if (editTextStudentId.getVisibility() == View.VISIBLE) {
            mDatabase.child("users").child(userId).child("studentId").setValue(studentId);
        }
        // Update medical info
        mDatabase.child("users").child(userId).child("medicalInfo").setValue(medicalInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this,
                                    "Profile information saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    "Failed to save profile information", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
