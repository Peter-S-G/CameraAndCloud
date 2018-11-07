package com.petergeras.cameraandcloud.inApp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.petergeras.cameraandcloud.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class CameraFragment extends Fragment {

    private RelativeLayout takePhoto;
    private RelativeLayout uploadPhoto;

    public static final int CAMERA_REQUEST = 99;
    public static final int IMAGE_GALLERY_REQUEST = 20;

    private Intent photoIntent;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imagesRef;

    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRef;

    private String uid;

    private ProgressBar progressBar;

    private View homeButton;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);


        takePhoto = view.findViewById(R.id.camera);
        uploadPhoto = view.findViewById(R.id.upload);



        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference(uid);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);


        homeButton = getActivity().findViewById(R.id.nav_home);



        // When you press the Take Photo button, the function will check if the permission is given
        // to use the camera, then open the camera if the permission is met.
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, CAMERA_REQUEST);
                }
            }
        });

        // When you click Upload Photo, the function onImageGalleryClicked will be called.
        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageGalleryClicked(v);
            }
        });
        return view;
    }



    // When the Upload Photo is clicked, the function looks for permission to access the phone's
    // image gallery. Once given, the user can then go to the gallery and select an image. ios is a lot better
    public void  onImageGalleryClicked(View view) {

        photoIntent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        Uri data = Uri.parse(pictureDirectoryPath);

        photoIntent.setDataAndType(data, "image/*");

        photoIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(photoIntent, IMAGE_GALLERY_REQUEST);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {


            //TAKE PHOTO
            if (requestCode == CAMERA_REQUEST) {

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                settingUpStorageAndDatabase(bitmap);


            }

            //UPLOAD PHOTO
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                Uri imageUri = data.getData();

                try {
                    InputStream inputStream = getActivity().getContentResolver()
                            .openInputStream(imageUri);

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    settingUpStorageAndDatabase(bitmap);



                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(CameraFragment.this.getActivity(), "Unable to open image",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    // The method below is setting up the Firebase Storage and Database
    public void settingUpStorageAndDatabase (Bitmap bitmap){

        // Creates a random ID number
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();


        // The random ID number will be assigned to an imaged that is uploaded to Firebase
        imagesRef = storageRef.child(uid + "/" + "images/" + randomUUIDString + ".jpg");


        // The ByteArrayOutputStream holds the data of the image before it is sent to Firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();


        // UploadTask will help determine if sending the image to
        final UploadTask uploadTask = imagesRef.putBytes(data);

        // Image was unable to be sent to Firebase
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                homeButton.setEnabled(true);
                // If the image is not able to be sent to Firebase, a toast will appear
                Toast.makeText(CameraFragment.this.getActivity(), "Unable to upload image",
                        Toast.LENGTH_LONG).show();
            }


            // Image was able to be sent to Firebase
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.INVISIBLE);
                homeButton.setEnabled(true);

                Toast.makeText(CameraFragment.this.getActivity(), "Image uploaded",
                        Toast.LENGTH_LONG).show();


                // When the image is uploaded, the Firebase Database will display the date,
                // the user who uploaded the image email, the name of the image file, the user ID code, and the
                final String name = taskSnapshot.getMetadata().getName();
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {


                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        String uid = firebaseAuth.getCurrentUser().getUid();

                        databaseRef = FirebaseDatabase.getInstance().getReference();

                        databaseRef = databaseRef.child("images");

                        long date = System.currentTimeMillis();

                        String email = firebaseAuth.getCurrentUser().getEmail();

                        String key = databaseRef.push().getKey();
                        UploadInfo info = new UploadInfo(name, uri.toString(), uid, date, email);
                        databaseRef.child(key).setValue(info);

                    }
                });
            }
            // When the image is being uploaded, a progress circle will appear and the user will
            // not be able to go to the home screen as the home button will be disable
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                progressBar.setVisibility(View.VISIBLE);
                homeButton.setEnabled(false);



            }
        });
    }
}
