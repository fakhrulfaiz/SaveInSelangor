package com.example.assignment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.TextView;

import com.example.assignment.data.CrimeReportData;
import com.example.assignment.data.LocationData;
import com.example.assignment.data.Posts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView postList;
boolean likeChecker = false;

    DatabaseReference likesRef;
    private String currID;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        likesRef = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference()
                .child("Likes");

    }


    private void displayAllUSerPosts() {
        DatabaseReference postRef = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference()
                .child("Posts");
        Query query = postRef.limitToLast(30);

        FirebaseRecyclerOptions<Posts> options =
                new FirebaseRecyclerOptions.Builder<Posts>()
                        .setQuery(query, Posts.class)
                        .build();

        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull Posts model) {

                        String postKey = getRef(position).getKey();
                        holder.setSubject(model.getSubject());
                        holder.setFullname(model.getFullname());
                        holder.setTime(model.getTime());
                        holder.setDate(model.getDate());
                        holder.setDescription(model.getDescription());
                        holder.setProfileimage(model.getProfileimage());
                        String imageUrl = model.getPostimage().get("img1");
                        holder.setPostimage(imageUrl);

                        holder.mView.setOnClickListener(v -> {
                            // Pass the postKey to the ClickPostFragment
                            Bundle bundle = new Bundle();
                            bundle.putString("postKey", postKey);

                            // Navigate to ClickPostFragment
                            Navigation.findNavController(v).navigate(R.id.clickPostFragment, bundle);

                        });
                        holder.likeBtn.setOnClickListener(v -> {
                            likeChecker = true;
                            likesRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    assert postKey != null;
                                    if(likeChecker){
                                        if(snapshot.child(postKey).hasChild(currID)){

                                            likesRef.child(postKey).child(currID).removeValue();
                                            likeChecker = false;
                                        }else{

                                            likesRef.child(postKey).child(currID).setValue(true);
                                            likeChecker = false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        });

                        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("postKey", postKey);

                                // Navigate to ClickPostFragment
                                Navigation.findNavController(v).navigate(R.id.commentFragment, bundle);

                            }
                        });

                        holder.reportPostBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showReportOptionsPopup(holder.reportPostBtn);
                            }
                        });
                        holder.reportPostBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showReportOptionsPopup(holder.reportPostBtn);
                            }
                        });
                        postRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child(postKey).hasChild("location")){
                                    holder.LocationButton.setVisibility(View.VISIBLE);
                                    holder.LocationButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            LocationData locationData = snapshot.child(postKey).child("location").getValue(LocationData.class);
                                            if (locationData != null) {
                                                double lat = locationData.getLat();
                                                double lng = locationData.getLng();
                                                LatLng latLng = new LatLng(lat, lng);
                                                Log.d("ChatFragment", "onSuccess: " + lat + " , " + lng);
                                                    // You can create an Intent to start the MapsActivity and pass the command as an extra
                                                    Intent intent = new Intent(getContext(), MapsActivity.class);
                                                    intent.putExtra("LATLNG", latLng);
                                                    startActivity(intent);
                                                    if (getActivity() != null) {
                                                        getActivity().finish();
                                                    }

                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        holder.setLikeButtonStatus(postKey);
                        holder.setCommentCount(postKey);
                    }

                    @NonNull
                    @Override
                    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_layout, parent, false);
                        return new PostsViewHolder(view);
                    }

                    @Override
                    public boolean onFailedToRecycleView(@NonNull PostsViewHolder holder) {
                        return true;
                    }
                };

        postList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    private void showReportOptionsPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.report_menu, popupMenu.getMenu());

        // Set gravity for proper alignment
        popupMenu.setGravity(Gravity.BOTTOM);

        // Set a listener for the report options
        popupMenu.setOnMenuItemClickListener(new androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the selected report option
                String reportOption = item.getTitle().toString();
                showReportTypeDialog(reportOption);
                return true;
            }
        });

        // Show the PopupMenu
        popupMenu.show();
    }

    private void showReportTypeDialog(String reportReason) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("What type of issue are you reporting?");

        // Add multi-choice checkboxes for different report types
        // Create a list of ReportType objects with name and description
        final CharSequence[] items = {
                "Hate - Slurs, Racist or sexist stereotype, Hateful reference, symbols or logos",
                "Abuse & Harassment - Insult, Targeted Harassment",
                "Violent Speech - Violent threat, Wish or Harm, Glorification of Violence",
                "Privacy - Give some",
                "Spam - Financial scams, fake engagement, repetitive replies, posting malicious links"
        };

        // Boolean array to track the selected items
        final boolean[] checkedItems = new boolean[items.length];

        builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // Handle the selected report types
                checkedItems[which] = isChecked;
            }
        });

        // Add "Next" button
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the "Next" button click
                showConfirmationDialog(reportReason, items, checkedItems);
                dialog.dismiss();
            }
        });

        // Add "Cancel" button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel the report action
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showConfirmationDialog(String reportReason, CharSequence[] reportTypes, boolean[] checkedItems) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Report Post");

        // Build a list of selected report types
        StringBuilder selectedTypes = new StringBuilder();
        for (int i = 0; i < reportTypes.length; i++) {
            if (checkedItems[i]) {
                selectedTypes.append(reportTypes[i]).append("\n");
            }
        }

        // Add a message with the selected report types
        builder.setMessage("Are you sure you want to report this post for the following issues?\n\n" + selectedTypes.toString());

        // Add "Yes" button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Handle the report action
                // You can perform further actions or make a network request here
            }
        });

        // Add "No" button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Cancel the report action
            }
        });

        builder.show();
    }




    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        ImageView likeBtn , commentBtn, reportPostBtn;
        TextView totalLikes, totalComments, LocationButton;
        int countLikes, countComments;
        String currID;
        DatabaseReference likesRef, postRef;

        public PostsViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;

            likeBtn = itemView.findViewById(R.id.ic_dislike);
            commentBtn = itemView.findViewById(R.id.ic_comment);
            totalLikes = itemView.findViewById(R.id.totalLike);
            totalComments = itemView.findViewById(R.id.totalComment);
            reportPostBtn = itemView.findViewById(R.id.reportPostBtn);
            LocationButton = itemView.findViewById(R.id.goToLocationPost);
            likesRef = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference()
                    .child("Likes");
            postRef = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference()
                    .child("Posts");
            currID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        }
        public void setCommentCount(final String postKey){
            postRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(postKey).hasChild("comments")){
                        countComments = (int) snapshot.child(postKey).child("comments").getChildrenCount();
                        totalComments.setText(Integer.toString(countComments));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        public void setLikeButtonStatus(final String postKey){
            likesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(postKey).hasChild(currID)){
                        countLikes = (int) snapshot.child(postKey).getChildrenCount();
                        likeBtn.setImageResource(R.drawable.ic_like);
                        totalLikes.setText(Integer.toString(countLikes));
                    }else{
                        countLikes = (int) snapshot.child(postKey).getChildrenCount();
                        likeBtn.setImageResource(R.drawable.ic_dislike);
                        totalLikes.setText(Integer.toString(countLikes));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        public void setSubject(String subject)
        {
            Log.d("ChatFragment", "setSubject: " + subject);

            TextView username = mView.findViewById(R.id.post_subject);
            if(subject.equals("")){
                username.setVisibility(View.GONE);
            }
            username.setText(subject);
        }
        public void setFullname(String fullname)
        {
            TextView username = mView.findViewById(R.id.post_username);
            username.setText(fullname);
        }

        public void setProfileimage(String profileimage)
        {
            CircleImageView image = mView.findViewById(R.id.post_profile_image);
            if (profileimage != null && !profileimage.isEmpty() && !profileimage.equals("none")) {
                Picasso.get().load(profileimage).into(image);
            }else{
                Picasso.get().load(R.drawable.profile_default).into(image);
            }

        }

        public void setTime(String time)
        {
            TextView PostTime = mView.findViewById(R.id.post_time);
            PostTime.setText("    " + time);
        }

        public void setDate(String date)
        {
            TextView PostDate = mView.findViewById(R.id.post_date);
            PostDate.setText("    " + date);
        }

        public void setDescription(String description)
        {
            TextView PostDescription = mView.findViewById(R.id.post_description);
            PostDescription.setText(description);
        }

        public void setPostimage(String postimage) {
            ImageView PostImage = mView.findViewById(R.id.post_image);

            // Check if postimage is not empty, not null, and not equal to "none"
            if (postimage != null && !postimage.isEmpty() && !postimage.equals("none")) {
                Picasso.get().load(postimage).into(PostImage);
            } else {
                // If postimage is empty, null, or "none," hide the ImageView
                PostImage.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button addPost = view.findViewById(R.id.addPost);
        addPost.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.addPostFragment));


        postList = view.findViewById(R.id.users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        displayAllUSerPosts();
    }
}