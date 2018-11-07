package com.petergeras.cameraandcloud.loginSignUp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.Network;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import com.petergeras.cameraandcloud.R;

public class OpeningActivity extends AppCompatActivity implements View.OnClickListener {

    private LoginFragment loginFragment;
    private SignUpFragment signUpFragment;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);



        loginFragment = new LoginFragment();
        signUpFragment = new SignUpFragment();


        setFragment(loginFragment);


        // From line 44-60, the code listens if the the internet connection is available or not.
        ConnectivityManager cm = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        cm.registerDefaultNetworkCallback( new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {

            }

            // If there is no internet then a Snackbar will appear stating there is no internet.
            @Override
            public void onLost(Network network) {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.openingFrame), "Sorry! No internet connection", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

    }


    // When the TextView (below the sign-in/sign-up button) is pressed then the user can change
    // between fragments
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.signin:
                setFragment(loginFragment);
                break;


            case R.id.signup:
                setFragment(signUpFragment);
                break;

        }

    }


    // The below method helps distinguish which fragment the user wants to go to based on if the
    // user presses the TextView (below the sign-in/sign-up button).
    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.openingFrame, fragment);
        fragmentTransaction.commit();
    }



    // The method below is for Fragments allows the user to lose focus of the keyboard if they click
    // outside the EditText zone. In XML, android:focusableInTouchMode="true", was added to each
    // EditText so the function can work.
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
