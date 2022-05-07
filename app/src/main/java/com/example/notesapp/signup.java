package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signup extends AppCompatActivity {

    private EditText msignupemail;
    private EditText msignuppassword;
    private Button msignup;
    private Button mgotologin;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide(); // nem kell nekünk actionbar
        msignupemail = findViewById(R.id.signupemail);
        msignuppassword = findViewById(R.id.signuppassword);
        msignup = findViewById(R.id.signup);
        mgotologin = findViewById(R.id.gotologin);

        firebaseAuth = FirebaseAuth.getInstance();

        mgotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signup.this,MainActivity.class);
                startActivity(intent);
            }
        });

        msignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = msignupemail.getText().toString().trim();
                String password = msignuppassword.getText().toString().trim();

                if(mail.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"All fields are required",Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<8){
                    Toast.makeText(getApplicationContext(),"Password should be greater than 7 character",Toast.LENGTH_SHORT).show();
                }
                else{
                    // register the user to firebase

                    firebaseAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // ha sikerült elérni a firestore-t
                          if(task.isSuccessful()){
                              Toast.makeText(getApplicationContext(),"Registration succesful",Toast.LENGTH_SHORT).show();
                              //TODO: ha sikerült akkor email-t is küldünk róla
                              sendEmailVerification();
                          }
                          else {
                              Toast.makeText(getApplicationContext(),"Failed to register",Toast.LENGTH_SHORT).show();
                          }
                        }
                    });
                }
            }
        });
    }



    //send email verification
    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        // tehát már van ilyen felhasználó
        if(firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(),"Verification email is sent, Verify and log in again",Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish(); // finish-eljük az activityt
                    //elkezdünk 1 másikat
                    startActivity(new Intent(signup.this,MainActivity.class));
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(),"Failed to send verification email",Toast.LENGTH_SHORT).show();
        }
    }
}