package com.example.test.ui.user;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test.R;
import com.example.test.firebase.MainAuthentication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class ModifyUserFragment extends Fragment {

    EditText editTextMailUser;
    EditText editTextUsername;
    Button buttonConfirmUser;
    EditText editTextNewPasswordConfirm;
    EditText editTextNewPassword;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String name;

    public ModifyUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name = getArguments().getString("name");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
        View root =  inflater.inflate(R.layout.fragment_modify_user, container, false);

        editTextMailUser = root.findViewById(R.id.editTextMailUser);
        editTextMailUser.setText(uid.getEmail());
        editTextUsername = root.findViewById(R.id.editTextUsername);
        editTextNewPasswordConfirm = root.findViewById(R.id.editTextNewPasswordConfirm);
        editTextNewPassword = root.findViewById(R.id.editTextNewPassword);
        buttonConfirmUser = root.findViewById(R.id.buttonConfirmUser);
        editTextUsername.setText(name);

        buttonConfirmUser.setOnClickListener(v -> {
            if (checkField()) {
                uid.updateEmail(editTextMailUser.getText().toString().trim());
                db.collection("users").document(uid.getUid()).update("name",editTextUsername.getText().toString().trim());
                if (!editTextNewPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "Failed to login! Try again!", Toast.LENGTH_LONG).show();
                    uid.updatePassword(editTextNewPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("MSG", "task not failed successfuly");
                            }
                        }
                    });
                }
                Toast.makeText(getActivity(), "Modification successful", Toast.LENGTH_SHORT);
            }

        });



        return root;
    }


    private boolean checkField() {
        if(editTextMailUser.getText().toString().trim().isEmpty()){
            editTextMailUser.setError("Email is required!");
            editTextMailUser.requestFocus();
            return false;
        }
        if(editTextUsername.getText().toString().trim().isEmpty()){
            editTextUsername.setError("Name is required!");
            editTextUsername.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(editTextMailUser.getText().toString().trim()).matches()){
            editTextMailUser.setError("Please, provide valid email!");
            editTextMailUser.requestFocus();
            return false;
        }
        if(!editTextNewPassword.getText().toString().trim().isEmpty()){
            if(editTextNewPassword.getText().toString().trim().length() <3 ){
                editTextNewPassword.setError("Minimum password length should be 3 characters!");
                editTextNewPassword.requestFocus();
                return false;
            }
            if (!editTextNewPasswordConfirm.getText().toString().trim().equals(editTextNewPassword.getText().toString().trim())) {
                editTextNewPasswordConfirm.setError("Passwords are not the same!");
                editTextNewPasswordConfirm.requestFocus();
                return false;
            }
        }
        return true;
    }
}