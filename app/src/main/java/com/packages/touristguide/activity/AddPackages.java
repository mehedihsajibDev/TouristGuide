package com.packages.touristguide.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.packages.touristguide.R;

import java.io.IOException;

import dmax.dialog.SpotsDialog;

public class AddPackages extends AppCompatActivity {

    private ImageView imageView;
    private EditText packageName;
    private EditText details;
    private EditText tourDuration;
    private EditText loaction;
    private EditText bestTime;
    private EditText tourAvailable;
    private EditText nextTour;
    private EditText tourPrice;
    private EditText contact;

    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;

    private static final int PICK_IMAGE_REQUEST = 234;

    private StorageReference mStorage;

    private Uri uri;

    private SpotsDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_packages);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // get the reference of Toolbar
        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TravellingPackage.class);
                startActivity(intent);
                finish();
            }
        });
        progressDialog = new SpotsDialog(this, R.style.Custom_Progress_Bar);
        progressDialog.setMessage("please wait..");

        imageView = (ImageView) findViewById(R.id.image);
        packageName = (EditText) findViewById(R.id.packageName);
        details = (EditText) findViewById(R.id.details);
        tourDuration = (EditText) findViewById(R.id.tourDuration);
        tourAvailable = (EditText) findViewById(R.id.tourAvailable);
        loaction = (EditText) findViewById(R.id.loaction);
        bestTime = (EditText) findViewById(R.id.bestTime);
        nextTour = (EditText) findViewById(R.id.nextTour);
        tourPrice = (EditText) findViewById(R.id.tourPrice);
        contact = (EditText) findViewById(R.id.contact);

        auth = FirebaseAuth.getInstance();
        mCurrentUser = auth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("TourPackage");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mStorage = FirebaseStorage.getInstance().getReference();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(AddPackages.this, MainActivity.class));
                    finish();
                }
            }
        };
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryImageTake();
            }
        });
    }
    private void GalleryImageTake() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void saveInformationwithImge() {
        final String name = packageName.getText().toString().trim();
        final String fromdetail = details.getText().toString().trim();
        final String duration = tourDuration.getText().toString().trim();
        final String location = loaction.getText().toString().trim();
        final String besttime = bestTime.getText().toString().trim();
        final String available = tourAvailable.getText().toString().trim();
        final String nexttour = nextTour.getText().toString().trim();
        final String price = tourPrice.getText().toString().trim();
        final String contactof = contact.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(fromdetail) && !TextUtils.isEmpty(duration)
                && !TextUtils.isEmpty(location) && !TextUtils.isEmpty(besttime) && !TextUtils.isEmpty(available)
                && !TextUtils.isEmpty(nexttour)   && !TextUtils.isEmpty(price) && !TextUtils.isEmpty(contactof)  && uri != null) {
            progressDialog.show();

            StorageReference filePath = mStorage.child("post_image").child(uri.getLastPathSegment());
            filePath.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final Uri downloadUri = taskSnapshot.getDownloadUrl();
                            final DatabaseReference newPost = mDatabase.push();

                            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    newPost.child("name").setValue(name);
                                    newPost.child("details").setValue(fromdetail);
                                    newPost.child("duration").setValue(duration);
                                    newPost.child("userId").setValue(mCurrentUser.getUid());
                                    newPost.child("location").setValue(location);
                                    newPost.child("besttime").setValue(besttime);
                                    newPost.child("available").setValue(available);
                                    newPost.child("contact").setValue(contactof);
                                    newPost.child("nexttour").setValue(nexttour);
                                    newPost.child("price").setValue(price);
                                    newPost.child("post_image").setValue(downloadUri.toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Post Uploaded ", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(AddPackages.this, TravellingPackage.class));
                                                        finish();
                                                    } else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Post Uploading error ", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });

        }
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter package name", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(fromdetail)) {
            Toast.makeText(this, "Please enter  package details", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(duration)) {
            Toast.makeText(this, "Please enter duration", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(location)) {
            Toast.makeText(this, "Please enter location", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(besttime)) {
            Toast.makeText(this, "Please enter best time", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(available)) {
            Toast.makeText(this, "Please enter availability", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(nexttour)) {
            Toast.makeText(this, "Please enter next tour", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Please enter price", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(contactof)) {
            Toast.makeText(this, "Please enter contact number", Toast.LENGTH_LONG).show();
            return;
        }
        if (uri==null) {
            Toast.makeText(this, "Please enter image", Toast.LENGTH_LONG).show();
            return;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            try {
                uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), TravellingPackage.class);
        startActivity(intent);
        finish();
    }

    public void submit(View view) {
        saveInformationwithImge();
    }
}
