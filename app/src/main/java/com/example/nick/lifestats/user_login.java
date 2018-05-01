package com.example.nick.lifestats;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Users can enter their registered email and password combination to access their "LifeStats" (data/profile)
 */

public class user_login extends AppCompatActivity {

    private FirebaseAuth database;

    @Override
    /**
     * This initiates the page itself
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        database = FirebaseAuth.getInstance();

        TextView register = (TextView)findViewById(R.id.register_link);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(user_login.this, user_registration.class));
                finish();
            }
        });

        final ProgressBar loading = (ProgressBar) findViewById(R.id.progressBar);
        loading.setVisibility(View.INVISIBLE);

        final Button login = (Button)findViewById(R.id.login_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email_et = (EditText)findViewById(R.id.login_email);
                EditText password_et = (EditText)findViewById(R.id.login_pass);

                String email = email_et.getText().toString().trim();
                String password = password_et.getText().toString().trim();

                //Makes sure the Email input isnt empty
                if(email.equals("")){
                    Toast.makeText(user_login.this, "No Email Entered",
                            Toast.LENGTH_SHORT).show();
                }

                //Makes sure there are no empty inputs
                else if(password.equals("")){
                    Toast.makeText(user_login.this, "No Password Entered",
                            Toast.LENGTH_SHORT).show();
                }

                //Makes sure there are not empty inputs
                else if(email.equals("") && password.equals("")){
                    Toast.makeText(user_login.this, "No Email or Password Entered",
                            Toast.LENGTH_SHORT).show();
                }

                else {
                    login.setVisibility(View.INVISIBLE); //replaces login with progress circle
                    loading.setVisibility(View.VISIBLE);
                    database.signInWithEmailAndPassword(email, password) //Authentication with Firebase
                            .addOnCompleteListener(user_login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(user_login.this, "Login Failed...Please Try Again",
                                                Toast.LENGTH_SHORT).show();
                                        loading.setVisibility(View.INVISIBLE);
                                        login.setVisibility(View.VISIBLE);
                                    } else {
                                        startActivity(new Intent(user_login.this, Dashboard.class));
                                        finish();
                                    }
                                }
                            });
                }//end else
            }
        });


    }
}
