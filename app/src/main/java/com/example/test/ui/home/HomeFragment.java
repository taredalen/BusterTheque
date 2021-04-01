package com.example.test.ui.home;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.SecondActivity;
import com.example.test.firebase.MainAuthentication;
import com.example.test.firebase.RegisterUser;

@RequiresApi(api = Build.VERSION_CODES.P)


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
public static final String EXTRA_MESSAGE = "com.example.helloworld.MESSAGE";


public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

    return root;

    }

    //public void sendMessage(View view) {
    //    Intent intent = new Intent(getActivity(), SecondActivity.class);
    //    startActivity(intent);
    //}
}