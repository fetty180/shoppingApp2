package com.example.shoppingapptest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class userProfile extends AppCompatActivity {

    private Button gallery,camera,home,logout;
    private TextView FName, SName,Email, userName, dob, phone;
    public Uri imageUri;
    private ImageView circleImageView;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private String userId;
    private FirebaseFirestore fStore;
    private Uri doenloadUri = null;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userId = mAuth.getCurrentUser().getUid();



        gallery = findViewById(R.id.galleryBtn);
        circleImageView = findViewById(R.id.profileImageUser);
        FName = findViewById(R.id.userFirstName);
        SName = findViewById(R.id.userSecondName);
        Email = findViewById(R.id.userEmail);

        home = findViewById(R.id.backHomePg);
        logout = findViewById(R.id.logoutBT);


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(userProfile.this, MyHomePage.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(userProfile.this, MainActivity.class));
            }
        });




        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 1000);
            }
        });


        fStore.collection("user profile image").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String imageUri = task.getResult().getString("image");
                        Glide.with(userProfile.this).load(imageUri).into(circleImageView);
                    }
                }
            }
        });


        fStore.collection("user details").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String email = task.getResult().getString("Email");
                        Email.setText(email);
                        String fname = task.getResult().getString("First name");
                                FName.setText(fname);
                        String sname = task.getResult().getString("Second name");
                        SName.setText(sname);



                    }
                }
            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            if (resultCode== Activity.RESULT_OK){
                imageUri =data.getData();
               circleImageView.setImageURI(imageUri);
                uploadImageToFirebase(imageUri);


            }
        }

    }

    private void uploadImageToFirebase(Uri imageUri) {
        //upload image to firebase
        StorageReference fileRef = storageReference.child("profile pic").child(userId+".jpg");
        fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    saveToFirebase(task, fileRef);
                }

            }
        });

    }

    private void saveToFirebase(Task<UploadTask.TaskSnapshot> task, StorageReference fileRef) {
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                doenloadUri = uri;
                HashMap <String, Object> map = new HashMap<>();
                map.put("image", doenloadUri.toString());
                fStore.collection("user profile image").document(userId).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(userProfile.this, "image saved to fire base", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(userProfile.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

    }


}