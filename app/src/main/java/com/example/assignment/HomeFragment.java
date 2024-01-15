package com.example.assignment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assignment.helper.CallHelper;
import com.example.assignment.helper.NewsApiHelper;
import com.example.assignment.helper.RoundedTransformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */



public class HomeFragment extends Fragment {
    String currentUserID;
    private FirebaseAuth auth;
    private DatabaseReference userRef;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private CircleImageView navProfileImage;
    private ImageView callPolice,callAmbulance,callBomba;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView dateTextView;
    private TextView greetingTextView;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


         dateTextView = view.findViewById(R.id.date_text);
         greetingTextView = view.findViewById(R.id.greeting_text);

        // Update date and time
        updateDateTime();

        // Update time every minute
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateDateTime();
                handler.postDelayed(this, 60000); // 1 minute
            }
        }, 60000);

        // Update greeting
        updateGreeting();

        auth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users");
        navProfileImage = (CircleImageView) view.findViewById(R.id.navProfile);
        
        //emergency button declaration
        callPolice = view.findViewById(R.id.callPolice);
        callAmbulance = view.findViewById(R.id.callAmbulance);
        callBomba = view.findViewById(R.id.callBomba);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the common onClick method when any button is clicked
                makeEmergencyCall(v);
            }
        };

        callPolice.setOnClickListener(onClickListener);
        callAmbulance.setOnClickListener(onClickListener);
        callBomba.setOnClickListener(onClickListener);
        //setProfile Image
        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){


                    if(snapshot.hasChild("profileimage")){
                        String image = Objects.requireNonNull(snapshot.child("profileimage").getValue()).toString();
                        Log.d("ProfileImageURL", image);
                        Picasso.get().load(image).placeholder(R.drawable.profile_img_loading).memoryPolicy(MemoryPolicy.NO_CACHE).into(navProfileImage);
                    }else{
                        navProfileImage.setImageResource(R.drawable.profile_default);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        Button reportBtn = view.findViewById(R.id.reportBtn);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(view).navigate(R.id.reportsFragment);
            }
        });
        Button guideBtn = view.findViewById(R.id.guideBtn);
        guideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(view).navigate(R.id.guidesFragment);
            }
        });
        Button locationBtn = view.findViewById(R.id.locationBtn);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(view).navigate(R.id.locationFragment);
            }
        });
        Button rightsBtn = view.findViewById(R.id.rightsBtn);
        rightsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(view).navigate(R.id.rightsFragment);
            }
        });

        NewsApiHelper newsApiHelper = new NewsApiHelper();
        List<NewsApiHelper.NewsItem> news = newsApiHelper.fetchData();
        ImageView newsList = view.findViewById(R.id.newsList);  // Assuming newsList is an ImageView
        loadNewsImage(newsList,news);
        newsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("newsList",  new ArrayList<>(news));
                // Navigate to the new fragment with the Bundle
                Navigation.findNavController(view).navigate(R.id.NAViewPageFragment, bundle);
            }
        });

    }

    void loadNewsImage(ImageView newsList, List<NewsApiHelper.NewsItem> news){
        // Use a Handler to schedule image loading with a delay
        Handler handler = new Handler();
        int delayMillis = 6000;  // Adjust this value according to your needs

        for (int i = 0; i < news.size(); i++) {
            final int index = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Picasso.get()
                            .load(news.get(index).getImageUrl())
                            .fit()
                            .centerCrop()
                            .transform(new RoundedTransformation(20, 0))
                            .into(newsList);

                }

            }, i * delayMillis);  // Delay loading each image by 'i * delayMillis' milliseconds

        }
    }
    private void updateDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy 'at' hh:mm a", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());
        dateTextView.setText(formattedDate);
    }

    private void updateGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour <= 11) {
            greeting = "Good Morning Selangor!";
        } else if (hour <= 16) {
            greeting = "Good Afternoon Selangor!";
        } else {
            greeting = "Good Evening Selangor!";
        }

        greetingTextView.setText(greeting);
    }
    public void makeEmergencyCall(View view) {
        // Get the ID of the clicked button
        int buttonId = view.getId();

        if (buttonId == R.id.callPolice) {
            CallHelper.callService(getContext(), "999");
        } else if (buttonId == R.id.callAmbulance) {
            CallHelper.callService(getContext(), "999");
        } else if (buttonId == R.id.callBomba) {
            CallHelper.callService(getContext(), "994");
        }

    }
}