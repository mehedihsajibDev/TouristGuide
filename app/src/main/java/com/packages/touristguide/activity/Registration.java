package com.packages.touristguide.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.packages.touristguide.Home;
import com.packages.touristguide.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class Registration extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextPhonenumber;
    private EditText editTextDetailAddress;
    private EditText editTextCountry;
    private EditText editTextCity;
    private de.hdodenhof.circleimageview.CircleImageView pro_picture;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseRole;

    private StorageReference mStorage;

    private Uri mImageUri;
    String downloadUri;

    private ImageButton buttonSignup;
    private static final int PICK_IMAGE_REQUEST = 234;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText)findViewById(R.id.editTextConfirmPassword);
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

        buttonSignup = (ImageButton) findViewById(R.id.buttonSignup);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseRole = FirebaseDatabase.getInstance().getReference().child("Role");
        mStorage = FirebaseStorage.getInstance().getReference().child("Profilepicture");

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), Home.class));
        }

        progressDialog = new ProgressDialog(this);
    }

    private void registerUser(){

        //getting email and password from edit texts
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();
        String confirmpass = editTextConfirmPassword.getText().toString().trim();
        final String phonenumber = editTextPhonenumber.getText().toString().trim();
        final String addresses = editTextDetailAddress.getText().toString().trim();
        final String countries = editTextCountry.getText().toString().trim();
        final String cities = editTextCity.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please enter name",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(confirmpass)){
            Toast.makeText(this,"Please confirm password",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(phonenumber)){
            Toast.makeText(this,"Please enter phone number",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(addresses)){
            Toast.makeText(this,"Please enter address",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(countries)){
            Toast.makeText(this,"Please enter country name",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(cities)){
            Toast.makeText(this,"Please enter city name",Toast.LENGTH_LONG).show();
            return;
        }
        if (mImageUri == null){
            Toast.makeText(this,"Please upload your photo",Toast.LENGTH_LONG).show();
        }
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        if(password.matches(confirmpass) && mImageUri !=null){
            firebaseAuth.createUserWithEmailAndPassword(email, confirmpass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                StorageReference filepath = mStorage.child(mImageUri.getLastPathSegment());
                                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downloadUri = taskSnapshot.getDownloadUrl().toString();
                                        String user_id = firebaseAuth.getCurrentUser().getUid();
                                        DatabaseReference current_user_db = mDatabase.child(user_id);

                                        DatabaseReference role_user_db = mDatabaseRole.child(user_id);
                                        role_user_db.child("role").setValue("User");
                                        role_user_db.child("name").setValue(name);
                                        role_user_db.child("userID").setValue(user_id);

                                        current_user_db.child("name").setValue(name);
                                        current_user_db.child("role").setValue("User");
                                        current_user_db.child("email").setValue(email);
                                        current_user_db.child("userID").setValue(user_id);
                                        current_user_db.child("phone").setValue(phonenumber);
                                        current_user_db.child("address").setValue(addresses);
                                        current_user_db.child("country").setValue(countries);
                                        current_user_db.child("city").setValue(cities);
                                        current_user_db.child("image").setValue(downloadUri);

                                        finish();
                                        Intent intent = new Intent(getApplicationContext(), Home.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(Registration.this,"Registration Error",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "Password not match", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                pro_picture.setImageURI(mImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    public void signin(View view) {
        Intent intent = new Intent(Registration.this, MainActivity.class);
        startActivity(intent);
    }
}
