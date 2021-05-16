package com.example.test.ui.film;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.example.test.firebase.MainAuthentication;
import com.example.test.firebase.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;


public class MovieAdd extends Fragment implements View.OnClickListener {

    public String imbdID, title, year, stringNote, country, uid, collection;
    public TextView textMovieTitle, textMovieYear;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public Button buttonAdd, buttonDelete, buttonEdit, buttonShare, buttonMark, buttonRating;
    public EditText editTextNote;

    public String[] listItems;
    public boolean[] checkedItems;
    public List<String> selectedItems;

    public MovieAdd() { }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imbdID = getArguments().getString("imdbID");
            title = getArguments().getString("title");
            year = getArguments().getString("year");
            country = getArguments().getString("country");
            collection = getArguments().getString("collection");

            listItems = new String[]{"watched", "wishlist"};
            checkedItems  = new boolean[listItems.length];
            selectedItems = Arrays.asList(listItems);

            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if(!collection.equals("null")){
                updateNote();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_movie_add, container, false);
        textMovieTitle = view.findViewById(R.id.textMovieTitle);
        textMovieYear = view.findViewById(R.id.textMovieYear);

        textMovieTitle.setText(title);
        textMovieYear.setText(year);

        buttonAdd = view.findViewById(R.id.add_button);
        buttonDelete = view.findViewById(R.id.del_button);
        buttonEdit = view.findViewById(R.id.edit_button);
        buttonShare = view.findViewById(R.id.share_button);
        buttonMark = view.findViewById(R.id.bookmark);
        buttonRating = view.findViewById(R.id.rating);

        buttonAdd.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
        buttonEdit.setOnClickListener(this);
        buttonShare.setOnClickListener(this);
        buttonRating.setOnClickListener(this);

        editTextNote = view.findViewById(R.id.editTextNote);

        TextView textView = view.findViewById(R.id.date);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        textView.setText(currentDateandTime);

        if(collection.equals("null")){
            buttonDelete.setBackgroundResource(R.drawable.trash_grey);
            buttonEdit.setBackgroundResource(R.drawable.check_grey);
            buttonShare.setBackgroundResource(R.drawable.share_grey);
        }
        else{
            buttonMark.setBackgroundResource(R.drawable.bookmark_yellow);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_button:
                addMovie();
                break;
            case R.id.del_button:
                deleteMovie();
                break;
            case R.id.edit_button:
                editMovie();
                break;
            case R.id.rating:
                ratingMovie();
                break;
        }
    }

    public void updateNote(){
        db.collection("users").document(uid).collection(collection).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if(document.getId().equals(imbdID)){
                        Log.d("DOCS", document.getId() + " => " + document.getString("note"));
                        editTextNote.setText(document.getString("note"));
                        String note = document.getString("note");
                        editTextNote.setText(note);
                        Log.d("UPD", note);
                    }
                }
            } else {
                Log.d("DOCS", "Error getting documents: ", task.getException());
            }
        });
    }

    public void addMovie() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add to collection : ");

        builder.setMultiChoiceItems(listItems, checkedItems, (dialog, which, isChecked) -> {
            checkedItems[which] = isChecked;
            String currentItem = selectedItems.get(which);
        });
        builder.setPositiveButton("Done", (dialog, which) -> {
            for (int i = 0; i < checkedItems.length; i++) {
                if (checkedItems[i]) {
                    if (MainAuthentication.user == null) {
                        Toast.makeText(getActivity(), "No user is signed in", Toast.LENGTH_LONG).show();
                    }
                    else {
                        stringNote = editTextNote.getText().toString().trim();
                        if(stringNote.length() == 0 ) stringNote = " ";

                        Movie movie = new Movie(imbdID, stringNote, "0", country, year);
                        DocumentReference documentReference =  db.collection("users").document(uid).collection(selectedItems.get(i)).document(imbdID);
                        documentReference.get().addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                documentReference.set(movie).addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        Toast.makeText(getActivity(), "Movie saved" , Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "Save erreur", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            else {
                                Toast.makeText(getActivity(), "Movie already saved!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> { });

        builder.create();

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteMovie() {
        db.collection("users").document(uid).collection(collection).document(imbdID).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    Toast.makeText(getActivity(), "Movie successfully deleted!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }

    private void editMovie(){
        String note = editTextNote.getText().toString().trim();

        db.collection("users").document(uid).collection(collection).document(imbdID)
                .update("note", note).addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                    Toast.makeText(getActivity(), "saved", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

    public void ratingMovie() {

        AlertDialog.Builder popDialog = new AlertDialog.Builder(getActivity());
        LinearLayout linearLayout = new LinearLayout(getActivity());
        RatingBar rating = new RatingBar(getActivity());

        linearLayout.setGravity(Gravity.CENTER);
        rating.setNumStars(5);
        rating.setStepSize(1);
        db.collection("users").document(uid).collection(collection).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if(document.getId().equals(imbdID)){
                        Log.d("DOCS", document.getId() + " => " + document.getString("rating"));
                        String str = document.getString("rating");
                        int number = Integer.parseInt(str);
                        //rating.setNumStars(number);
                        rating.setRating(number);
                    }
                }
            } else {
                Log.d("DOCS", "Error getting documents: ", task.getException());
            }
        });

        linearLayout.addView(rating);
        popDialog.setTitle("  Rate this film:  ");
        popDialog.setView(linearLayout);

        rating.setOnRatingBarChangeListener((ratingBar, v, b) -> System.out.println("Rated val:" + v));

        popDialog.setPositiveButton(android.R.string.ok,
                (dialog, which) -> {
                    String prog = String.valueOf(rating.getProgress());
                    Log.d(TAG, "value: " + prog);
                    dialog.dismiss();
                    db.collection("users").document(uid).collection(collection).document(imbdID)
                            .update("rating", prog).addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Toast.makeText(getActivity(), "rate saved", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                })
                .setNegativeButton("Cancel",
                        (dialog, id) -> dialog.cancel());

        popDialog.create();
        popDialog.show();

    }

    /*
    private void checkDocument(){

        Task<DocumentSnapshot> documentRefWatched =  db.collection("users").document(uid).collection("watched").document(imbdID).get();
        Task<DocumentSnapshot> documentRefWishlist=  db.collection("users").document(uid).collection("wishlist").document(imbdID).get();

        if (documentRefWatched.isSuccessful()) {
            collection = "watched";
        } else {
            if (documentRefWishlist.isSuccessful()) {
                collection = "wishlist";
            } else {
                collection = "null";
            }
        }
    }

    private void checkDocument(){

        AtomicReference<String> collection = null;
        db.collection("users").document(uid).collection("watched").document(imbdID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                collection.set("watched");
            } else {
                collection.set("null");
            }
        });
        db.collection("users").document(uid).collection("wishlist").document(imbdID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                collection.set("wishlist");
            } else {
                collection.set("null");
            }
        });
    }

    private String checkDocument(){
        DocumentReference documentReference =  db.collection("users").document(uid).collection("watched").document(imbdID);
        DocumentReference documentReference2 =  db.collection("users").document(uid).collection("wishlist").document(imbdID);

        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    collection.equals("watched");
                } else {
                    Log.d(TAG, "No such document in watched");
                    documentReference2.get().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            DocumentSnapshot document2 = task2.getResult();
                            if (document2.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document2.getData());

                                collection = "wishlist";
                            } else {
                                Log.d(TAG, "No such document in wishlist");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    });
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
        return collection;
    }*/
}
