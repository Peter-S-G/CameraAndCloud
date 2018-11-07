package com.petergeras.cameraandcloud.inApp;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petergeras.cameraandcloud.R;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private GridView gridView;
    private GridViewAdapter gridViewAdapter;

    private ArrayList<UploadInfoWithKey> infoList;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);



        gridView = view.findViewById(R.id.gridViewHome);

        infoList = new ArrayList<>();

        gridViewAdapter = new GridViewAdapter(HomeFragment.this.getActivity(), infoList);
        gridView.setAdapter(gridViewAdapter);

        retrieveDatabase();



        return view;
    }

    // This method retrieves the data from Firebase Database and puts the images in the GridView
    public void retrieveDatabase() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference().child("images");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                infoList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    String key = postSnapshot.getKey();
                    UploadInfo uploadInfo = postSnapshot.getValue(UploadInfo.class);
                    infoList.add(new UploadInfoWithKey(key, uploadInfo));
                }
                gridViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
