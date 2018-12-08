package com.packages.touristguide.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.packages.touristguide.Home;
import com.packages.touristguide.R;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    private ImageButton buttonSignin;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private FirebaseAuth firebaseAuth;

    private SpotsDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        progressDialog = new SpotsDialog(this, R.style.Custom_Progress_Bar);
        progressDialog.setMessage("please wait..");

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), Home.class));
        }
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonSignin = (ImageButton) findViewById(R.id.buttonSignin);
        buttonSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });
    }
    private void userLogin() {
        // login with email and password
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Your email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Input password");
            return;
        }
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            progressDialog.dismiss();
                        } else {
                            progressDialog.dismiss();
                            editTextEmail.setError("Invalid email");
                            editTextPassword.setError("Invalid password");
                        }

                    }
                });

    }

    public void password(View view) {
        Intent intent = new Intent(MainActivity.this, ForgetPassword.class);
        startActivity(intent);
    }

    public void signup(View view) {
        Intent intent = new Intent(MainActivity.this, Registration.class);
        startActivity(intent);
    }
}
