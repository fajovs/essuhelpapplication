package com.ensias.essuhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EmergencyActivity extends AppCompatActivity {

    private EditText descriptionEditText;
    private RadioGroup emergencyLevelRadioGroup;
    private Spinner locationSpinner;
    private ImageView campusMapImageView;
    private Button sendAlertButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

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

        // Get user name from database
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                userName = user.getName();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(EmergencyActivity.this, "Database error: " +
                                databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Initialize UI elements
        descriptionEditText = findViewById(R.id.editTextDescription);
        emergencyLevelRadioGroup = findViewById(R.id.radioGroupEmergencyLevel);
        locationSpinner = findViewById(R.id.spinnerLocation);
        campusMapImageView = findViewById(R.id.imageViewCampusMap);
        sendAlertButton = findViewById(R.id.buttonSendAlert);

        // Set click listener for send alert button
        sendAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmergencyAlert();
            }
        });
    }

    private void sendEmergencyAlert() {
        String description = descriptionEditText.getText().toString().trim();

        if (description.isEmpty()) {
            descriptionEditText.setError("Please describe the emergency");
            descriptionEditText.requestFocus();
            return;
        }

        // Get selected emergency level
        int selectedRadioButtonId = emergencyLevelRadioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "Please select emergency level", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String emergencyLevel = selectedRadioButton.getText().toString();

        // Get selected location
        String location = locationSpinner.getSelectedItem().toString();

        // Create emergency object
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        Emergency emergency = new Emergency(
                userId,
                userName,
                description,
                emergencyLevel,
                location,
                timestamp,
                "pending"
        );

        // Save to Firebase
        String emergencyId = mDatabase.child("emergencies").push().getKey();

        mDatabase.child("emergencies").child(emergencyId).setValue(emergency)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EmergencyActivity.this,
                                    "Emergency alert sent successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(EmergencyActivity.this,
                                    "Failed to send emergency alert", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
