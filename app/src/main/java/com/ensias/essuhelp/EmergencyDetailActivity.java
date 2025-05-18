package com.ensias.essuhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmergencyDetailActivity extends AppCompatActivity {

    private TextView nameTextView, levelTextView, locationTextView, timeTextView,
            statusTextView, descriptionTextView, medicalInfoTextView;
    private ImageView locationImageView;
    private Button respondButton, completeButton;

    private DatabaseReference mDatabase;
    private String emergencyId;
    private Emergency currentEmergency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_detail);

        // Get emergency ID from intent
        emergencyId = getIntent().getStringExtra("emergencyId");
        if (emergencyId == null) {
            finish();
            return;
        }

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        nameTextView = findViewById(R.id.textViewName);
        levelTextView = findViewById(R.id.textViewLevel);
        locationTextView = findViewById(R.id.textViewLocation);
        timeTextView = findViewById(R.id.textViewTime);
        statusTextView = findViewById(R.id.textViewStatus);
        descriptionTextView = findViewById(R.id.textViewDescription);
        medicalInfoTextView = findViewById(R.id.textViewMedicalInfo);
        locationImageView = findViewById(R.id.imageViewLocation);
        respondButton = findViewById(R.id.buttonRespond);
        completeButton = findViewById(R.id.buttonComplete);

        // Load emergency data
        loadEmergencyData();

        // Set click listeners for buttons
        respondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmergencyStatus("responding");
            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmergencyStatus("completed");
            }
        });
    }

    private void loadEmergencyData() {
        mDatabase.child("emergencies").child(emergencyId).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            currentEmergency = dataSnapshot.getValue(Emergency.class);
                            if (currentEmergency != null) {
                                displayEmergencyData();
                                loadUserMedicalInfo();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(EmergencyDetailActivity.this, "Database error: " +
                                databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayEmergencyData() {
        nameTextView.setText("Name: " + currentEmergency.getUserName());
        levelTextView.setText("Level: " + currentEmergency.getEmergencyLevel());
        locationTextView.setText("Location: " + currentEmergency.getLocation());
        timeTextView.setText("Time: " + currentEmergency.getTimestamp());
        statusTextView.setText("Status: " + currentEmergency.getStatus());
        descriptionTextView.setText("Description: " + currentEmergency.getDescription());

        // Set location image based on location
        // This would be replaced with actual campus building images
        int imageResourceId = getResources().getIdentifier(
                "map_" + currentEmergency.getLocation().toLowerCase().replace(" ", "_").replace("-", "_"),
                "drawable",
                getPackageName()
        );

        if (imageResourceId != 0) {
            locationImageView.setImageResource(imageResourceId);
        } else {
            locationImageView.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // Update button visibility based on status
        if (currentEmergency.getStatus().equals("pending")) {
            respondButton.setVisibility(View.VISIBLE);
            completeButton.setVisibility(View.GONE);
        } else if (currentEmergency.getStatus().equals("responding")) {
            respondButton.setVisibility(View.GONE);
            completeButton.setVisibility(View.VISIBLE);
        } else {
            respondButton.setVisibility(View.GONE);
            completeButton.setVisibility(View.GONE);
        }
    }

    private void loadUserMedicalInfo() {
        mDatabase.child("users").child(currentEmergency.getUserId()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                medicalInfoTextView.setText("Medical Info: " +
                                        (user.getMedicalInfo().isEmpty() ?
                                                "No medical information provided" : user.getMedicalInfo()));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(EmergencyDetailActivity.this, "Database error: " +
                                databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateEmergencyStatus(String status) {
        mDatabase.child("emergencies").child(emergencyId).child("status").setValue(status)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EmergencyDetailActivity.this,
                                    "Status updated to " + status, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EmergencyDetailActivity.this,
                                    "Failed to update status", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}