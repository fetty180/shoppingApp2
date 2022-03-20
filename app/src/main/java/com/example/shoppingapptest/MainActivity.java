package com.example.shoppingapptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
private TextView register, forgotPassword;
private EditText editTextEmail, editTextPassword;
private Button signin;
private ProgressBar progressBar;



private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        register = findViewById(R.id.register);
        signin = findViewById(R.id.loginBtn);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        forgotPassword = findViewById(R.id.forgotpass);

        register.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:
                startActivity(new Intent(this,registerUser.class));
                break;
            case R.id.loginBtn:
                loggingIn();
                break;
            case R.id.forgotpass:
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));

        }

    }


    private void loggingIn() {
        String email = editTextEmail.getText().toString().trim();

        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()){
            editTextEmail.setError("input password");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("enter valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            editTextPassword.setError("Enter password");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length()< 6){
            editTextPassword.setError("Password too Short!");
            editTextPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // redirect to home page
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified())
                    {
                        startActivity(new Intent(MainActivity.this, MyHomePage.class));
                        progressBar.setVisibility(View.GONE);
                    }
                    else {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check your email to verify", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Failed to login! Please check your Credemtials", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

        });
    }
}