package com.petergeras.cameraandcloud.inApp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.petergeras.cameraandcloud.loginSignUp.OpeningActivity;
import com.petergeras.cameraandcloud.R;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mainNav;

    private HomeFragment homeFragment;
    private CameraFragment cameraFragment;
    private PhotoFragment photoFragment;
    private NoInternet noInternet;

    private Fragment activeFragment;


    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    public static Drawable selectedImageView = null;

    public static UploadInfoWithKey selectedUploadInfoWithKey = null;

    private Menu menu;


    private static String TAG = "TTT";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainNav = findViewById(R.id.mainNav);

        homeFragment = new HomeFragment();
        cameraFragment = new CameraFragment();
        photoFragment = new PhotoFragment();

        noInternet = new NoInternet();

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();

        // Initialize that the homeFragment is the first fragment that is opened.
        activeFragment = homeFragment;



        // From line 85 to 103, the code listens if the the internet connection is available or not.
        ConnectivityManager cm = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        cm.registerDefaultNetworkCallback( new ConnectivityManager.NetworkCallback() {
            @Override
            // If the internet was lost and then regained then the fragment the user was using will
            // appear again.
            public void onAvailable(Network network) {
                Log.i(TAG, "INTERNET CONNECTED");
                setFragment(activeFragment);
            }

            @Override
            // If there is no internet then the fragment noInternet will appear
            public void onLost(Network network) {
                Log.i(TAG, "INTERNET LOST");
                setFragment(noInternet);
            }
        });

        // Sets the bottom navigation control as visible
        mainNav.setVisibility(View.VISIBLE);


        firebaseAuth = FirebaseAuth.getInstance();

        // If the user is not signed in then the sign-in/sign-up page will appear
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, OpeningActivity.class));
        }


        // The bottom navigation control has two (2) buttons the user can click on and using
        // setFragment method sets the fragment. When the user clicks on one of the buttons, it
        // makes the fragment the user is in into the activeFragment so if the user loses internet,
        // the user can come back to the same page when the internet returns
        mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.nav_home:
                        mainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(homeFragment);
                        activeFragment = homeFragment;
                        return true;


                    case R.id.nav_camera:
                        mainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(cameraFragment);
                        activeFragment = cameraFragment;
                        return true;


                    default:
                        return false;
                }
            }
        });
    }


    // The below method helps distinguish which fragment the user wants to go to based on
    // mainNav.setOnNavigationItemSelectedListener
    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();
    }


    // Once a photo is clicked in the GridView, then the PhotoFragment will appear.
    public void moveToPhotoFragment(){

        Fragment fragment = getFragmentManager().findFragmentById(R.id.mainFrame);

        // If the user did not upload the image, then the delete button in the menu will not appear.
        this.menu.findItem(R.id.menuDelete).setVisible(false);

        if (HomeFragment.class.isInstance(fragment)) {

            photoFragment = new PhotoFragment();

            activeFragment = photoFragment;

            // Getting data from PhotoFragment
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            photoFragment.retrievingData(fragmentTransaction);


            String currentUser = firebaseAuth.getCurrentUser().getEmail();

            // If the user did upload the image, then the delete button in the menu will appear.
            if (currentUser.equals(selectedUploadInfoWithKey.getValue().getEmail())){
                this.menu.findItem(R.id.menuDelete).setVisible(true);
            }

            // When the user is in PhotoFragment then the bottom navigation control will be
            // invisible.
            mainNav.setVisibility(View.INVISIBLE);
        }
    }

    // The method uses the layout XML from the menu layout. The delete button is invisible unless
    // the user is in PhotoFragment and the photo the user is on is one that s/he uploaded.
  @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_and_logout, menu);

        this.menu = menu;
        this.menu.findItem(R.id.menuDelete).setVisible(false);

        return true;
    }


    // The method below distinguish which actions will be taken when one of the buttons is clicked.
    // If the user wants to log out, it will take the user back to the LoginFragment and alerting
    // Firebase the user is logged off. If the user wants to delete an image s/he uploaded, then
    // Firebase is alerted and deletes the photo.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, OpeningActivity.class));
                break;

            case R.id.menuDelete:
                databaseRef.child("images").child(selectedUploadInfoWithKey.getKey()).removeValue();
          }
        onBackPressed();

        return true;
    }


    // When the user presses the back button on the top left corner, the bottom navigation control
    // will appear and the delete button in menu will not be visible.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mainNav.setVisibility(View.VISIBLE);
        this.menu.findItem(R.id.menuDelete).setVisible(false);
    }


    // If the user is in a EditText and clicks outside the focus, then the keyboard will disappear
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

