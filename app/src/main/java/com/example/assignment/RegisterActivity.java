package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {


    private EditText email;
    private EditText password,confirmPassword;
    private Button register;
    private FirebaseAuth auth;
    private ProgressBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = findViewById(R.id.register);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        email = findViewById(R.id.email);
        loadingBar = findViewById(R.id.progressBar);
        

        auth = FirebaseAuth.getInstance();
        register.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                String confirmPasswordText = confirmPassword.getText().toString();
                if(TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText)){
                    Toast.makeText(RegisterActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                } else if(passwordText.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
                }else if(!passwordText.equals(confirmPasswordText)) {
                    Toast.makeText(RegisterActivity.this, "Password must be same!", Toast.LENGTH_SHORT).show();
                }else{
                    loadingBar.setVisibility(View.VISIBLE);
                    registerUser(emailText,passwordText);
                }


            }
        });
    }

    private void registerUser(String emailText, String passwordText) {
        // firebase implementation
        auth.createUserWithEmailAndPassword(emailText,passwordText).addOnCompleteListener(RegisterActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Toast.makeText(RegisterActivity.this, "Registering user successful!!", Toast.LENGTH_SHORT).show();
                    loadingBar.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(RegisterActivity.this, SetupActivity.class));
                    finish();
                }else{
                    loadingBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterActivity.this, "Fail! ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}