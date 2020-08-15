package com.maro.waka_well;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;
    String userEmail, userPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUpFirebaseAuth();

        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        Button login = findViewById(R.id.login_button);
        TextView register = findViewById(R.id.register);
        TextView resend = findViewById(R.id.resend_ver);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResendVerificationDialog dialog = new ResendVerificationDialog();
                dialog.show(getSupportFragmentManager(), "fragment_resend_verification_dialog");
            }
        });

        userEmail = email.getText().toString();
        userPass = password.getText().toString();

        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                } else {
                    signIn();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpFirebaseAuth(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    if (user.isEmailVerified()){
                        Toast.makeText(LoginActivity.this, "authenticated with: " + user.getEmail(),
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, Homepage.class);
                        startActivity(intent);
                    } else {
                        mAuth.signOut();
                        Toast.makeText(LoginActivity.this, "Please verify email address",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }

    private void signIn(){
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, Homepage.class);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Login failed: " + e,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
       mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null){
           mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}