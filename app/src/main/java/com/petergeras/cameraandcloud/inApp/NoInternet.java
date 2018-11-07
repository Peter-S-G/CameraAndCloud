package com.petergeras.cameraandcloud.inApp;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.petergeras.cameraandcloud.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoInternet extends Fragment {


    public NoInternet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_no_internet, container, false);


        return view;
    }

}
