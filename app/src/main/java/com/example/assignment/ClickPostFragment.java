package com.example.assignment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClickPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClickPostFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
String postKey;
    private ImageView clickPostImage;
    private TextView clickPostDescription;
final private String ARG_POST_KEY = "postKey";
    private DatabaseReference databaseReference;

    public ClickPostFragment() {
        // Required empty public constructor
    }



    public ClickPostFragment newInstance(String postKey) {
        ClickPostFragment fragment = new ClickPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST_KEY, postKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postKey = getArguments().getString(ARG_POST_KEY);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_click_post, container, false);

        // Find views by ID
        clickPostImage = view.findViewById(R.id.clickPostImage);
        clickPostDescription = view.findViewById(R.id.clickPostDesciption);
        databaseReference = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference()
                .child("Posts").child(postKey);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String description = snapshot.child("description").getValue().toString();
                String postImage = snapshot.child("postimage").child("img1").getValue().toString();

                if(description.equals("")){
                    clickPostDescription.setVisibility(View.GONE);
                }else{
                    clickPostDescription.setText(description);
                }
                clickPostDescription.setText(description);
                if (!postImage.isEmpty() && !postImage.equals("none")) {
                    Picasso.get().load(postImage).into(clickPostImage);
                }else{
                    clickPostImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Now you can use clickPostImage and clickPostDescription as needed

        return view;
    }

}