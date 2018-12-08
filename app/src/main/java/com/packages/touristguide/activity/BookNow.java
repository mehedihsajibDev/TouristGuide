package com.packages.touristguide.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.packages.touristguide.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import dmax.dialog.SpotsDialog;

public class BookNow extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private DatabaseReference mUser;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth mAuth;
    private String post_key;
    private SpotsDialog progressDialog;
    String contactof;
    String emailSender;
    String password;

    EditText editTextName, editTextItem, editTextQuantity, editTextMessage,editTextPhone,editTextEmail, editTextTime;
    Button buttonSend;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_now);

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

        post_key = getIntent().getExtras().getString("post_key");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("TourPackage");
        mUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mDatabase.keepSynced(true);

        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String post_imageof = (String) dataSnapshot.child("post_image").getValue();
                String detailof = (String) dataSnapshot.child("details").getValue();
                String titleOf = (String) dataSnapshot.child("name").getValue();
                String durationof = (String) dataSnapshot.child("duration").getValue();
                String locationof = (String) dataSnapshot.child("location").getValue();
                String besttimeof = (String) dataSnapshot.child("besttime").getValue();
                String availableof = (String) dataSnapshot.child("available").getValue();
                String nexttourof = (String) dataSnapshot.child("nexttour").getValue();
                String priceof = (String) dataSnapshot.child("price").getValue();
                contactof = (String) dataSnapshot.child("contact").getValue();

                editTextItem.setText(titleOf);


                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nameof = (String) dataSnapshot.child("name").getValue();
                final String pro_pic = (String) dataSnapshot.child("image").getValue();
                String addressof = (String) dataSnapshot.child("address").getValue();
                String phoneof = (String) dataSnapshot.child("phone").getValue();
                String cityof = (String) dataSnapshot.child("city").getValue();
                String countryof = (String) dataSnapshot.child("country").getValue();
                String emailof = (String) dataSnapshot.child("email").getValue();


                editTextName.setText(nameof);
                editTextPhone.setText(phoneof);
                editTextEmail.setText(emailof);

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
                    startActivity(new Intent(BookNow.this, MainActivity.class));
                    finish();
                }
            }
        };

        emailSender = "siprince3836@gmail.com";
        password = "prince3836";
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextItem = (EditText) findViewById(R.id.editTextItem);
        editTextQuantity = (EditText) findViewById(R.id.editTextQuantity);
        editTextTime = (EditText) findViewById(R.id.editTextTime);
        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        TextView textView = (TextView) findViewById(R.id.call);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactof)));
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(editTextName.getText().toString() )){
                    Toast.makeText(BookNow.this,"Please enter name",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(editTextItem.getText().toString() )){
                    Toast.makeText(BookNow.this,"Please enter package name",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(editTextQuantity.getText().toString() )){
                    Toast.makeText(BookNow.this,"Please enter package quantity",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(editTextTime.getText().toString() )){
                    Toast.makeText(BookNow.this,"Please enter tour time",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(editTextMessage.getText().toString() )){
                    Toast.makeText(BookNow.this,"Please enter your message",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(editTextPhone.getText().toString() )){
                    Toast.makeText(BookNow.this,"Please enter phone number",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(editTextEmail.getText().toString() )){
                    Toast.makeText(BookNow.this,"Please enter email",Toast.LENGTH_LONG).show();
                    return;
                }else {

                    openprogresdialog();
                }
            }
        });
    }
    private void openprogresdialog() {
        // TODO Auto-generated method stub
        final ProgressDialog progDailog = ProgressDialog.show(
                BookNow.this, "Requesting for Booking", "Please wait...we will call you soon.", true);

        new Thread() {
            public void run() {
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Properties props = new Properties();
                    props.put("mail.smtp.host", "smtp.gmail.com");
                    props.put("mail.smtp.socketFactory.port", "465");
                    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.port", "465");

                    try{
                        session = Session.getDefaultInstance(props, new Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(emailSender, password);
                            }
                        });

                        if(session != null){

                            Message message = new MimeMessage(session);
                            message.setFrom(new InternetAddress(emailSender));
                            message.setSubject("Booking from Application");
                            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("siprince3836@gmail.com"));
                            message.setContent("Client Name: " + editTextName.getText().toString() +"; "+ "\n\n"
                                    + "Package Name: " + editTextItem.getText().toString()+"; "+ "\n\n"
                                    + "Package Quantity: "+ editTextQuantity.getText().toString()+"; "+ "\n\n"
                                    + "Tour time: "+ editTextTime.getText().toString()+"; "+ "\n\n"
                                    + "Booking message: "+ editTextMessage.getText().toString()+"; "+ "\n\n"
                                    + "Phone number: " + editTextPhone.getText().toString()+"; "+ "\n\n"
                                    + "Email: " + editTextEmail.getText().toString()+"; ", "text/html; charset=utf-8");
                            ;
                            Transport.send(message);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                }
                progDailog.dismiss();
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
