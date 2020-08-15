package com.maro.waka_well;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    private EditText email, firstName, lastName, dob, phone;
    private EditText password, confirmPassword;
    private Button signUp;
//    private TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        email = findViewById(R.id.emailSignUp);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        dob = findViewById(R.id.dob);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.passwordSignUp);
        confirmPassword = findViewById(R.id.confirmPassword);
        signUp = findViewById(R.id.sign_up_button);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Please enter your credentials",
                            Toast.LENGTH_SHORT).show();
                } else if (!password.getText().toString().equals(confirmPassword.getText().toString())){
                    Toast.makeText(SignUpActivity.this, "Passwords dob't match",
                            Toast.LENGTH_SHORT).show();
                } else {
                    registerNewUser(email.getText().toString(), password.getText().toString());
                }
            }
        });
    }

    private void registerNewUser(String mEmail, String mPassword){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mEmail,mPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            User user = new User();
                            user.setFirstname(firstName.getText().toString());
                            user.setLastname(lastName.getText().toString());
                            user.setEmail(email.getText().toString());
                            user.setPhone(phone.getText().toString());
                            user.setProfile_image("");
                            user.setDob(dob.getText().toString());
                            user.setUser_id(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

                            FirebaseDatabase.getInstance().getReference()
                                    .child("users")
                                    .child(mAuth.getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(SignUpActivity.this,
                                            "User registration successful" + "\nUser info saved",
                                            Toast.LENGTH_SHORT).show();
                                    sendVerEmail();
                                    mAuth.signOut();
                                    goToLoginScreen();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpActivity.this,
                                            "error " + e,
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "error: " + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendVerEmail(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this,
                                        "Email verification sent, Check email for link",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this, "Could not send email: " + e,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void goToLoginScreen(){
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}