package com.ensias.essuhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class EmergencyListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Emergency, EmergencyViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_list);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewEmergencies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Query to get all emergencies, ordered by timestamp (most recent first)
        Query query = mDatabase.child("emergencies").orderByChild("timestamp");

        // Configure adapter
        FirebaseRecyclerOptions<Emergency> options =
                new FirebaseRecyclerOptions.Builder<Emergency>()
                        .setQuery(query, Emergency.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Emergency, EmergencyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EmergencyViewHolder holder, int position,
                                            @NonNull Emergency model) {
                holder.bind(model);

                // Set click listener to view emergency details
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String emergencyId = getRef(position).getKey();
                        Intent intent = new Intent(EmergencyListActivity.this,
                                EmergencyDetailActivity.class);
                        intent.putExtra("emergencyId", emergencyId);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public EmergencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_emergency, parent, false);
                return new EmergencyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    // ViewHolder for emergency items
    public static class EmergencyViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView, levelTextView, locationTextView, timeTextView, statusTextView;

        public EmergencyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewName);
            levelTextView = itemView.findViewById(R.id.textViewLevel);
            locationTextView = itemView.findViewById(R.id.textViewLocation);
            timeTextView = itemView.findViewById(R.id.textViewTime);
            statusTextView = itemView.findViewById(R.id.textViewStatus);
        }

        public void bind(Emergency emergency) {
            nameTextView.setText(emergency.getUserName());
            levelTextView.setText(emergency.getEmergencyLevel());
            locationTextView.setText(emergency.getLocation());
            timeTextView.setText(emergency.getTimestamp());
            statusTextView.setText(emergency.getStatus());

            // Set background color based on emergency level
            if (emergency.getEmergencyLevel().equals("Critical")) {
                itemView.setBackgroundResource(R.drawable.bg_emergency_critical);
            } else if (emergency.getEmergencyLevel().equals("Severe")) {
                itemView.setBackgroundResource(R.drawable.bg_emergency_severe);
            } else {
                itemView.setBackgroundResource(R.drawable.bg_emergency_mild);
            }
        }
    }
}
