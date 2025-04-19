package com.ensias.essuhelp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView, idTextView, emailTextView;
    private EditText medicalInfoEditText;
    private Button saveButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI elements
        nameTextView = findViewById(R.id.textViewName);
        idTextView = findViewById(R.id.textViewStudentId);
        emailTextView = findViewById(R.id.textViewEmail);
        medicalInfoEditText = findViewById(R.id.editTextMedicalInfo);
        saveButton = findViewById(R.id.buttonSave);



        // Set click listener for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


}
