package com.packages.touristguide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.packages.touristguide.Home;
import com.packages.touristguide.R;
import com.packages.touristguide.model.Message;
import com.packages.touristguide.model.Users;
import com.packages.touristguide.view.MessageAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Conversation extends AppCompatActivity
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

    private DatabaseReference mUser;
    private FirebaseUser mCurrentUser;
    private TextView textViewName;
    private TextView address;
    private de.hdodenhof.circleimageview.CircleImageView send_image;

    /**
     * For chatting
     */
    //RecyclerView
    RecyclerView mMessageView;
    List<Message> mMessageList = new ArrayList<>();
    public LinearLayoutManager mLinearLayout;
    public MessageAdapter mAdapter;
    //Widget
    private ImageView sendMessageButton;
    private EditText messageTextBox;
    public ArrayList<String> adminIdList = new ArrayList<>();
    public DatabaseReference mDatabase;
    public String adminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setSupportActionBar(toolbar);
        navigation();
        send_image = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.send_image);
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

        address = (TextView) findViewById(R.id.address);
        textViewName = (TextView) findViewById(R.id.textViewName);
        mCurrentUser = mAuth.getCurrentUser();
        mUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nameof = (String) dataSnapshot.child("name").getValue();
                final String pro_pic = (String) dataSnapshot.child("image").getValue();
                String addressof = (String) dataSnapshot.child("address").getValue();

                textViewName.setText(nameof);
                address.setText(addressof);
                Picasso.with(Conversation.this).load(pro_pic).networkPolicy(NetworkPolicy.OFFLINE).into(send_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(Conversation.this).load(pro_pic).into(send_image);

                    }
                });
                Picasso.with(Conversation.this).load(pro_pic).networkPolicy(NetworkPolicy.OFFLINE).into(profilepic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(Conversation.this).load(pro_pic).into(profilepic);

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
                    startActivity(new Intent(Conversation.this, MainActivity.class));
                    finish();
                }
            }
        };

        //Message Widget initialization
        sendMessageButton = (ImageView) findViewById(R.id.message_send_button);

        messageTextBox = (EditText) findViewById(R.id.send_text_message);

        //RecyclerView
        mAdapter = new MessageAdapter(this,mMessageList);
        mMessageView = (RecyclerView) findViewById(R.id.user_message_list);
        mLinearLayout = new LinearLayoutManager(this);
        mMessageView.setHasFixedSize(true);
        mMessageView.setLayoutManager(mLinearLayout);
        loadMessage();
        mMessageView.setAdapter(mAdapter);

        /**
         * Start Chatting
         */
        DatabaseReference findAdmin = FirebaseDatabase.getInstance().getReference().child("Users");
        findAdmin.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Users users = dataSnapshot.getValue(Users.class);

                if(users.getRole().equals("Admin")) {
                    final String adminUid = users.getUserID();
                    //  Toast.makeText(getApplicationContext(),adminUid,Toast.LENGTH_SHORT).show();

                    //Firebase Auth init
                    mAuth = FirebaseAuth.getInstance();
                    final String currentUserId = mAuth.getCurrentUser().getUid();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("Chat").child(currentUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map chatAddMap = new HashMap();
                            chatAddMap.put("seen",false);
                            chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                            Map chatUserMap = new HashMap();
                            chatUserMap.put("Chat/"+currentUserId+"/"+adminUid,chatAddMap);
                            chatUserMap.put("Chat/"+adminUid+"/"+currentUserId,chatAddMap);

                            mDatabase.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if(databaseError != null){
                                        Log.d("TAG","Message sending failed for, database failure.");
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Onclick for send button
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    /**
     * For Sending Message
     * from user
     */
    public void sendMessage(){
        DatabaseReference findAdmin = FirebaseDatabase.getInstance().getReference().child("Users");

        findAdmin.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Users users = dataSnapshot.getValue(Users.class);


                if(users.getRole().equals("Admin")) {
                    String adminUid = users.getUserID();
                  //  Toast.makeText(getApplicationContext(),adminUid,Toast.LENGTH_SHORT).show();

                    //Firebase Auth init
                    mAuth = FirebaseAuth.getInstance();
                    String currentUserId = mAuth.getCurrentUser().getUid();

                 //   Toast.makeText(getApplicationContext(), currentUserId, Toast.LENGTH_SHORT).show();

                    /**
                     * Sending Message
                     */
                    String message = messageTextBox.getText().toString();

                    if(!TextUtils.isEmpty(message)){

                        String current_user_ref = "Message/"+currentUserId+"/"+adminUid;
                        String doctor_ref = "Message/"+adminUid+"/"+currentUserId;

                     //   Toast.makeText(getApplicationContext(),doctor_ref,Toast.LENGTH_SHORT).show();

                        DatabaseReference user_message_push = mDatabase.child("Message")
                                .child(currentUserId).child(adminUid).push();

                        String push_id = user_message_push.getKey();

                        Map messageMap = new HashMap();
                        messageMap.put("msg",message);
                        messageMap.put("seen",false);
                        messageMap.put("type","text");
                        messageMap.put("time",ServerValue.TIMESTAMP);
                        messageMap.put("from",currentUserId);
                        messageMap.put("to",adminUid);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
                        messageUserMap.put(doctor_ref+"/"+push_id,messageMap);

                        messageTextBox.setText("");

                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError != null){
                                    Log.d("Conversion","Message sending failed for, database failure.");
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * For Loading Message
     */
    public void loadMessage(){
        DatabaseReference findAdmin = FirebaseDatabase.getInstance().getReference().child("Users");
        findAdmin.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Users users = dataSnapshot.getValue(Users.class);
                if(users.getRole().equals("Admin")){
                    String adminUid = users.getUserID();
                    //Toast.makeText(getApplicationContext(),adminUid,Toast.LENGTH_SHORT).show();

                    //Firebase Auth init
                    mAuth = FirebaseAuth.getInstance();
                    String currentUserId = mAuth.getCurrentUser().getUid();

                   // Toast.makeText(getApplicationContext(),currentUserId,Toast.LENGTH_SHORT).show();
                    DatabaseReference retriveMessae = FirebaseDatabase.getInstance().getReference().child("Message")
                            .child(currentUserId).child(adminUid);
                    Query msgQuery = retriveMessae;

                    msgQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Message message = dataSnapshot.getValue(Message.class);
                            mMessageList.add(message);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void navigation(){
        edit_profile =(ImageView) findViewById(R.id.edit_profile);
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Conversation.this, EditProfile.class);
                startActivity(intent);
            }
        });
        profilepic = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.profilepic);
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Conversation.this, "Profile Picture", Toast.LENGTH_SHORT).show();
            }
        });
        home = (LinearLayout)findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(Conversation.this, Home.class);
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
                    Intent intent = new Intent(Conversation.this, TopPlace.class);
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
                    Intent intent = new Intent(Conversation.this, TravellingPackage.class);
                    startActivity(intent);
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
                    Intent intent = new Intent(Conversation.this, Forum.class);
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
        // progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
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
