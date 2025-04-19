package com.ensias.essuhelp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class EmergencyActivity extends AppCompatActivity {

    private EditText descriptionEditText;
    private RadioGroup emergencyLevelRadioGroup;
    private Spinner locationSpinner;
    private ImageView campusMapImageView;
    private Button sendAlertButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

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

            }
        });
    }


}
