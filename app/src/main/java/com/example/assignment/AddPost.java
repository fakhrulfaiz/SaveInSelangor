package com.example.assignment;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import com.example.assignment.adapter.ImageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPost#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPost extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MAX_PHOTOS = 4;

    private EditText postDesc;
    private ImageView imageView;
    private GridView gridView;
    private ArrayList<Uri> selectedImages = new ArrayList<>();
    private ArrayAdapter<Uri> gridAdapter;


    private StorageReference postImageRef;
    private DatabaseReference usersRef,postRef;
    private FirebaseAuth auth;
    private String Description, saveCurrDate, saveCurrTime, postRandomName,currentUserID;
    private Uri ImageUri;
    private Button postBtn;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageAdapter imageAdapter;
private List<Uri> uriList = new ArrayList<>();
    private List<String> downloadUriList = new ArrayList<>();
    private ActivityResultLauncher<Intent> galleryLauncher;

    public AddPost() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPost.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPost newInstance(String param1, String param2) {
        AddPost fragment = new AddPost();
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
        // Initialize the ActivityResultLauncher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        handleGalleryResult(result);
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        postDesc = view.findViewById(R.id.postDescription);
        gridView = view.findViewById(R.id.gridView);
        postBtn = view.findViewById(R.id.postButton);

//        gridAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, selectedImages);
//        gridView.setAdapter(gridAdapter);

        // Use the custom adapter

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        postImageRef = FirebaseStorage.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users");

        postRef = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Posts");
        imageAdapter = new ImageAdapter(requireContext(), selectedImages);
        gridView.setAdapter(imageAdapter);

        Button buttonAttachPhoto = view.findViewById(R.id.buttonAttachPhoto);
        buttonAttachPhoto.setOnClickListener(view12 -> openGallery());

        gridView.setOnItemClickListener((adapterView, view1, position, id) -> {
            if (selectedImages.size() < MAX_PHOTOS && position == selectedImages.size()) {
                openGallery();
            }
        });

        postBtn.setOnClickListener(v -> {


                saveImageToFirebaseStorage();

        });

        return view;
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void handleGalleryResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Uri imageUri = result.getData().getData();
            uriList.add(imageUri);
            if (selectedImages.size() < MAX_PHOTOS) {
                selectedImages.add(imageUri);

                imageAdapter.notifyDataSetChanged();


                if (selectedImages.size() == 1) {
                    // Show GridView when the first image is added
                    gridView.setVisibility(View.VISIBLE);
                }

                if (selectedImages.size() == MAX_PHOTOS) {
                    // Hide the "Attach Photo" button when the maximum number of photos is reached
                    requireView().findViewById(R.id.buttonAttachPhoto).setVisibility(View.GONE);
                }
            }
        }
    }
    private void saveImageToFirebaseStorage(){

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrDate = currDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currTime = new SimpleDateFormat("HH:mm");
        saveCurrTime = currTime.format(calForTime.getTime());

        postRandomName = saveCurrDate + saveCurrTime;

        Description = postDesc.getText().toString();
        if(!uriList.isEmpty()){
            for(Uri uri : uriList){
                StorageReference filePath = postImageRef.child("post Images").child(uri.getLastPathSegment() + postRandomName + ".jpg");
                filePath.putFile(uri).addOnCompleteListener(task -> {

                    final Task<Uri> imageUploadedSuccessfully = filePath.getDownloadUrl().addOnCompleteListener(uriTask -> {
                        if (uriTask.isSuccessful()) {
                            // Add the download URL to your list or perform other actions
                            Uri downloadUri = uriTask.getResult();
                            downloadUriList.add(downloadUri.toString());
                            savePostInformationToDatabase();
                            Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle the failure to get download URL
                            Toast.makeText(getContext(), "Error getting download URL: " + uriTask.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        }else{

            if(!Description.equals("")){
                savePostInformationToDatabase();
            }


        }

    }

    private void savePostInformationToDatabase() {

        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    String userFullname = snapshot.child("username").getValue().toString();
                    String userprofileImage;
                    if(snapshot.hasChild("profileimage")){
                        userprofileImage = snapshot.child("profileimage").getValue().toString();
                    }else{
                        userprofileImage = "none";
                    }
                    HashMap<String, Object> postsMap = new HashMap<>();
                    HashMap<String, Object> urlMap = new HashMap<>();
                    int i = 1;
                    for(String url: downloadUriList){
                        urlMap.put("img" + i, url);
                        i++;
                    }
                    postsMap.put("uid", currentUserID);
                    postsMap.put("date", saveCurrDate);
                    postsMap.put("time", saveCurrTime);
                    postsMap.put("description", Description);
                    postsMap.put("profileimage", userprofileImage);
                    postsMap.put("fullname", userFullname);
                    DatabaseReference newPostRef = postRef.child(currentUserID + postRandomName);
                    postRef.child(currentUserID + postRandomName).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){


                                    if(!urlMap.isEmpty()){
                                        newPostRef.child("postimage").updateChildren(urlMap);
                                    }else{
                                        urlMap.put("img1", "none");
                                        newPostRef.child("postimage").updateChildren(urlMap);
                                    }




                                    View view = getView();
                                    assert view != null;
                                    Navigation.findNavController(view).popBackStack();
                                    Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                                }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
