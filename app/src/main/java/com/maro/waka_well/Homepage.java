package com.maro.waka_well;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class Homepage extends AppCompatActivity {

    private static final int PICTURE_RESULT = 42 ;
    FirebaseStorage firebaseStorage;
    DatabaseReference dbReference;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String userId;
    TextView welcomeText;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        welcomeText = findViewById(R.id.welcome);
        imageView = findViewById(R.id.imageView);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        dbReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        setupFirebaseAuth();

        dbReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = Objects.requireNonNull(snapshot.child("firstname").getValue()).toString();

                //Get image url from firebase and save it as a string
                String imageUrl = Objects.requireNonNull(snapshot.child("profile_image").getValue()).toString();

                //parse the image url string gotten from  firebase into picasso for display.
                showImage(imageUrl);

                welcomeText.setText("welcome " + firstName + " \n Your deals will show up here");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            case R.id.delete_account:
                deleteAccount();
                return true;
            case R.id.upload:
                uploadPhoto();
                return true;
        }
            return super.onOptionsItemSelected(item);

    }
//The intent here allows you select any image from your local directory irrespective of its extension/type
    private void uploadPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Insert Picture"), PICTURE_RESULT);
    }
    // This is where the image is uploaded. The onSuccess Listener is where the image link is saved to the profile image node on firebase.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK);
        assert data != null;
        final Uri imageUri = data.getData();
        //this specifies where the image should be stored. if the path is the same, It will keep replacing images uploaded by the same user.
        // You can specify a parameter to ensure that the path for each new image is different
//        StorageReference ref = firebaseStorage.getReference().child("images/users/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid() + "/");
        assert imageUri != null;
        StorageReference ref = firebaseStorage.getReference().child("images/users/" + imageUri.getLastPathSegment());
        ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                if (taskSnapshot.getMetadata() != null){
                    if (taskSnapshot.getMetadata().getReference() != null){
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                                        .child("profile_image")
                                        .setValue(imageUrl);
                                Toast.makeText(Homepage.this, "upload success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
//
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Homepage.this, "error " + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteAccount() {
       DeleteAccountDialog deleteAccountDialog = new DeleteAccountDialog();
       deleteAccountDialog.show(getSupportFragmentManager(),"fragment_delete_account");
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Intent intent = new Intent(Homepage.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else{
            Log.d("authstate: ", "checkAuthenticationState: user is authenticated.");
        }
    }

    private void setupFirebaseAuth(){
        Log.d("setup ", "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Log.d("cool: ", "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    Log.d("emmm: ", "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(Homepage.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void logout() {
        firebaseAuth.signOut();
    }

    private void showImage (String url) {
        if (url != null && !url.isEmpty()) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get().load(url).resize(width, width*2/3)
                    .centerCrop().into(imageView);

        }
    }
}