package com.packages.touristguide.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.packages.touristguide.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import dmax.dialog.SpotsDialog;

public class ShowTopPlaceDetails extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth mAuth;
    private ImageView Post_image;
    private TextView details, title, address;
    private double geoLat, geoLong;
    private String post_key;
    private SpotsDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_top_place_details);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // get the reference of Toolbar
        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressDialog = new SpotsDialog(this, R.style.Custom_Progress_Bar);
        progressDialog.setMessage("please wait..");
        progressDialog.show();

        Post_image = (ImageView) findViewById(R.id.Post_image);
        details = (TextView) findViewById(R.id.details);
        title = (TextView) findViewById(R.id.title);
        address = (TextView) findViewById(R.id.address);

        post_key = getIntent().getExtras().getString("post_key");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Top_place");
        mDatabase.keepSynced(true);

        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String post_imageof = (String) dataSnapshot.child("post_image").getValue();
                String detailof = (String) dataSnapshot.child("details").getValue();
                String titleOf = (String) dataSnapshot.child("title").getValue();
                String addressof = (String) dataSnapshot.child("address").getValue();
                geoLat = (double) dataSnapshot.child("geolat").getValue();
                geoLong = (double) dataSnapshot.child("geolong").getValue();

                Toast.makeText(ShowTopPlaceDetails.this, geoLat+" "+geoLong, Toast.LENGTH_SHORT).show();

                details.setText(detailof);
                title.setText(titleOf);
                address.setText(addressof);
                Picasso.with(ShowTopPlaceDetails.this).load(post_imageof).networkPolicy(NetworkPolicy.OFFLINE).into(Post_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(ShowTopPlaceDetails.this).load(post_imageof).into(Post_image);

                    }
                });

                progressDialog.dismiss();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(ShowTopPlaceDetails.this, MainActivity.class));
                    finish();
                }
            }
        };
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
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
        mAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }
    @Override
    public void onBackPressed() {
        finish();

    }
}
