package com.example.test.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.test.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserFragment extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView textViewMail;
    TextView textViewUsername;
    TextView textViewCreationDate;
    Button buttonUser;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_user, container, false);
        textViewMail = root.findViewById(R.id.textViewMail);
        textViewUsername = root.findViewById(R.id.textViewUsername);
        textViewCreationDate = root.findViewById(R.id.textViewCreationDate);

        FloatingActionButton buttonUser = root.findViewById(R.id.buttonUser);

        FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
        uid.getMetadata().getCreationTimestamp();
        Date date = new Date(uid.getMetadata().getCreationTimestamp());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(uid.getMetadata().getCreationTimestamp());
        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
        textViewCreationDate.setText(f1.format(cal.getTime()));
        textViewUsername.setText(uid.getDisplayName());
        textViewMail.setText(uid.getEmail());

        Bundle b = new Bundle();

        db.collection("users").document(uid.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String name = task.getResult().getString("name");
                textViewUsername.setText(name);
                b.putString("name", name);

            }
            buttonUser.setOnClickListener(v -> {
                Navigation.findNavController(root).navigate(R.id.action_nav_profile_to_user_modify, b);
            });
        });




        return root;
    }
}