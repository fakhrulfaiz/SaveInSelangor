package com.example.assignment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.assignment.data.LocationData;
import com.example.assignment.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.transition.platform.MaterialFade;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    private double currentLat; // New variable to store latitude
    private double currentLng; // New variable to store longitude
    private SearchView mapSearchView;
    Location currentLocation;
    private static final String TAG = "MapsActivity";
    private PlacesClient placesClient;
    private SeekBar seekBar;
    Intent intent;
    private int radius = 10 * 1000;
    private DatabaseReference postRef;
    private boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         intent = getIntent();
        String apiKey = getString(R.string.api_key);

        if (!apiKey.isEmpty()) {
            Places.initialize(getApplicationContext(), apiKey);
        } else {
            Log.e("API Key Error", "Google Maps API key is missing");
        }
        com.example.assignment.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        postRef = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Posts");




        // For toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // For Search view
        placesClient = Places.createClient(this);

        seekBar = findViewById(R.id.radiusSeekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the radius variable when SeekBar progress changes
                radius = progress * 1000;  // Convert progress to meters, adjust as needed

                // Perform a new search or update the existing markers based on the new radius
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });



        mapSearchView = findViewById(R.id.mapSearch);

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;

                Geocoder geoCoder = new Geocoder(MapsActivity.this);
                try {
                    addressList = geoCoder.getFromLocationName(location, 20);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (addressList != null && !addressList.isEmpty()) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    // Add marker for the searched location
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));

                    // Move the camera to a suitable position
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                } else {
                    String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                            "?location=" + currentLat + "," + currentLng +
                            "&radius=" + radius +
                            "&type=" + location +
                            "&sensor=true" +
                            "&key=" + getResources().getString(R.string.api_key);

                    Executor executor = Executors.newSingleThreadExecutor();

                    CompletableFuture.supplyAsync(() -> {
                        try {

                            return downloadUrl(url);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, executor).thenAccept(result -> {
                        parseResult(result);

                    });

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        FloatingActionButton showAllBtn = findViewById(R.id.showAllBtn);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Handle back button click (navigate back to MainActivity)
                startActivity(new Intent(MapsActivity.this, MainActivity.class));
                finish();

        });
        showAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear existing markers
                mMap.clear();

                // Add markers from Firebase within the specified radius
                postRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            if(snapshot.hasChild("location")){
                                LocationData locationData = snapshot.child("location").getValue(LocationData.class);
                                if(locationData != null){
                                    double lat = locationData.getLat();
                                    double lng = locationData.getLng();
                                    Log.d(TAG, "lng: " + lng);
                                    Log.d(TAG, "lat: " + lat);
                                    // Check if the crime location is within the radius
                                    if (isLocationWithinRadius(lat, lng, currentLat, currentLng, 20.0)) {

//                                        String type = locationData.getType();
//                                        String date = locationData.getDate();
//                                        String time = locationData.getTime();




                                        String description = snapshot.child("description").getValue().toString();

                                        String snippet = "";
                                        String imageUrl = snapshot.child("postimage").child("img1").getValue().toString();
                                        if(!imageUrl.equals("none")){
                                            snippet = description + "<@>" + imageUrl;
                                        }else{
                                            snippet = description;
                                        }
                                        Log.d(TAG, "postimage: " + imageUrl);

                                        String subject = snapshot.child("subject").getValue().toString();
                                        LatLng latLng = new LatLng(lat, lng);

                                        // Add marker with crime information
                                        Objects.requireNonNull(mMap.addMarker(new MarkerOptions()
                                                        .position(latLng)
                                                        .title(subject)))
                                                .setSnippet(snippet);
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10));
                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Error reading from Firebase: " + databaseError.getMessage());
                    }
                });
            }
        });
        FloatingActionButton arrowDownButton = findViewById(R.id.arrowDownButton);
        FloatingActionButton hospitalButton = findViewById(R.id.hospitalButton);
        FloatingActionButton policeButton = findViewById(R.id.policeButton);
        FloatingActionButton fireStationButton = findViewById(R.id.fireStationButton);
        LinearLayout linearLayout = findViewById(R.id.expandedButtonsContainer);
        arrowDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linearLayout.getVisibility() == View.GONE) {
                    arrowDownButton.setImageResource(R.drawable.ic_up_arrow);
                    TransitionManager.beginDelayedTransition(linearLayout, new MaterialFade());
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    arrowDownButton.setImageResource(R.drawable.ic_down_arrow);
                    TransitionManager.beginDelayedTransition(linearLayout, new MaterialFade());
                    linearLayout.setVisibility(View.GONE);
                }
                isExpanded = !isExpanded;
            }
        });
        hospitalButton.setOnClickListener(v -> {
            getNearbyPlace("hospital");
        });
        policeButton.setOnClickListener(v -> {
            getNearbyPlace("police%20station");

        });
        fireStationButton.setOnClickListener(v -> {
            getNearbyPlace("fire%20station");
        });
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }
    private boolean isLocationWithinRadius(double lat1, double lng1, double lat2, double lng2, double radius) {
        float[] distance = new float[1];
        Location.distanceBetween(lat1, lng1, lat2, lng2, distance);
        return distance[0] <= radius * 1000; // Convert radius to meters for comparison
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                marker.showInfoWindow();

                return true;
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @NonNull
            @Override
            public View getInfoContents(@NonNull Marker marker) {
                // Inflate your custom info window layout
                View infoWindow = getLayoutInflater().inflate(R.layout.map_show_info, null);

                // Set the title and button in the custom info window
                TextView title = infoWindow.findViewById(R.id.info_window_title);
                TextView desc = infoWindow.findViewById(R.id.info_window_desc);
                ImageView image =infoWindow.findViewById(R.id.info_window_image);
               title.setText(marker.getTitle());
                String snippet = marker.getSnippet();
                Log.d(TAG, "snippet: " + snippet);
                if(snippet.contains("<@>")){
                    String[] parts = snippet.split("<@>");

                    if (parts.length == 2) {

                        desc.setText(parts[0]);
                        image.setVisibility(View.VISIBLE);
                        Log.d(TAG, "getInfoContents: " + parts[1]);
                        Picasso.get().load(parts[1]).into(image);
                    }

                }else{
                    desc.setText(snippet);
                }
                return infoWindow;
            }



            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng destinationLatLng = marker.getPosition();
                String destinationName = marker.getTitle();

                // Start Google Maps intent for directions
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + destinationLatLng.latitude + "," + destinationLatLng.longitude)
                );
                intent.setPackage("com.google.android.apps.maps");

                startActivity(intent);
            }
        });

        getCurrentLocation();


    }

    private void getNearbyPlace(String command) {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + currentLat + "," + currentLng +
                "&radius=" + radius +
                "&keyword=" + command +
                "&sensor=true" +
                "&key=" + getResources().getString(R.string.api_key);
        Log.d(TAG, "Url : " + url);
        Executor executor = Executors.newSingleThreadExecutor();

        CompletableFuture.supplyAsync(() -> {
            try {
                return downloadUrl(url);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executor).thenAccept(this::parseResult);
    }


    private void getCurrentLocation() {
        // Call this method when you need to get the current location
        // It's now separated for better code organization

        // Initialize the FusedLocationProviderClient
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Request location updates
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                currentLocation = location;
                                currentLat = location.getLatitude(); // Store latitude
                                currentLng = location.getLongitude(); // Store longitude
                                LatLng currentLatLng = new LatLng(currentLat, currentLng);
                                Log.d(TAG, "currentLat : " + currentLat);
                                Log.d(TAG, "currentLng : " + currentLng);

                                // Retrieve the command from the intent
                                if (intent != null && intent.hasExtra("COMMAND")) {
                                    String command = intent.getStringExtra("COMMAND");
                                    Log.d(TAG, "Command : " + command);
                                    String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                                            "?location=" + location.getLatitude() + "," + location.getLongitude() +
                                            "&radius=" + radius +
                                            "&keyword=" + command +
                                            "&sensor=true" +
                                            "&key=" + getResources().getString(R.string.api_key);

                                    Log.d(TAG, "nearbyUrl : " + url);
                                    Executor executor = Executors.newSingleThreadExecutor();

                                    CompletableFuture.supplyAsync(() -> {
                                        try {
                                            return downloadUrl(url);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }, executor).thenAccept(result -> parseResult(result));

                                }else if (getIntent() != null && getIntent().hasExtra("LATLNG")) {
                                    LatLng latLng = getIntent().getParcelableExtra("LATLNG");

                                    // Now you can use the latLng object as needed
                                    assert latLng != null;
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                    double latitude = latLng.latitude;
                                    double longitude = latLng.longitude;
                                    Log.d(TAG, "onSuccess: " + latitude + " , " + longitude);
                                    mMap.addMarker(new MarkerOptions().position(latLng).title("").snippet(
                                            "Lat :" + latitude + "\nLng :" + longitude
                                    ));
                                    // Use latitude and longitude as needed in your map-related functionality
                                }else{
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10));
                                }

                            }
                        }
                    });

            // Enable My Location button
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.map_none) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        } else if (id == R.id.map_normal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (id == R.id.map_satellite) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (id == R.id.map_hybrid) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (id == R.id.map_terrain) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }

        return super.onOptionsItemSelected(item);
    }

    private void parseResult(String data) {
        Executor executor = Executors.newSingleThreadExecutor();

        CompletableFuture.supplyAsync(() -> {
            Log.d(TAG, "parseResult : " + data);
            JsonParser jsonParser = new JsonParser();
            List<HashMap<String, String>> mapList = null;

            try {
                JSONObject object = new JSONObject(data);
                mapList = jsonParser.parseResult(object);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            return mapList;
        }, executor).thenAcceptAsync(hashMaps -> {

            runOnUiThread(() -> {
                mMap.clear();
                double shortestDistance = Double.MAX_VALUE;
                LatLng closestLocation = null;

                for (int i = 0; i < hashMaps.size(); i++) {
                    HashMap<String, String> hashMapList = hashMaps.get(i);
                    double lat = Double.parseDouble(Objects.requireNonNull(hashMapList.get("lat")));
                    double lng = Double.parseDouble(Objects.requireNonNull(hashMapList.get("lng")));
                    String name = hashMapList.get("name");
                    LatLng latLng = new LatLng(lat, lng);

                    // Calculate distance between current location and this marker
                    Location currentLocation = new Location("current");
                    currentLocation.setLatitude(currentLat);
                    currentLocation.setLongitude(currentLng);

                    Location markerLocation = new Location("marker");
                    markerLocation.setLatitude(lat);
                    markerLocation.setLongitude(lng);

                    double distance = currentLocation.distanceTo(markerLocation);

                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        closestLocation = latLng;
                    }

                    mMap.addMarker(new MarkerOptions().position(latLng).title(name).snippet(
                            String.format(Locale.getDefault(), "Lat: %f\nLng: %f\nDistance: %.2f km", lat, lng, distance/1000)
                    ));
                }

                if (closestLocation != null) {
                    // Move the camera to the closest location
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(closestLocation, 12));
                }
            });
        });
    }




    private String downloadUrl(String string) throws IOException {
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        try (InputStream stream = connection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            return builder.toString();
        }
    }
}
