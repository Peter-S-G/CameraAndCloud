package com.petergeras.cameraandcloud.loginSignUp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.petergeras.cameraandcloud.inApp.MainActivity;
import com.petergeras.cameraandcloud.R;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        buttonSignIn = view.findViewById(R.id.buttonLogin);
        editTextEmail = view.findViewById(R.id.emailLI);
        editTextPassword = view.findViewById(R.id.passwordLI);


        progressDialog = new ProgressDialog(LoginFragment.this.getActivity());

        firebaseAuth = FirebaseAuth.getInstance();

        buttonSignIn.setOnClickListener(this);


        if (firebaseAuth.getCurrentUser() != null){
            // Profile activity here
            startActivity(new Intent (getActivity(), MainActivity.class));

        }
        return view;
    }


    @Override
    public void onClick(View v) {

        if (v == buttonSignIn){
            userLogin();
        }
    }


    // User logging in
    private void userLogin(){

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Email is empty
        if (TextUtils.isEmpty(email)){
            Toast.makeText(LoginFragment.this.getActivity(),
                    "Please enter email", Toast.LENGTH_LONG).show();
            // Stopping the function from executing further
            return;
        }

        // Password is empty
        if (TextUtils.isEmpty(password)){
            Toast.makeText(LoginFragment.this.getActivity(),
                    "Please enter password", Toast.LENGTH_LONG).show();
            // Stopping the function from executing further
            return;
        }

        progressDialog.setMessage("Signing In");
        progressDialog.show();

        // This method signs in a user who already has signed up to the app. If login is successful
        // then the user will shown the HomeFragment in the MainActivity
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginFragment.this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            startActivity(new Intent (getActivity(), MainActivity.class));
                        }
                    }
                });
    }
}
