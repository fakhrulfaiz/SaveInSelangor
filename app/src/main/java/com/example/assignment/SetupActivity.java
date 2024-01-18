package com.example.assignment;


import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

// Account setup during user sign up
public class SetupActivity extends AppCompatActivity {

    Button create_account;
    private CircleImageView profileImage;
    private EditText userNameTV, nameTV, birthDateTV;
    private FirebaseAuth auth;
    private DatabaseReference userRef;
    String currentUserID;
    final static int GALLERY_PICK = 1;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private StorageReference userProfileImageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);


        create_account = findViewById(R.id.registerSetup);
        profileImage = findViewById(R.id.addPhoto);
        userNameTV = findViewById(R.id.usernameText);
        nameTV = findViewById(R.id.nameText);
        birthDateTV = findViewById(R.id.birthDate);
        auth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users").child(currentUserID);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile Images");


        profileImage.setOnClickListener(v -> {

            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_PICK);


        });
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    if(snapshot.hasChild("profileimage")){
                        String image = Objects.requireNonNull(snapshot.child("profileimage").getValue()).toString();
                        Log.d("ProfileImageURL", image);
                        Picasso.get().load(image).placeholder(R.drawable.profile_default).memoryPolicy(MemoryPolicy.NO_CACHE).into(profileImage);
                    }else{
                        profileImage.setImageResource(R.drawable.profile_default);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        create_account.setOnClickListener(v -> saveAccountSetupInformation());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null){

            Uri imageUri = data.getData();

            StorageReference filePath = userProfileImageRef.child(currentUserID + ".jpg");

            assert imageUri != null;
            filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SetupActivity.this, "Profile picture added", Toast.LENGTH_SHORT).show();

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadURL = uri.toString();
                                userRef.child("profileimage").setValue(downloadURL).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(SetupActivity.this, "Task Successful!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SetupActivity.this, "Task Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                    else{
                        Toast.makeText(SetupActivity.this, "Task Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void saveAccountSetupInformation() {

        String userName = userNameTV.getText().toString();
        String name = nameTV.getText().toString();
        String birthDate = birthDateTV.getText().toString();
        if(TextUtils.isEmpty(userName)){
            Toast.makeText(this, "Please write your username", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please write your name", Toast.LENGTH_SHORT).show();
        }else{

            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("username",userName);
            userMap.put("name", name);
            userMap.put("birthdate", birthDate);
            userMap.put("gender","none");
            userRef.updateChildren(userMap).addOnCompleteListener(task -> {

                if(task.isSuccessful()){
                    Toast.makeText(SetupActivity.this, "Your Account is created Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SetupActivity.this, LoginActivity.class));
                }else{
                    Toast.makeText(SetupActivity.this, "Error Occurred: " + task.getException(), Toast.LENGTH_SHORT).show();



                }
            });


        }

    }





}