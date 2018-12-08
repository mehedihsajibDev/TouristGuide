package com.packages.touristguide.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.packages.touristguide.Home;
import com.packages.touristguide.R;
import com.packages.touristguide.model.Packages;
import com.packages.touristguide.model.Users;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import dmax.dialog.SpotsDialog;

public class TravellingPackage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ImageView edit_profile;
    private de.hdodenhof.circleimageview.CircleImageView profilepic;
    private EditText editTextSearch;
    private ImageView searchIcom;
    private LinearLayout home;
    private LinearLayout topPlace;
    private LinearLayout travell;
    private LinearLayout forum;
    private LinearLayout conversation;
    private LinearLayout logOut;
    private TextView terms;
    private TextView rateUs;
    private TextView about;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;

    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private SpotsDialog progressDialog;
    private ProgressBar progressBar;

    private DatabaseReference mUser;
    private FirebaseUser mCurrentUser;
    private TextView textViewName;
    private TextView address;
    boolean role = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travelling_package);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setSupportActionBar(toolbar);

        progressDialog = new SpotsDialog(this, R.style.Custom_Progress_Bar);
        progressDialog.setMessage("please wait..");
        progressDialog.show();

        navigation();
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        searchIcom = (ImageView) findViewById(R.id.searchIcom);
        searchIcom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextSearch.setEnabled(true);
            }
        });
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ImageView menuRight = (ImageView) findViewById(R.id.menuRight);
        menuRight.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        } else {
                            drawer.openDrawer(GravityCompat.START);
                        }
                    }
                });


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("TourPackage");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUser.keepSynced(true);
        mDatabase.keepSynced(true);

        address = (TextView) findViewById(R.id.address);
        textViewName = (TextView) findViewById(R.id.textViewName);
        mCurrentUser = mAuth.getCurrentUser();
        mUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                if(users.getRole().equals("Admin")){
                    role = true;
                }
                String nameof = (String) dataSnapshot.child("name").getValue();
                final String pro_pic = (String) dataSnapshot.child("image").getValue();
                String addressof = (String) dataSnapshot.child("address").getValue();

                textViewName.setText(nameof);
                address.setText(addressof);
                Picasso.with(TravellingPackage.this).load(pro_pic).networkPolicy(NetworkPolicy.OFFLINE).into(profilepic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(TravellingPackage.this).load(pro_pic).into(profilepic);

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (role == true){

        }
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), AddPackages.class);
//                startActivity(intent);
//            }
//        });

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(TravellingPackage.this, MainActivity.class));
                    finish();
                }
            }
        };

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        mBlogList = (RecyclerView) findViewById(R.id.blog_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(layoutManager);
    }

    public void navigation(){
        edit_profile =(ImageView) findViewById(R.id.edit_profile);
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TravellingPackage.this, EditProfile.class);
                startActivity(intent);
            }
        });
        profilepic = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.profilepic);
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TravellingPackage.this, "Profile Picture", Toast.LENGTH_SHORT).show();
            }
        });
        home = (LinearLayout)findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(TravellingPackage.this, Home.class);
                    startActivity(intent);
                }
            }
        });
        topPlace = (LinearLayout)findViewById(R.id.topPlace);
        topPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(TravellingPackage.this, TopPlace.class);
                    startActivity(intent);
                } else {

                }
            }
        });
        travell = (LinearLayout)findViewById(R.id.travell);
        travell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {

                }
            }
        });
        forum = (LinearLayout)findViewById(R.id.forum);
        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(TravellingPackage.this, Forum.class);
                    startActivity(intent);
                } else {

                }
            }
        });
//        conversation = (LinearLayout)findViewById(R.id.conversation);
//        conversation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                if (drawer.isDrawerOpen(GravityCompat.START)) {
//                    drawer.closeDrawer(GravityCompat.START);
//                    Intent intent = new Intent(TravellingPackage.this, Conversation.class);
//                    startActivity(intent);
//                } else {
//
//                }
//            }
//        });
        logOut = (LinearLayout)findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                    mAuth.signOut();
                } else {

                }
            }
        });
        terms = (TextView)findViewById(R.id.terms);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {

                }
            }
        });
        rateUs = (TextView)findViewById(R.id.rateUs);
        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {

                }
            }
        });
        about = (TextView)findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {

                }
            }
        });
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
        FirebaseRecyclerAdapter<Packages, TravellingPackage.ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Packages,
                TravellingPackage.ViewHolder>(Packages.class, R.layout.travel_packagelist, TravellingPackage.ViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(TravellingPackage.ViewHolder viewHolder, final Packages model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setName(model.getName());
                viewHolder.setDetails(model.getDetails());
                viewHolder.setDuration(model.getDuration());
                viewHolder.setPrice(model.getPrice() + " TK");
                viewHolder.setLocation(model.getLocation());
                viewHolder.setPost_image(getApplicationContext(),model.getPost_image());
                progressDialog.dismiss();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), ShowTravelPackage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("post_key", post_key);
                        startActivity(intent);
                    }
                });
            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public  static class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name){
            TextView nameof = (TextView) mView.findViewById(R.id.packageName);
            nameof.setText(name);
        }

        public void setDetails(String details){
            TextView detailsof = (TextView) mView.findViewById(R.id.tourDetails);
            detailsof.setText(details);
        }
        public void setDuration(String duration){
            TextView dateof = (TextView) mView.findViewById(R.id.tourDuration);
            dateof.setText(duration);
        }
        public void setLocation(String location){
            TextView dateof = (TextView) mView.findViewById(R.id.location);
            dateof.setText(location);
        }
        public void setPrice(String price){
            TextView dateof = (TextView) mView.findViewById(R.id.tourPrice);
            dateof.setText(price);
        }

        public void setPost_image(final Context context, final String post_image){
            final ImageView postimage = (ImageView) mView.findViewById(R.id.place_img);

            Picasso.with(context).load(post_image).networkPolicy(NetworkPolicy.OFFLINE).into(postimage, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError() {
                    Picasso.with(context).load(post_image).into(postimage);
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
