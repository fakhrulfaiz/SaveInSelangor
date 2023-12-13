package com.example.assignment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountChangePassword#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountChangePassword extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private EditText reEnterNewPasswordEditText;
    private Button changePasswordButton;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    public AccountChangePassword() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountChangePassword.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountChangePassword newInstance(String param1, String param2) {
        AccountChangePassword fragment = new AccountChangePassword();
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
        View view = inflater.inflate(R.layout.fragment_account_change_password, container, false);

        // Find views by ID
        currentPasswordEditText = view.findViewById(R.id.currentPassword);
        newPasswordEditText = view.findViewById(R.id.newPassword);
        reEnterNewPasswordEditText = view.findViewById(R.id.reEnterNewPassword);
        changePasswordButton = view.findViewById(R.id.changePassword);
        progressBar = view.findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        // Set onClickListener for the button
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleChangePasswordClick();
                // Handle change password button click
                // You can access the entered values using currentPasswordEditText.getText().toString(), newPasswordEditText.getText().toString(), and reEnterNewPasswordEditText.getText().toString()
            }
        });

        return view;

    }

    private void handleChangePasswordClick() {
        String currentPasswordText = currentPasswordEditText.getText().toString();
        String newPasswordText = newPasswordEditText.getText().toString();
        String reEnterNewPasswordText = reEnterNewPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(currentPasswordText) || TextUtils.isEmpty(newPasswordText) || TextUtils.isEmpty(reEnterNewPasswordText)) {
            Toast.makeText(getActivity(), "Please enter all fields", Toast.LENGTH_SHORT).show();
        } else if (newPasswordText.length() < 6) {
            Toast.makeText(getActivity(), "Password too short!", Toast.LENGTH_SHORT).show();
        } else if (!newPasswordText.equals(reEnterNewPasswordText)) {
            Toast.makeText(getActivity(), "Passwords must match!", Toast.LENGTH_SHORT).show();
        } else {
            showConfirmationDialog(currentPasswordText, newPasswordText);
        }
    }
    private void showConfirmationDialog(final String currentPasswordText, final String newPassword) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change Password");
        builder.setMessage("Are you sure you want to change the password?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // User clicked Yes, handle password change
            updatePassword( currentPasswordText ,newPassword);
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // User clicked No, do nothing or provide feedback
            Toast.makeText(getActivity(), "Password change canceled", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }
    private void updatePassword(String currentPasswordText ,String newPassword) {
        // Implement your logic to update the password using Firebase Auth here
        // For example, you can use FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPassword);
        // Show loading indicator while updating password
        FirebaseUser user = auth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),currentPasswordText );
        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Password change successfully!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        View view = getView() ;
                        assert view != null;
                        Navigation.findNavController(view).popBackStack();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        progressBar.setVisibility(View.VISIBLE);
    }

}