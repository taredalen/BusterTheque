package com.example.test.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.MainActivity;
import com.example.test.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private View decorView;
    private ProgressBar progressBar;
    private TextView button_register;
    private EditText editTextUserName, editTextEmail, editTextPassword;

    private FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uid;

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user);

        auth = FirebaseAuth.getInstance();

        button_register = (Button) findViewById(R.id.button_register);
        button_register.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.user_email);
        editTextUserName = (EditText) findViewById(R.id.user_name);
        editTextPassword = (EditText) findViewById(R.id.user_password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        decorView = getWindow().getDecorView(); // hide bar navigation & tool bar
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if(visibility == 0)
                decorView.setSystemUiVisibility(hideSystemBars());
        });
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_register) {
            registerUser();
        }
    }
    //----------------------------------------------------------------------------------------------
    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String name = editTextUserName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if(name.isEmpty()){
            editTextUserName.setError("Name is required!");
            editTextUserName.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please, provide valid email!");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length() <3 ){
            editTextPassword.setError("Minimum password length should be 3 characters!");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                User user = new User(name, email);
                uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference documentReference = db.collection("users").document(uid);
                documentReference.set(user).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        //Toast.makeText(RegisterUser.this,"User has been registered successfully!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.VISIBLE);
                        startActivity(new Intent(this, BusterActivity.class));
                    }
                    else {
                        Toast.makeText(RegisterUser.this,"Failed to register! Try again!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } else {
                Toast.makeText(RegisterUser.this,"Failed to register!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }
    private int hideSystemBars(){
        return  SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
    }
    //----------------------------------------------------------------------------------------------
}