package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment.helper.CallHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Objects;

public class LoginActivity extends AppCompatActivity {


    private EditText email,password;
    private Button login;
    private FirebaseAuth auth;
    private Button register;
    private static final int REQUEST_CODE = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email_loginActivity);
        password = findViewById(R.id.password_loginActivity);
        login = findViewById(R.id.login_loginActivity);
        register = findViewById(R.id.openRegister);
        ImageView callPolice = findViewById(R.id.callPolice);
        ImageView callAmbulance = findViewById(R.id.callAmbulance);
        ImageView callBomba = findViewById(R.id.callBomba);
        auth = FirebaseAuth.getInstance();

        //TODO: View Onclicklistener is never used (needs to be removed)

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
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                if(TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText)){
                    Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                } else if(passwordText.length() < 6) {
                    Toast.makeText(LoginActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
                }else{
                    loginUser(emailText,passwordText);
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
        TextView forgotPassword = findViewById(R.id.forgotPassword);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click on the "Forgot Password" text
                // For simplicity, let's redirect the user to Firebase's default password recovery flow
                String emailText = email.getText().toString().trim();

                if (TextUtils.isEmpty(emailText)) {
                    Toast.makeText(LoginActivity.this, "Enter your registered email ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send password reset email
                auth.sendPasswordResetEmail(emailText)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("LoginActivity", "onComplete: " + emailText);
                                    Toast.makeText(LoginActivity.this, "Password reset email sent. Check your email.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Failed to send password reset email. Please check your email address.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null && isNetworkAvailable(this)){
            startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }else{

        }
    }


    private void loginUser(String emailText, String passwordText) {


        auth.signInWithEmailAndPassword(emailText,passwordText).addOnSuccessListener(LoginActivity.this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LoginActivity.this, "Login successful!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(LoginActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Wrong email or password!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkUsernameChildExists() {
        String currentUserID = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        DatabaseReference userNodeRef = FirebaseDatabase.getInstance("https://assignment-1c692-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users").child(currentUserID);
        userNodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("username")) {
                    // User has a username child, start MainActivity

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    // User doesn't have a username child, start SetupActivity
                    startActivity(new Intent(LoginActivity.this, SetupActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error

                Toast.makeText(LoginActivity.this, "Error checking username: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void makeEmergencyCall(View view) {
        // Get the ID of the clicked button
        int buttonId = view.getId();

        if (buttonId == R.id.callPolice) {
            CallHelper.callService(LoginActivity.this, "999");
        } else if (buttonId == R.id.callAmbulance) {
            CallHelper.callService(LoginActivity.this, "999");
        } else if (buttonId == R.id.callBomba) {
            CallHelper.callService(LoginActivity.this, "994");
        }

    }



}
