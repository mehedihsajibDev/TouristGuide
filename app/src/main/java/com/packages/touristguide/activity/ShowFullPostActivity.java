package com.packages.touristguide.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.packages.touristguide.R;
import com.packages.touristguide.model.Comment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import dmax.dialog.SpotsDialog;

public class ShowFullPostActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUser;
    private DatabaseReference mComments;
    private Query mCommentQuery;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth mAuth;
    private de.hdodenhof.circleimageview.CircleImageView userImage;
    private ImageView Post_image;
    private TextView date, details, title, address, name;
    private String post_key;
    private EditText mComment;
    private SpotsDialog progressDialog;
    private RecyclerView mBlogList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_full_post);
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

        userImage = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.pro_image);
        date = (TextView) findViewById(R.id.date);
        Post_image = (ImageView) findViewById(R.id.Post_image);
        details = (TextView) findViewById(R.id.details);
        title = (TextView) findViewById(R.id.title);
        address = (TextView) findViewById(R.id.address);
        name = (TextView) findViewById(R.id.name);
        mComment = (EditText) findViewById(R.id.editTextComment);

        post_key = getIntent().getExtras().getString("post_key");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mDatabaseUser.keepSynced(true);
        mUser.keepSynced(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Forum");
        mDatabase.keepSynced(true);

        mComments = FirebaseDatabase.getInstance().getReference().child("Comments");
        mCommentQuery = mComments.orderByChild("post_key").equalTo(post_key);
        mComments.keepSynced(true);

        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nameof = (String) dataSnapshot.child("name").getValue();
                final String pro_pic = (String) dataSnapshot.child("pro_pic").getValue();
                final String post_imageof = (String) dataSnapshot.child("post_image").getValue();
                String detailof = (String) dataSnapshot.child("details").getValue();
                String dateof = (String) dataSnapshot.child("date").getValue();
                String titleOf = (String) dataSnapshot.child("title").getValue();
                String addressof = (String) dataSnapshot.child("address").getValue();

                name.setText(nameof);
                details.setText(detailof);
                date.setText(dateof);
                title.setText(titleOf);
                address.setText(addressof);
                Picasso.with(ShowFullPostActivity.this).load(post_imageof).networkPolicy(NetworkPolicy.OFFLINE).into(Post_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(ShowFullPostActivity.this).load(post_imageof).into(Post_image);

                    }
                });
                Picasso.with(ShowFullPostActivity.this).load(pro_pic).networkPolicy(NetworkPolicy.OFFLINE).into(userImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(ShowFullPostActivity.this).load(pro_pic).into(userImage);

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
                    startActivity(new Intent(ShowFullPostActivity.this, MainActivity.class));
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

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
        FirebaseRecyclerAdapter<Comment, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment,
                ShowFullPostActivity.ViewHolder>(Comment.class, R.layout.comment_list, ShowFullPostActivity.ViewHolder.class, mCommentQuery) {
            @Override
            protected void populateViewHolder(ShowFullPostActivity.ViewHolder viewHolder, final Comment model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setName(model.getName());
                viewHolder.setComment(model.getComment());
                viewHolder.setDate(model.getDate());
                viewHolder.setAddress(model.getAddress());
                viewHolder.setPro_pic(getApplicationContext(),model.getPro_pic());
                progressDialog.dismiss();
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
            TextView nameof = (TextView) mView.findViewById(R.id.name);
            nameof.setText(name);
        }

        public void setAddress(String address){
            TextView rollif = (TextView) mView.findViewById(R.id.address);
            rollif.setText(address);
        }
        public void setComment(String comment){
            TextView detailsof = (TextView) mView.findViewById(R.id.textComment);
            detailsof.setText(comment);
        }
        public void setDate(String date){
            TextView dateof = (TextView) mView.findViewById(R.id.date);
            dateof.setText(date);
        }
        public void setPro_pic(final Context context, final String pro_pic){
            final ImageView propic = (ImageView) mView.findViewById(R.id.pro_image);

            Picasso.with(context).load(pro_pic).networkPolicy(NetworkPolicy.OFFLINE).into(propic, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError() {
                    Picasso.with(context).load(pro_pic).into(propic);
                }
            });
        }
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

    public void sendComment(View view) {
        saveInformation();
    }
    private void saveInformation() {
        final String commentof = mComment.getText().toString().trim();
        final String moments_date = DateFormat.getDateTimeInstance().format(new Date());

        if (!TextUtils.isEmpty(commentof)) {
            progressDialog.show();

            final DatabaseReference newPost = mComments.push();

            mUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    newPost.child("comment").setValue(commentof);
                    newPost.child("post_key").setValue(post_key);
                    newPost.child("date").setValue(moments_date);
                    newPost.child("name").setValue(dataSnapshot.child("name").getValue());
                    newPost.child("address").setValue(dataSnapshot.child("address").getValue());
                    newPost.child("pro_pic").setValue(dataSnapshot.child("image").getValue())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "commented ", Toast.LENGTH_LONG).show();
                                        mComment.setText("");
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Uploading error ", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }
        if (TextUtils.isEmpty(commentof)) {
            Toast.makeText(this, "Please enter comment", Toast.LENGTH_LONG).show();
            return;
        }

    }
}
