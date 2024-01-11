package com.example.assignment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment.data.Announcement;
import com.example.assignment.data.Comments;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.transition.platform.MaterialFade;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnnouncementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnnouncementFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView announcementList;

    public AnnouncementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnnouncementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnnouncementFragment newInstance(String param1, String param2) {
        AnnouncementFragment fragment = new AnnouncementFragment();
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
        return inflater.inflate(R.layout.fragment_announcement, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        announcementList = (RecyclerView) view.findViewById(R.id.recyclerViewAnnouncement);
     //   announcementList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        announcementList.setLayoutManager(linearLayoutManager);
        displayAllAnnouncement();
    }


    public void displayAllAnnouncement() {
        Log.d("AnnouncementFragment", "onStart: Testing" );
        super.onStart();
        Query query = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference()
                .child("Announcement");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Announcement announcement = snapshot1.getValue(Announcement.class);
                    Log.d("AnnouncementFragment",  announcement.getSubject());
                    Log.d("AnnouncementFragment",  announcement.getDate());
                    Log.d("AnnouncementFragment",  announcement.getDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<Announcement> options = new FirebaseRecyclerOptions.Builder<Announcement>().setQuery(query, Announcement.class).build();

        FirebaseRecyclerAdapter<Announcement, AnnouncementViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Announcement, AnnouncementViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position, @NonNull Announcement model) {
                holder.setSubject(model.getSubject());
                Log.d("AnnouncementFragment", "model.getSubject(): " + model.getSubject());
                holder.setDescription(model.getDescription());
                holder.setDate(model.getDate());

            }

            @NonNull
            @Override
            public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcement_layout, parent, false);
                return new AnnouncementFragment.AnnouncementViewHolder(view);
            }
        };


        announcementList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class AnnouncementViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView TVDesc;  // Declare TextView for description

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            TVDesc = itemView.findViewById(R.id.announcement_description);

            // Set click listener for the entire item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toggle visibility of the description
                    TransitionManager.beginDelayedTransition((ViewGroup) mView, new MaterialFade());
                    if (TVDesc.getVisibility() == View.VISIBLE) {
                        TVDesc.setVisibility(View.GONE);
                    } else {
                        TVDesc.setVisibility(View.VISIBLE);
                    }

                }
            });
        }
        public void setSubject(String subject) {
            TextView subjectTV = mView.findViewById(R.id.announcement_subject);
            subjectTV.setText(subject);

        }
        public void setDescription(String description) {
            TVDesc = mView.findViewById(R.id.announcement_description);
            TVDesc.setText(description);

        }
        public void setDate(String date) {
            TextView myDate = mView.findViewById(R.id.announcement_date);
            myDate.setText(date);

        }


    }

}
