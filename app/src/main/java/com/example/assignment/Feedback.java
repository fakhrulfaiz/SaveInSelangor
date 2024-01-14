package com.example.assignment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Feedback#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Feedback extends Fragment {
    private RatingBar ratingBar;
    private EditText editTextFeedback;
    private Button buttonOverallService;
    private Button buttonTransparency;
    private Button buttonRepairQuality;
    private Button buttonCustomerSupport;
    private Button buttonConfirm;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Feedback() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Feedback.
     */
    // TODO: Rename and change types and number of parameters
    public static Feedback newInstance(String param1, String param2) {
        Feedback fragment = new Feedback();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        initializeViews(view);
        setupButtonListeners();
        return view;
    }
    private void initializeViews(View view) {
        ratingBar = view.findViewById(R.id.ratingBar);
        editTextFeedback = view.findViewById(R.id.editTextFeedback);
        buttonOverallService = view.findViewById(R.id.buttonOverallService);
        buttonTransparency = view.findViewById(R.id.buttonTransparency);
        buttonRepairQuality = view.findViewById(R.id.buttonRepairQuality);
        buttonCustomerSupport = view.findViewById(R.id.buttonCustomerSupport);
        buttonConfirm = view.findViewById(R.id.buttonConfirm);
    }
    private void setupButtonListeners() {
        // Handle the confirm button click
        buttonConfirm.setOnClickListener(v -> submitFeedback());
        // Handle the overall service button click
        buttonOverallService.setOnClickListener(v -> highlightButton(buttonOverallService));

        // Handle the transparency button click
        buttonTransparency.setOnClickListener(v -> highlightButton(buttonTransparency));

        // Handle the repair quality button click
        buttonRepairQuality.setOnClickListener(v -> highlightButton(buttonRepairQuality));

        // Handle the customer support button click
        buttonCustomerSupport.setOnClickListener(v -> highlightButton(buttonCustomerSupport));

        // Handle the confirm button click
        buttonConfirm.setOnClickListener(v -> submitFeedback());

    }
    private void highlightButton(Button button) {
        // Reset all buttons to the default background
        buttonOverallService.setBackgroundColor(Color.TRANSPARENT);
        buttonTransparency.setBackgroundColor(Color.TRANSPARENT);
        buttonRepairQuality.setBackgroundColor(Color.TRANSPARENT);
        buttonCustomerSupport.setBackgroundColor(Color.TRANSPARENT);

        // Highlight the clicked button
        button.setBackgroundColor(Color.YELLOW); // Use an actual color value or resource
    }

    private void submitFeedback() {
        float rating = ratingBar.getRating();
        String feedback = editTextFeedback.getText().toString().trim();


        // TODO: Implement the logic to send feedback to the admin.

        Toast.makeText(getContext(), "Thank you for your feedback!", Toast.LENGTH_SHORT).show();

        // Clear the inputs after submission
        ratingBar.setRating(0);
        editTextFeedback.setText("");
    }
}