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

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;



    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);


        buttonRegister = view.findViewById(R.id.buttonReg);
        editTextEmail = view.findViewById(R.id.emailSI);
        editTextPassword = view.findViewById(R.id.passwordSI);


        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(SignUpFragment.this.getActivity());

        buttonRegister.setOnClickListener(this);

        if (firebaseAuth.getCurrentUser() != null){
            // Profile activity here
            startActivity(new Intent (getActivity(), MainActivity.class));

        }
        return view;
    }


    @Override
    public void onClick(View v) {

        if (v == buttonRegister){
            registerUser();
        }
    }



    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Email is empty
        if (TextUtils.isEmpty(email)){
            Toast.makeText(SignUpFragment.this.getActivity(),
                    "Please enter email", Toast.LENGTH_LONG).show();
            // Stopping the function from executing further
            return;
        }

        // Password is empty
        if (TextUtils.isEmpty(password)){
            Toast.makeText(SignUpFragment.this.getActivity(),
                    "Please enter password", Toast.LENGTH_LONG).show();
            // Stopping the function from executing further
            return;
        }

        progressDialog.setMessage("Registering, Please Wait");
        progressDialog.show();

        // The method below creates a new user into the Firebase. If login is successful then the
        // user will shown the HomeFragment in the MainActivity
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpFragment.this.getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                // If login is successful then the user will shown the HomeFragment in the MainActivity
                if (task.isSuccessful()){
                    startActivity(new Intent (getActivity(), MainActivity.class));

                }
                else {
                    Toast.makeText(SignUpFragment.this.getActivity(),
                            "Could not register...Please try again " + task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();

                }
            }
        });
    }
}
