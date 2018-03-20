package com.example.nick.lifestats;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class user_login extends AppCompatActivity {

    private FirebaseAuth database;

    @Override
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

        Button login = (Button)findViewById(R.id.login_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email_et = (EditText)findViewById(R.id.login_email);
                EditText password_et = (EditText)findViewById(R.id.login_pass);

                String email = email_et.getText().toString().trim();
                String password = password_et.getText().toString().trim();

                database.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(user_login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(user_login.this, "Login Failed...Please Try Again",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(user_login.this, "Welcome Back!",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(user_login.this, Dashboard.class));
                                    finish();
                                }
                            }
                        });
            }
        });


    }
}
