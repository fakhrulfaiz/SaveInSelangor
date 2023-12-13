package com.example.assignment;

import static com.example.assignment.Constants.ERROR_DIALOG_REQUEST;
import static com.example.assignment.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment.data.GuideData;
import com.example.assignment.databinding.ActivityMainBinding;
import com.example.assignment.helper.CheckMapHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
private CircleImageView navProfileImage;

private FirebaseAuth auth;
private DatabaseReference userRef;
String currentUserID;
TextView navUsername;
ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;
private boolean mLocationPermissionGranted = false;
    // Define constant values
    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     binding = ActivityMainBinding.inflate(getLayoutInflater());
       //setContentView(binding.getRoot());
       // replaceFragment(new HomeFragment());






        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.NHFMain);
         navController = host.getNavController();

        Toolbar toolbar = findViewById(R.id.TBMainAct);
        setSupportActionBar(toolbar);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        // Set up the ActionBar with the NavController
      
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();

            if (destinationId == R.id.DestHome ||
                    destinationId == R.id.DestChat ||
                    destinationId == R.id.DestSetting) {
                // Hide the toolbar for specific fragments
                Objects.requireNonNull(getSupportActionBar()).hide();
            } else {
                // Show the toolbar for other fragments
                Objects.requireNonNull(getSupportActionBar()).show();
            }
        });

        setupBottomNavMenu(navController);


        auth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users");

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        View naView = navigationView.inflateHeaderView(R.layout.navigation_header);
        navProfileImage = (CircleImageView) naView.findViewById(R.id.navProfile);
        navUsername = (TextView) naView.findViewById(R.id.navUsername);

     //   handleBottomNavigationView(binding.bottomNavigationView);

        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    String userName = "@" + Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                    if(snapshot.hasChild("profileimage")){
                        String image = Objects.requireNonNull(snapshot.child("profileimage").getValue()).toString();
                        navUsername.setText(userName);
                        Log.d("ProfileImageURL", image);
                        Picasso.get().load(image).placeholder(R.drawable.profile_default).memoryPolicy(MemoryPolicy.NO_CACHE).into(navProfileImage);
                    }else{
                        navProfileImage.setImageResource(R.drawable.profile_default);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item.getItemId());
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_navigation_menu,menu);
        return true;
    }

    private void setupBottomNavMenu(NavController navController){
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        NavigationUI.setupWithNavController(bottomNav,navController);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       try{
           Navigation.findNavController(this,R.id.NHFMain).navigate(item.getItemId());
           return true;
       } catch (Exception e) {
           return super.onOptionsItemSelected(item);
       }

    }

    @Override
    public boolean onSupportNavigateUp() {
       return Navigation.findNavController(this,R.id.NHFMain).navigateUp();
    }


    private void handleBottomNavigationView(BottomNavigationView bottomNavigationView) {
        if (getResources().getConfiguration().screenWidthDp <= 600) {
            if(bottomNavigationView.getVisibility() == View.GONE){
                bottomNavigationView.setVisibility(View.VISIBLE);
            }

            bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int selected = item.getItemId();
                    if (selected == R.id.DestHome) {
                        replaceFragment(new HomeFragment());
                    } else if (selected == R.id.DestHome) {
                        replaceFragment(new ChatFragment());
                    } else if (selected == R.id.DestSetting) {
                        replaceFragment(new SettingsFragment());
                    }
                    return true;
                }
            });
        }
    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();
    }


    private void UserMenuSelector(int itemId) {
        if (itemId == R.id.nav_profile) {
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_home) {
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_settings) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_signout) {

                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();

        } else if (itemId == R.id.nav_map) {
           if(CheckMapHelper.checkMapServices(this)){
               getLocationPermission();
           }
        }
    }
    private void checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                getLocationPermission();
            }
        }
    }
    public boolean isServicesOK() {
        Log.d("MainActivity", "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            // Everything is fine, and the user can make map requests
            Log.d("MainActivity", "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // An error occurred but we can resolve it
            Log.d("MainActivity", "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            assert dialog != null;
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showEnableGpsDialog();
            return false;
        }
        return true;
    }

    private void showEnableGpsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS is not enabled. Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Open the location settings to enable GPS
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            startMapsActivity();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    private void startMapsActivity() {

        startActivity(new Intent(MainActivity.this, MapsActivity.class));
        finish();
    }



}


