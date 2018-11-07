package com.petergeras.cameraandcloud.inApp;


import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petergeras.cameraandcloud.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PhotoFragment extends Fragment {


    private ImageView sendBtn;
    private EditText commentET;

    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRef;

    private String uid;

    private List<Object> commentList = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;



    public PhotoFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        // back button
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);


        sendBtn = view.findViewById(R.id.sendBtn);
        commentET = view.findViewById(R.id.comments_ET);
        recyclerView = view.findViewById(R.id.rvList);




        adapter = new RecyclerViewAdapter(commentList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);



        sendBtnActive();




        // The method listens to the EditText in the PhotoFragment to see if the user is typing.
        // Once the user is typing it will change the button fade to its original color and enable
        // the send button to Firebase.
        commentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

               }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                sendBtnActive();

            }
        });




        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseAuth = FirebaseAuth.getInstance();
                uid = firebaseAuth.getCurrentUser().getUid();

                database = FirebaseDatabase.getInstance();
                //databaseRef = database.getReference(uid);
                databaseRef = database.getReference();
                databaseRef = databaseRef.child("images");


                final DatabaseReference ref = databaseRef.child(MainActivity
                        .selectedUploadInfoWithKey.getKey()).child("comments");

                String uid = firebaseAuth.getCurrentUser().getUid();
                String email = firebaseAuth.getCurrentUser().getEmail();
                String comment = commentET.getText().toString();

                String key = ref.push().getKey();
                Comment info = new Comment(uid, email, comment, System.currentTimeMillis());
                ref.child(key).setValue(info);

                commentET.getText().clear();
            }
        });

        return view;
    }


    // When the user is typing in the EditText in PhotoFragment
    public void sendBtnActive() {

        String commentEmpty = commentET.getText().toString().trim();

        // If the EditText is empty, the send button is disabled and it is faded
        if (commentEmpty.isEmpty()){
            sendBtn.setEnabled(false);
            sendBtn.setAlpha(0.5f);
        }
        // If the EditText has text written in, the send button is enabled and is not faded.
        else {
            sendBtn.setEnabled(true);
            sendBtn.setAlpha(1.0f);
        }
    }


    // Retrieving data from Firebase
    public void retrievingData(final FragmentTransaction fragmentTransaction){


        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
        databaseRef = databaseRef.child("images");


        databaseRef.child(MainActivity.selectedUploadInfoWithKey.getKey()).addValueEventListener (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Make sures the comments are clear before uploading the comments from the
                // Firebase. If this was not done then the comments that were loaded from a previous
                // time would show and overlap, displaying multiple comments (many the same) that
                // should not be shown.
                commentList.clear();

                UploadInfo uploadInfo = dataSnapshot.getValue(UploadInfo.class);

                if(uploadInfo == null)
                    return;


                PhotoInfo photoInfo = new PhotoInfo();

                // Creates a date and time when the photo was uploaded
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy  HH:mm");
                String dateString = sdf.format(uploadInfo.getDate());


                // Sets email, date, and likes to the photoInfo and then the adapter.
                photoInfo.setEmail(uploadInfo.getEmail());
                photoInfo.setDate(dateString);
                photoInfo.setLiked(false);
                commentList.add(photoInfo);


                // Sets if there are likes on the photo
                if (uploadInfo.getLikes() == null){
                    photoInfo.setLikeCountValue(0);
                }
                else {
                    photoInfo.setLikeCountValue(uploadInfo.getLikes().size());

                    // If the current user has liked the post
                    if(uploadInfo.getLikes().containsKey(uid)){
                        photoInfo.setLiked(true);
                    }
                }

                // Sets if there are comments on the photo
                if (uploadInfo.getComments() == null){
                    photoInfo.setCommentCountValue(0);
                }
                else {
                    photoInfo.setCommentCountValue(uploadInfo.getComments().size());

                    // Sorting the comments based on date and time which is coded in CommentComparator
                    ArrayList<Comment> comments = new ArrayList<>(uploadInfo.getComments().values());

                    Collections.sort(comments, new CommentComparator());

                    commentList.addAll(comments);
                }

                // Alerts the adapter of data changes
                if(adapter!=null)
                    adapter.notifyDataSetChanged();


                // If the fragmentTransaction is not null and the recyclerView is null then the user
                // can go back to the HomeFragment. If this is not stated then an error will appear
                // crashing the app when retrievingData method is called from MainActivity and the
                // Fragment is not yet ready.
                if(fragmentTransaction != null && recyclerView == null){
                    fragmentTransaction.add(R.id.mainFrame, PhotoFragment.this)
                            .addToBackStack("HomeFragment")
                            .commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // When the user presses the back button in the action bar, the back button will be destroyed
    // once the user is in the HomeFragment
    @Override
    public void onDestroy() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        super.onDestroy();
    }



    // Ordering of the comments based on date it was posted
    class CommentComparator implements Comparator<Comment> {
        @Override
        public int compare(Comment o1, Comment o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }

}
