package com.example.assignment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment.data.Comments;
import com.example.assignment.data.Posts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentFragment extends Fragment {

    private ImageButton postCommentBtn;
    private EditText commentInput;
    private RecyclerView commentList;
    private DatabaseReference userRef;
    final private String ARG_POST_KEY = "postKey";
    private String postKey, currentUserID;
    private FirebaseAuth auth;
    private DatabaseReference postRef;


    public CommentFragment() {
        //  Required empty public constructor
    }

    public CommentFragment newInstance(String param1, String param2) {
        CommentFragment fragment = new CommentFragment();
        Bundle args =  new Bundle();
        args.putString(ARG_POST_KEY, postKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postKey =  getArguments().getString(ARG_POST_KEY);
        }
        userRef = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users");
        postRef =  FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Posts").child(postKey).child("comments");
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        commentList  = (RecyclerView) view.findViewById(R.id.recyclerViewComments);
        commentList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentList.setLayoutManager(linearLayoutManager);

        commentInput = (EditText) view.findViewById(R.id.editTextComment);
        postCommentBtn = (ImageButton) view.findViewById(R.id.postCommentBtn);

        postCommentBtn.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String  username = snapshot.child("username").getValue().toString();

                            validateComment(username);

                            commentInput.setText("");
                        }
                    }



                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });


            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference()
                .child("Posts").child(postKey).child("comments");

        FirebaseRecyclerOptions<Comments> options =
                new FirebaseRecyclerOptions.Builder<Comments>()
                        .setQuery(query, Comments.class)
                        .build();
        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options)  {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Comments model) {

                holder.setUsername(model.getUsername());
                holder.setComment(model.getComment());
                holder.setDate(model.getDate());
                holder.setTime(model.getTime());
            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
                return new CommentFragment.CommentsViewHolder(view);
            }
        };

        commentList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder{

        View mView ;
        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }
        public void setUsername(String username) {
            TextView commentUsername = mView.findViewById(R.id.comment_username_username);
            commentUsername.setText("@" + username + " ");

        }
        public void setComment(String comment) {
            TextView myComment = mView.findViewById(R.id.comment_text);
            myComment.setText(comment);

        }
        public void setDate(String date) {
            TextView myDate = mView.findViewById(R.id.comment_date);
            myDate.setText(" " + date);

        }
        public void setTime(String time) {
            TextView myTime = mView.findViewById(R.id.comment_time);
            myTime.setText(" " + time);

        }

    }
    private void validateComment(String username) {
    String commentText = commentInput.getText().toString();
        if(TextUtils.isEmpty(commentText)){
    Toast.makeText(getContext(), "Write to comment", Toast.LENGTH_SHORT).show();
        }else{
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrDate = currDate.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currTime = new SimpleDateFormat("HH:mm");
            final String saveCurrTime = currTime.format(calForTime.getTime());

            final String randomKey = currentUserID + saveCurrDate + saveCurrTime;

            HashMap<String,Object> commentsMap = new HashMap<>();
            commentsMap.put("uid", currentUserID);
            commentsMap.put("comment", commentText);
            commentsMap.put("date", saveCurrDate);
            commentsMap.put("time", saveCurrTime);
            commentsMap.put("username", username);


            postRef.child(randomKey).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){

                    }else{
                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
}