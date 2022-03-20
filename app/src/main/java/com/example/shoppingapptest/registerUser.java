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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class registerUser extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;

    private ImageView banner;
    private Button registerUser;
    private EditText editFirstName, editSecondName,editEmail,editPassword,reenterPassword;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private String userId;
    private TextView backToLogin;
    private Uri imageUri;
    private StorageReference storageReference;
   // public  String emailAddress = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        banner = findViewById(R.id.banner);


        registerUser = findViewById(R.id.registerBtn);
        registerUser.setOnClickListener(this);


        editFirstName = findViewById(R.id.firstName);
        editSecondName = findViewById(R.id.secondName);
        editEmail = findViewById(R.id.rgEmail);
        editPassword = findViewById(R.id.rgPassword);
        reenterPassword = findViewById(R.id.rgReenterPassword);
        backToLogin = findViewById(R.id.registerTxtView1);
        backToLogin.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);

       // emailAddress = editFirstName.getText().toString().trim();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registerBtn:
                registerUser();
        }


    }

    private void registerUser() {
        String fName = editFirstName.getText().toString().trim();
        String sName = editSecondName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String pass2 = reenterPassword.getText().toString().trim();



        if (fName.isEmpty()){
            editFirstName.setError("First name required");
            editFirstName.requestFocus();
            return;
        }


        if (sName.isEmpty()){
            editSecondName.setError("second name required");
            editSecondName.requestFocus();
            return;
        }


        if (email.isEmpty()){
            editEmail.setError("email required");
            editEmail.requestFocus();
            return;
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("invalid email");
            editEmail.requestFocus();
            return;
        }




        if (password.isEmpty()){
            editPassword.setError("enter password");
            editPassword.requestFocus();
            return;
        }
        if (password.length()< 6){
            editPassword.setError("enter long password");
            editPassword.requestFocus();
            return;
        }
        if (!pass2.equals(password)){
            reenterPassword.setError("incorrect password");
            reenterPassword.requestFocus();
            return;
        }
       // emailAddress = email;

        Map<String, Object> useData = new HashMap<>();

        useData.put("Email", email);
        useData.put("First name", fName);
        useData.put("Second name", sName);
        useData.put("time", FieldValue.serverTimestamp());


        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

              if (task.isSuccessful()){
                  userId = mAuth.getCurrentUser().getUid();
                  firestore.collection("user details").document(userId).set(useData).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          if (task.isSuccessful()){
                              Toast.makeText(registerUser.this, "user has been registered", Toast.LENGTH_LONG).show();
                              //redirecting to login layout!

                          }else {
                              Toast.makeText(registerUser.this, "failed to register user",Toast.LENGTH_LONG).show();
                          }
                          progressBar.setVisibility(View.GONE);


                      }
                  });
              }else {
                  Toast.makeText(registerUser.this, "failed to register user",Toast.LENGTH_LONG).show();
                  progressBar.setVisibility(View.GONE);
              }
            }
        });

    }


}