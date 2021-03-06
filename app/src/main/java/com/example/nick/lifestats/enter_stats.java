package com.example.nick.lifestats;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Core function of the application; User data is entered here to be processed and graphically represented
 */

public class enter_stats extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Connects to firebase root node
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference lifestats_db = database.getReference("user");

    @Override
    /**
     * This initiates the page itself
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //---------------------------------------------------------------------------
        //                      INITIALIZES ITEMS
        //---------------------------------------------------------------------------
        final Spinner data_types = (Spinner) findViewById(R.id.data_type_dropdown);
        data_types.setVisibility(View.INVISIBLE);

        final ArrayAdapter<CharSequence> adapter_financial = ArrayAdapter.createFromResource(this, R.array.data_type_array_money, android.R.layout.simple_spinner_item);
            adapter_financial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<CharSequence> adapter_time = ArrayAdapter.createFromResource(this, R.array.data_type_array_time, android.R.layout.simple_spinner_item);
            adapter_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<CharSequence> adapter_default = ArrayAdapter.createFromResource(this, R.array.data_type_array_default, android.R.layout.simple_spinner_item);
        adapter_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final EditText input = (EditText) findViewById(R.id.data_et);//field where values will be entered from
        input.setVisibility(View.GONE);

        final TextView tv = (TextView) findViewById(R.id.tv);//values will be printed
        tv.setVisibility(View.GONE);

        final TextView symbol = (TextView) findViewById(R.id.input_symbol_tv);//$ or Time
        symbol.setText("");

        final TextView minutes_tv = (TextView) findViewById(R.id.minutes_tv);//Textview for Minutes
        minutes_tv.setVisibility(View.GONE);

        final EditText minutes_et = (EditText) findViewById(R.id.minute_et);//Textview for Hours
        //---------------------------------------------------------------------------

        //Goes back to dashboard
        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(enter_stats.this, Dashboard.class);
                startActivity(intent);
            }
        });

        final Button submit = (Button) findViewById(R.id.submit_test); //submit button for inputs to db
        submit.setVisibility(View.INVISIBLE);

        //Button to enable financial options
        ImageView financial_btn = (ImageView) findViewById(R.id.financial_button);
        financial_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //subcategoryBit[0]++;
                data_types.setAdapter(adapter_financial);
                data_types.setVisibility(View.VISIBLE);
                input.setVisibility(View.VISIBLE);
                tv.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
                minutes_tv.setVisibility(View.GONE);
                minutes_et.setText("");
                minutes_et.setVisibility(View.GONE);
                symbol.setText("$");

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String type = data_types.getSelectedItem().toString();
                        String data = input.getText().toString();

                        //Used for getting data and time for database entry
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = date_time.format(c.getTime());

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String id = user.getUid();

                        if(data.equals("")){
                            Toast toast = Toast.makeText(getApplicationContext(), "Some Fields Were Left Blank!", Toast.LENGTH_SHORT);
                            toast.show();
                        }//end if
                        else {
                            lifestats_db.child(id).child("Data").child("Financial").child(type).child(formattedDate).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Data Successfully Entered", Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                            });
                        }//end else
                    }
                });
            }
        });

        //button to enable Time data entry
        ImageView time_button = (ImageView) findViewById(R.id.time_button);
        time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data_types.setAdapter(adapter_time);
                data_types.setVisibility(View.VISIBLE);
                input.setVisibility(View.VISIBLE);
                tv.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
                symbol.setText("Hours:");

                minutes_tv.setText("Minutes:");
                minutes_tv.setVisibility(View.VISIBLE);
                minutes_et.setVisibility(View.VISIBLE);

                input.requestFocus();//sets it as the first one to input into

                submit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        int zero = 0;

                        //Stores hours and minutes in a 4-digit number - first 2 = hours, next 2 = minutes
                        String minutes = minutes_et.getText().toString();
                            if(minutes.length() == 0){minutes = "0";}
                        String hours = input.getText().toString();
                            if(hours.length() == 0){hours = "0";}

                        int hour = Integer.parseInt(hours);
                        int min = Integer.parseInt(minutes);

                        int total_in_minutes = (hour * 60) + min;
                        String total = String.valueOf(total_in_minutes);

                        String type = data_types.getSelectedItem().toString();
                        String total_time = hours + minutes;

                        //Used for getting data and time for database entry
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = date_time.format(c.getTime());

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String id = user.getUid();

                        if(total.equals("0")) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Some Fields Were Left Blank!", Toast.LENGTH_SHORT);
                            toast.show();
                        }//end if
                        else {
                            lifestats_db.child(id).child("Data").child("Time").child(type).child(formattedDate).setValue(total).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Data Successfully Entered", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }//end else
                    }
                });
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
        getMenuInflater().inflate(R.menu.enter_stats, menu);
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
    /**
     * Handles Menu items
     */
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile_info) {
            Intent newIntent = new Intent(enter_stats.this,view_profile.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
        } else if (id == R.id.profile_edit) {
            Intent newIntent = new Intent(enter_stats.this,edit_profile.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
        } else if (id == R.id.user_settings) {
            Intent newIntent = new Intent(enter_stats.this,user_settings.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
        } else if (id == R.id.dashboard_page){
            Intent newIntent = new Intent(enter_stats.this,Dashboard.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
