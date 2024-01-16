package com.example.assignment;

import static com.example.assignment.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.assignment.helper.CheckMapHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Spinner placeSpinner;
    private TableLayout crimeTable;
    private JSONObject crimeData;
    Context context;
    public LocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocationFragment.
     */
    // TODO: Rename and change types and number of parameters


    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the ImageViews by their IDs
        ImageView fireButton = view.findViewById(R.id.nearbyFireBtn);
        ImageView policeButton = view.findViewById(R.id.nearbyPoliceBtn);
        ImageView hospitalButton = view.findViewById(R.id.nearbyHospitalBtn);
        placeSpinner = view.findViewById(R.id.placeSpinner);
        crimeTable = view.findViewById(R.id.crimeTable);


        loadJSONData();
        setupSpinner();
        // Set click listeners for the ImageViews
        fireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for nearby fire button
                onFindNearbyClick(v);
            }
        });

        policeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for nearby police button
                onFindNearbyClick(v);
            }
        });

        hospitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for nearby hospital button
                onFindNearbyClick(v);
            }
        });
    }

    // Define the click handling methods
    private void onFindNearbyClick(View view) {
        Context context = getContext();
        // Implement your logic for the nearby fire button click
        if(CheckMapHelper.checkMapServices(context)){
            assert context != null;
            getLocationPermission(context, view);
        }

    }
    private void getLocationPermission(Context context, View view) {
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            startMapsActivity(view);
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void startMapsActivity(View view) {
        String command = view.getContentDescription().toString();

        // You can create an Intent to start the MapsActivity and pass the command as an extra
        Intent intent = new Intent(getContext(), MapsActivity.class);
        intent.putExtra("COMMAND", command);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void loadJSONData() {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("stats.json");
            InputStreamReader reader = new InputStreamReader(inputStream);

            StringBuilder jsonString = new StringBuilder();
            char[] buffer = new char[1024];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                jsonString.append(buffer, 0, read);
            }

            crimeData = new JSONObject(jsonString.toString());

            reader.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupSpinner() {
        final ArrayAdapter<String> placeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        placeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Iterator<String> placeIterator = crimeData.keys();
        while (placeIterator.hasNext()) {
            String place = placeIterator.next();
            placeAdapter.add(place);
        }

        placeSpinner.setAdapter(placeAdapter);

        placeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateTable(placeAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    private void updateTable(String selectedPlace) {
        crimeTable.removeAllViews();

        // Get the place data for the selected place
        try {
            JSONObject placeData = crimeData.getJSONObject(selectedPlace);

            // Get the years as an array
            JSONArray years = placeData.names();
            if (years != null) {
                // Create header row with years
                TableRow headerRow = new TableRow(context);
                headerRow.addView(createTableCell("Crime Type"));

                for (int i = 0; i < years.length(); i++) {
                    String year = years.getString(i);
                    headerRow.addView(createTableCell(year));
                }

                crimeTable.addView(headerRow);

                // Iterate over crime types
                Iterator<String> crimeIterator = placeData.getJSONObject(years.getString(0)).keys();
                while (crimeIterator.hasNext()) {
                    String crimeType = crimeIterator.next();

                    // Create data row with crime type and counts for each year
                    TableRow dataRow = new TableRow(context);
                    dataRow.addView(createTableCell(crimeType));

                    for (int i = 0; i < years.length(); i++) {
                        String year = years.getString(i);
                        JSONObject crimeDetails = placeData.getJSONObject(year);
                        int count = crimeDetails.getInt(crimeType);
                        dataRow.addView(createTableCell(String.valueOf(count)));
                    }

                    crimeTable.addView(dataRow);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private TextView createTableCell(String text) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);

        // Set background drawable for border
        if(text.equals("Crime Type") || text.equals("2020") || text.equals("2021")){
            textView.setBackgroundResource(R.drawable.header_border);
            textView.setTextColor(Color.WHITE);
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        }else{
            textView.setBackgroundResource(R.drawable.table_cell_border);
        }


        return textView;
    }


}