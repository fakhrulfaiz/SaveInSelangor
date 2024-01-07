package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class LocationSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);

        // Initialize the Places API client
        String apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {

            Places.initialize(getApplicationContext(), apiKey);
        }

        // Create the AutocompleteSupportFragment programmatically
        AutocompleteSupportFragment autocompleteFragment = new AutocompleteSupportFragment();
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Send back the selected Place object to the calling activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("SELECTED_PLACE", place);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle errors
                Log.e("LocationSearchActivity", "onError: AutocompletePrediction: " + apiKey);
                    Log.e("LocationSearchActivity", "onError: AutocompletePrediction: " + status.getStatusMessage());
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, autocompleteFragment)
                .commit();

        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle cancellation when the Cancel button is clicked
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }
}
