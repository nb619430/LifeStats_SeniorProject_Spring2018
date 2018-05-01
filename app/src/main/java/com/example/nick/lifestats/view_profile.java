package com.example.nick.lifestats;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Users can view the personal information associated with their account
 */

public class view_profile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference lifestats_db = database.getReference("user");
    private FirebaseAuth auth;

    /**
     * This initiates the page itself
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String id = user.getUid();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button back = (Button) findViewById(R.id.backprofile);
        final Button save = (Button) findViewById(R.id.save);
        Button edit = (Button) findViewById(R.id.edit);

        final EditText fname = (EditText) findViewById(R.id.first_et);
        final EditText lname = (EditText) findViewById(R.id.last_et);
        final EditText age = (EditText) findViewById(R.id.age_et);

        final TextView first_tv = (TextView) findViewById(R.id.firstname_tv);
        final TextView last_tv = (TextView) findViewById(R.id.lastname_tv);
        final TextView age_tv = (TextView) findViewById(R.id.age_tv);

        //Pulls directly from the users "Profile" node in the database
        DatabaseReference ref = lifestats_db.child(id).child("Profile");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String first_name = dataSnapshot.child("First Name").getValue().toString();
                String last_name = dataSnapshot.child("Last Name").getValue().toString();
                String user_age = dataSnapshot.child("Age").getValue().toString();

                first_tv.setText("First Name: " + first_name);
                last_tv.setText("Last Name: " + last_name);
                age_tv.setText("Age: " + user_age);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view_profile.this, Dashboard.class);
                startActivity(intent);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fname.setVisibility(View.VISIBLE);
                lname.setVisibility(View.VISIBLE);
                age.setVisibility(View.VISIBLE);

                first_tv.setVisibility(View.INVISIBLE);
                last_tv.setVisibility(View.INVISIBLE);
                age_tv.setVisibility(View.INVISIBLE);

                save.setVisibility(View.VISIBLE);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String first = fname.getText().toString();
                        String last = lname.getText().toString();
                        String user_age = age.getText().toString();

                        if(!(first.equals(""))){
                            lifestats_db.child(id).child("Profile").child("First Name").setValue(first).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Information Updated", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }
                        if(!(last.equals(""))){
                            lifestats_db.child(id).child("Profile").child("Last Name").setValue(last).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Information Updated", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }
                        if(!(user_age.equals(""))){
                            lifestats_db.child(id).child("Profile").child("Age").setValue(user_age).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Information Updated", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }
                        fname.setVisibility(View.INVISIBLE);
                        lname.setVisibility(View.INVISIBLE);
                        age.setVisibility(View.INVISIBLE);

                        first_tv.setVisibility(View.VISIBLE);
                        last_tv.setVisibility(View.VISIBLE);
                        age_tv.setVisibility(View.VISIBLE);

                        save.setVisibility(View.INVISIBLE);
                    }
                });//end save Onclick

            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile_info) {
            startActivity(new Intent(view_profile.this, view_profile.class));
            finish();
        } else if (id == R.id.profile_edit) {
            startActivity(new Intent(view_profile.this, edit_profile.class));
            finish();
        } else if (id == R.id.user_settings) {
            startActivity(new Intent(view_profile.this, user_settings.class));
            finish();
        } else if (id == R.id.dashboard_page){
            startActivity(new Intent(view_profile.this, Dashboard.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
