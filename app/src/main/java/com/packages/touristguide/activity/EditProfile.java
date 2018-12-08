package com.packages.touristguide.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.packages.touristguide.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class EditProfile extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextPhonenumber;
    private EditText editTextDetailAddress;
    private EditText editTextCountry;
    private EditText editTextCity;
    private de.hdodenhof.circleimageview.CircleImageView pro_picture;
    private String post_key;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference mDatabaseUser;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUser;
    private StorageReference mStorage;

    private Uri mImageUri;
    String downloadUri;

    private ImageButton buttonSignup;
    private static final int PICK_IMAGE_REQUEST = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

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

        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextPhonenumber = (EditText)findViewById(R.id.editTextPhonenumber);
        editTextDetailAddress = (EditText)findViewById(R.id.editTextDetailAddress);
        editTextCountry = (EditText)findViewById(R.id.editTextCountry);
        editTextCity = (EditText)findViewById(R.id.editTextCity);
        pro_picture = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.pro_picture);
        pro_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select AddOfferData"), PICK_IMAGE_REQUEST);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        post_key = mCurrentUser.getUid().toString();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mDatabaseUser.keepSynced(true);
        mUser.keepSynced(true);

        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nameof = (String) dataSnapshot.child("name").getValue();
                final String pro_pic = (String) dataSnapshot.child("image").getValue();
                String addressof = (String) dataSnapshot.child("address").getValue();
                String phoneof = (String) dataSnapshot.child("phone").getValue();
                String cityof = (String) dataSnapshot.child("city").getValue();
                String countryof = (String) dataSnapshot.child("country").getValue();


                editTextName.setText(nameof);
                editTextDetailAddress.setText(addressof);
                editTextPhonenumber.setText(phoneof);
                editTextCity.setText(cityof);
                editTextCountry.setText(countryof);
                Picasso.with(EditProfile.this).load(pro_pic).networkPolicy(NetworkPolicy.OFFLINE).into(pro_picture, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(EditProfile.this).load(pro_pic).into(pro_picture);

                    }
                });
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
                    startActivity(new Intent(EditProfile.this, MainActivity.class));
                    finish();
                }
            }
        };

        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onBackPressed() {
        finish();

    }

}
