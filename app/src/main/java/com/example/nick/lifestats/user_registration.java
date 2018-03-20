package com.example.nick.lifestats;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class user_registration extends AppCompatActivity {

    private FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference lifestats_db = database.getReference("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        auth = FirebaseAuth.getInstance();

        Button submit = (Button)findViewById(R.id.reg_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email_et = (EditText)findViewById(R.id.email_et);
                EditText password_et = (EditText)findViewById(R.id.pw_et);
                EditText confirm_pw_et = (EditText)findViewById(R.id.confirm_et);
                EditText age_et = (EditText)findViewById(R.id.user_age);
                EditText first_name = (EditText)findViewById(R.id.firstname);
                EditText last_name = (EditText)findViewById(R.id.lastname);

                String email = email_et.getText().toString().trim();
                String password = password_et.getText().toString().trim();
                String confirm = confirm_pw_et.getText().toString().trim();
                final String fname = first_name.getText().toString();
                final String lname = last_name.getText().toString();
                final String age = age_et.getText().toString();

                if((email.equals(""))||(password.equals(""))||(confirm.equals(""))||(fname.equals(""))||(lname.equals(""))||(age.equals(""))){
                    Toast.makeText(user_registration.this, "One Or More Field Is Blank",
                            Toast.LENGTH_SHORT).show();
                }

                else if(password.length()<6){
                    Toast.makeText(user_registration.this, "Password Must Be At Least 6 Characters Long",
                            Toast.LENGTH_SHORT).show();
                }

                else if(password.equals(confirm)){
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(user_registration.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(user_registration.this, "Registration Failed...Please Try Again",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(user_registration.this, "Welcome To LifeStats!", Toast.LENGTH_SHORT).show();

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        String id = user.getUid();
                                        lifestats_db.child(id).child("Profile").child("First Name").setValue(fname);
                                        lifestats_db.child(id).child("Profile").child("Last Name").setValue(lname);
                                        lifestats_db.child(id).child("Profile").child("Age").setValue(age);

                                        startActivity(new Intent(user_registration.this, user_login.class));
                                        finish();
                                    }
                                }
                            });
                }//end if
                else{
                    Toast.makeText(user_registration.this, "Passwords Do Not Match",
                            Toast.LENGTH_SHORT).show();
                }//end else
            }
        });

    }

}
