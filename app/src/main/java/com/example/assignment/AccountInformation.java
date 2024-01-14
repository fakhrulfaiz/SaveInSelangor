package com.example.assignment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.assignment.dialog.AddImageDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountInformation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountInformation extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText nameChangeEditText;
    private EditText usernameChangeEditText;
    private EditText birthDateEditText;
    private EditText emailChangeEditText;
    private Button confirmButton;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private String currentUserID;
    private ActivityResultLauncher<Intent> galleryLauncher, cameraLauncher;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private ImageView profileImageView;

    public AccountInformation() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountInformation.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountInformation newInstance(String param1, String param2) {
        AccountInformation fragment = new AccountInformation();
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
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        handleActivityResult(result, REQUEST_PICK_IMAGE);
                        //    handleGalleryResult(result);
                    }
                });
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        handleActivityResult(result, REQUEST_IMAGE_CAPTURE);
                        //   handleGalleryResult(result);
                    }
                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_information, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Find views by ID
        profileImageView = view.findViewById(R.id.profileImageView);
        nameChangeEditText = view.findViewById(R.id.nameChange);
        usernameChangeEditText = view.findViewById(R.id.usernameChange);
        birthDateEditText = view.findViewById(R.id.birthDate);
        emailChangeEditText = view.findViewById(R.id.emailChange);
        confirmButton = view.findViewById(R.id.confirmBtn);
        progressBar = view.findViewById(R.id.progressBar);
        emailChangeEditText.setHint(Objects.requireNonNull(auth.getCurrentUser()).getEmail());
        currentUserID = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users");

        loadUserData();
        Button changeImageButton = view.findViewById(R.id.changeImageButton);
        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageDialog();
            }
        });
        // Set onClickListener for the button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleConfirmButtonClick();
            }
        });
    }
    private void loadUserData() {


        userRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Load profile image
                    if (snapshot.hasChild("profileimage")) {
                        String image = Objects.requireNonNull(snapshot.child("profileimage").getValue()).toString();
                        Log.d("ProfileImageURL", image);
                        Picasso.get().load(image).placeholder(R.drawable.profile_img_loading)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(profileImageView);
                    } else {
                        profileImageView.setImageResource(R.drawable.profile_default);
                    }

                    String name = snapshot.child("name").getValue(String.class);
                    String username = snapshot.child("username").getValue(String.class);
                    String birthDate = snapshot.child("birthdate").getValue(String.class);

                    // Add log statements for debugging
                    Log.d("FirebaseData", "Name: " + name);
                    Log.d("FirebaseData", "Username: " + username);
                    Log.d("FirebaseData", "BirthDate: " + birthDate);

                    // Add null checks
                    if (name != null) {
                        nameChangeEditText.setHint(name);
                    }
                    if (username != null) {
                        usernameChangeEditText.setHint(username);
                    }
                    if (birthDate != null) {
                        birthDateEditText.setHint(birthDate);
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }
    private void handleConfirmButtonClick() {
        // Retrieve values from EditTexts
        String name = nameChangeEditText.getText().toString();
        String username = usernameChangeEditText.getText().toString();
        String birthDate = birthDateEditText.getText().toString();
        String email = emailChangeEditText.getText().toString();

        if (!TextUtils.isEmpty(username)) {

            checkUsernameAvailability(name, username, birthDate, email);
        } else {

            showConfirmationDialog(name, username, birthDate, email);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }
    private void checkUsernameAvailability(final String name, final String username, final String birthDate, final String email) {
        // Assuming you have a reference to the users' node in your database
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users");

        usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username already exists, show an error message or take appropriate action
                    Toast.makeText(requireContext(), "Username already exists", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Information updated successfully", Toast.LENGTH_SHORT).show();

                    // Show confirmation dialog
                    showConfirmationDialog(name, username, birthDate, email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Toast.makeText(requireContext(), "Error checking username availability", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openImageDialog() {
        AddImageDialog customDialog = new AddImageDialog(requireContext());

        customDialog.setOnCameraClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                openCamera();
            }
        });
        customDialog.setOnGalleryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                openGallery();
            }
        });

        customDialog.setOnCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });

        customDialog.show();
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Activity activity = getActivity();
        assert activity != null;
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }
    private void handleActivityResult(ActivityResult result, int requestCode) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                if (requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_PICK_IMAGE) {
                    // Handle the captured image from the camera
                    Uri imageUri = data.getData();
                    DatabaseReference newUserRef = userRef.child(currentUserID);
                    StorageReference userProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile Images");
                    StorageReference filePath = userProfileImageRef.child(currentUserID + ".jpg");

                    assert imageUri != null;
                    filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(), "Profile picture changed", Toast.LENGTH_SHORT).show();

                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadURL = uri.toString();
                                        newUserRef.child("profileimage").setValue(downloadURL).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getContext(), "Task Successful!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "Task Failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                            else{
                                Toast.makeText(getContext(), "Task Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }
    }
    private void showConfirmationDialog(final String name, final String username, final String birthDate, final String email) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Account Information");
        builder.setMessage("Are you sure you want to change your information?");
        builder.setPositiveButton("Yes", (dialog, which) -> {

            if (!TextUtils.isEmpty(name)) {
                // Update 'name' in the Firebase database
                userRef.child(currentUserID).child("name").setValue(name);
            }
            if (!TextUtils.isEmpty(username)) {
                // Update 'username' in the Firebase database

                userRef.child(currentUserID).child("username").setValue(username);
            }
            if (!TextUtils.isEmpty(birthDate)) {
                // Update 'birthdate' in the Firebase database
                userRef.child(currentUserID).child("birthdate").setValue(birthDate);
            }
            if (!TextUtils.isEmpty(email)) {
//                // Update 'birthdate' in the Firebase database
//                FirebaseUser user = auth.getCurrentUser();
//
//                assert user != null;
//                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//
//                    }
//                });
           }
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // User clicked No, do nothing or provide feedback
            Toast.makeText(getActivity(), "Data change canceled", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }


}
