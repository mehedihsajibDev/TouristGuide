package com.packages.touristguide.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class AddTopPlace extends AppCompatActivity {

    private ImageView imageView;
    private EditText title;
    private EditText details;
    private EditText address;
    private EditText geoLat;
    private EditText geoLong;

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
        setContentView(R.layout.activity_add_top_place);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // get the reference of Toolbar
        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TopPlace.class);
                startActivity(intent);
                finish();
            }
        });
        progressDialog = new SpotsDialog(this, R.style.Custom_Progress_Bar);
        progressDialog.setMessage("please wait..");

        imageView = (ImageView) findViewById(R.id.image);
        title = (EditText) findViewById(R.id.postTitle);
        details = (EditText) findViewById(R.id.details);
        address = (EditText) findViewById(R.id.address);
        geoLat = (EditText) findViewById(R.id.geoLat);
        geoLong = (EditText) findViewById(R.id.geoLong);

        auth = FirebaseAuth.getInstance();
        mCurrentUser = auth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Top_place");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mStorage = FirebaseStorage.getInstance().getReference();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(AddTopPlace.this, MainActivity.class));
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
        final String fromTitle = title.getText().toString().trim();
        final String fromdetails = details.getText().toString().trim();
        final String addressof = address.getText().toString().trim();
        final String  geolat = geoLat.getText().toString().trim();
        final String  geolong = geoLong.getText().toString().trim();

        if (!TextUtils.isEmpty(fromTitle) && !TextUtils.isEmpty(fromdetails) && !TextUtils.isEmpty(geolat)
                && !TextUtils.isEmpty(geolong) && uri != null) {
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
                                    newPost.child("title").setValue(fromTitle);
                                    newPost.child("details").setValue(fromdetails);
                                    newPost.child("address").setValue(addressof);
                                    newPost.child("userId").setValue(mCurrentUser.getUid());
                                    newPost.child("geolat").setValue(Double.parseDouble(geolat));
                                    newPost.child("geolong").setValue(Double.parseDouble(geolong));
                                    newPost.child("post_image").setValue(downloadUri.toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Post Uploaded ", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(AddTopPlace.this, TopPlace.class));
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
        if (TextUtils.isEmpty(fromTitle)) {
            Toast.makeText(this, "Please enter title", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(fromdetails)) {
            Toast.makeText(this, "Please enter details", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(addressof)) {
            Toast.makeText(this, "Please enter address", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(geolat)) {
            Toast.makeText(this, "Please enter geoLat", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(geolong)) {
            Toast.makeText(this, "Please enter geoLong", Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent(getApplicationContext(), TopPlace.class);
        startActivity(intent);
        finish();
    }

    public void submit(View view) {
        saveInformationwithImge();
    }
}
