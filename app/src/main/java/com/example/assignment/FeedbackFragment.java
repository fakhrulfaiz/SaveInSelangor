package com.example.assignment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RatingBar rateBarFeedback;
    private TextView TVRating;
    private EditText ETFeedback;
    private Button btnFeedback;
    private FirebaseAuth auth;
    private String currentUserID;
    private DatabaseReference feedbackRef;
    private float FBrating = 0;


    public FeedbackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedbackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedbackFragment newInstance(String param1, String param2) {
        FeedbackFragment fragment = new FeedbackFragment();
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

        auth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        feedbackRef = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Feedback");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Find views by their IDs
        rateBarFeedback = view.findViewById(R.id.ratingBar);
        TVRating = view.findViewById(R.id.TVRating);
        ETFeedback = view.findViewById(R.id.ETFeedback);
        btnFeedback = view.findViewById(R.id.btnSubmitFeedback);

        // Set up OnRatingBarChangeListener for the RatingBar
        rateBarFeedback.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Update the TextView with the selected rating
                FBrating = rating;
                TVRating.setVisibility(View.VISIBLE);
                TVRating.setText("You have rate : " + rating);
            }
        });

        // Set up OnClickListener for the submit button
        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the user's feedback
                String feedback = ETFeedback.getText().toString().trim();

                // Display a toast message based on whether feedback is provided
                if (!feedback.isEmpty() && FBrating != 0) {

                    HashMap<String, Object> FBMap = new HashMap<>();

                    FBMap.put("rating", FBrating);
                    FBMap.put("feedback", feedback);
                    feedbackRef.child(currentUserID).setValue(FBMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            showToast("Thank you for your feedback!");
                            View view = getView();
                            assert view != null;
                            Navigation.findNavController(view).popBackStack();
                        }

                    });


                } else {
                    showToast("Enter yout feedback");
                }
            }
        });

        Button buttonOverallService = view.findViewById(R.id.buttonOverallService);
        Button buttonTransparency = view.findViewById(R.id.buttonTransparency);
        Button buttonRepairQuality = view.findViewById(R.id.buttonRepairQuality);
        Button buttonCustomerSupport = view.findViewById(R.id.buttonCustomerSupport);
        buttonOverallService.setOnClickListener(v -> {

        });
        buttonTransparency.setOnClickListener(v -> {

        });
        buttonRepairQuality.setOnClickListener(v -> {

        });
        buttonCustomerSupport.setOnClickListener(v -> {

        });

    }

    @Override
    public void onStart() {


        feedbackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                        Log.d("FeedbackFragment", "data.hasChild(currentUserID): " + snapshot.hasChild(currentUserID));
                        if (snapshot.hasChild(currentUserID)) {
                            Log.d("FeedbackFragment", "onDataChange: " + currentUserID);
                            String feedbackText = snapshot.child(currentUserID).child("feedback").getValue(String.class);
                            // Access the values of each child using getValue()
                            Double rating = snapshot.child(currentUserID).child("rating").getValue(Double.class);

                            // Set the rating for the RatingBar
                            if (rating != null) {
                                rateBarFeedback.setRating(rating.floatValue());
                            }
                            TVRating.setVisibility(View.VISIBLE);
                            TVRating.setText("You have rate : " + rating);

                            ETFeedback.setText(feedbackText);

                        }
                    }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FeedbackFragment", "onCancelled: true");
            }
        });
        super.onStart();
    }

    private void showToast(String message) {
        // Get the application context
        Context context = getContext();

        // Create and display the toast
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}