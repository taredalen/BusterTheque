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
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.MainActivity;
import com.example.test.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class MainAuthentication extends AppCompatActivity implements View.OnClickListener {

    private View decorView;
    private TextView register;
    private Button signIn, home;
    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    public static FirebaseUser user;

//----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        editTextPassword = (EditText) findViewById(R.id.password);
        editTextEmail = (EditText) findViewById(R.id.email); // wht why ???
        register = (TextView) findViewById(R.id.register);
        signIn = (Button) findViewById(R.id.signIn);
        home = (Button) findViewById(R.id.home);

        register.setOnClickListener(this);
        signIn.setOnClickListener(this);
        home.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();

        decorView = getWindow().getDecorView(); // hide bar navigation & tool bar
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if(visibility == 0)
                decorView.setSystemUiVisibility(hideSystemBars());


        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            System.out.println("User is signed in");
        } else {
            System.out.println("no ser is signed in");

        }
        //if (auth.getCurrentUser() != null) {
        //    startActivity(new Intent(MainAuthentication.this, MainActivity.class));
        //    finish();
        //}
    }
    public void onClick2(View view) { // TODO
        Intent i = new Intent(this, MainActivity.class);
    //    i.putExtra("key",value);
        startActivity(i);
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                System.out.println("register clicked");
                startActivity(new Intent(this, RegisterUser.class));
                break;
            case R.id.signIn:
                System.out.println("login clicked");
                userLogin();
                break;
            case R.id.home:
                System.out.println("home clicked");
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
    //----------------------------------------------------------------------------------------------
    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
        editTextEmail.setError("Email is required!");
        editTextEmail.requestFocus();
        return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        editTextEmail.setError("Please, enter valid email!");
        editTextEmail.requestFocus();
        return;
        }
        if (password.isEmpty()) {
        editTextPassword.setError("Password is required!");
        editTextPassword.requestFocus();
        return;
        }
        if (password.length() < 3) {
        editTextPassword.setError("Minimum password length should be 3 characters!");
        editTextPassword.requestFocus();
        return;
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainAuthentication.this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        user = auth.getCurrentUser();
                        Intent i = new Intent(MainAuthentication.this, MainActivity.class);
                        i.putExtra("id",user);
                        startActivity(i);
                        finish();

                    } else {
                        Toast.makeText(MainAuthentication.this, "Failed to login! Try again!", Toast.LENGTH_LONG).show();
                    }
                });
    }

       /* progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            //String uid = user.getUid();
            FirebaseUser currentUser = auth.getCurrentUser();
            Intent i = new Intent(MainAuthentication.this, MainActivity.class);
            i.putExtra("id",currentUser);
            startActivity(i);
        } else {
            Toast.makeText(MainAuthentication.this, "Failed to login! Try again!", Toast.LENGTH_LONG).show();
        }
    });*/

    //----------------------------------------------------------------------------------------------
    @Override
    public void onWindowFocusChanged(boolean hasFocus){  // hide bar navigation & tool bar
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
}


