package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//AppCompatActivity-t folyamatosan updatelik, tehát azért kell hogy folyamatosan up to date maradjon a program
public class MainActivity extends AppCompatActivity {

    private EditText mloginemail,mloginpassword;
    private Button mlogin, mgotosignup,mgotoforgotpassword;
    private FirebaseAuth firebaseAuth;
    private Button mlogin2, mgotosignup2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   // automatikusan generált add new empty activity-nél
        setContentView(R.layout.activity_main);  // automatikusan generált

        getSupportActionBar().hide();
        mloginemail = findViewById(R.id.loginemail);
        mloginpassword = findViewById(R.id.loginpassword);
        mlogin = findViewById(R.id.login);
        mgotoforgotpassword = findViewById(R.id.gotoforgotpassword);
        mgotosignup = findViewById(R.id.gotosignup);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        //animation
        //onclicklistener miatt kell a cast
        mlogin2 = (Button)findViewById(R.id.login);
        mgotosignup2 = (Button)findViewById(R.id.gotosignup);
        Animation animation1 = AnimationUtils.loadAnimation(this,R.anim.bounce);
        Animation animation2 = AnimationUtils.loadAnimation(this,R.anim.fadein);
        mlogin2.startAnimation(animation1);
        mgotosignup2.startAnimation(animation2);
        //

        if(firebaseUser != null){
            finish();
            startActivity(new Intent(MainActivity.this,notesActivity.class));
        }

        mgotosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,signup.class));
            }
        });
        mgotoforgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,forgotpassword.class));
            }
        });
        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = mloginemail.getText().toString().trim();
                String password = mloginpassword.getText().toString().trim();

                if(mail.isEmpty() ||password.isEmpty()){
                    //getApplicationContext lényegében a current activity, makeText csinál 1 toast objectet, .show() a display-hez kell
                    Toast.makeText(getApplicationContext(),"All fields are required",Toast.LENGTH_SHORT).show();
                }
                else{
                    //login the user
                    firebaseAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                checkmailverification();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Account doesn't exist",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private void checkmailverification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser.isEmailVerified()==true){
            Toast.makeText(getApplicationContext(),"Logged in",Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(MainActivity.this,notesActivity.class));
        }
        else{
            Toast.makeText(getApplicationContext(),"Verify your mail first",Toast.LENGTH_SHORT).show();
             firebaseAuth.signOut(); //elvileg a firebase automatikusan csinál accountot
        }
    }
}