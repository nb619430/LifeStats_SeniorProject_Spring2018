package com.example.nick.lifestats;

import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class view_stats extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Test Message to DB
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference lifestats_db = database.getReference("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Creates the Spinner and castes the arrays for the different data types into spinner dropdown items
        //---------------------------------------------------------------------------
        final Spinner data_types = (Spinner) findViewById(R.id.data_type_dropdown);

        final ArrayAdapter<CharSequence> adapter_financial = ArrayAdapter.createFromResource(this, R.array.data_type_array_money, android.R.layout.simple_spinner_item);
        adapter_financial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<CharSequence> adapter_time = ArrayAdapter.createFromResource(this, R.array.data_type_array_time, android.R.layout.simple_spinner_item);
        adapter_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<CharSequence> adapter_default = ArrayAdapter.createFromResource(this, R.array.data_type_array_default, android.R.layout.simple_spinner_item);
        adapter_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //final EditText input = (EditText) findViewById(R.id.data_et);//field where values will be entered from

        //group for radio buttons - Only one can be clicked
        RadioGroup radio_buttons = (RadioGroup)findViewById(R.id.radio_group);
        radio_buttons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.financial_radio) {

                    data_types.setAdapter(adapter_financial);
                    Button view = (Button) findViewById(R.id.view_test);
                    view.setOnClickListener(new View.OnClickListener() {

                        DatabaseReference ref = lifestats_db.child("Data").child("Financial");
                        @Override
                        public void onClick(View v) {
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String type = data_types.getSelectedItem().toString();
                                    String data = dataSnapshot.child(type).getValue().toString();
                                    TextView output = (TextView)findViewById(R.id.output_tv);

                                    String average = financial_average(user_data_financial(data)).toString();
                                    String total = financial_total(user_data_financial(data)).toString();
                                    String statistics = "Total Spent: $" + total + ".00\n" + "Average Spent: $" + average + ".00\n";
                                    output.setText(statistics);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }

                else if(checkedId == R.id.time_radio) {

                    data_types.setAdapter(adapter_time);
                    Button view = (Button) findViewById(R.id.view_test);
                    view.setOnClickListener(new View.OnClickListener() {

                        DatabaseReference ref = lifestats_db.child("Data").child("Time");
                        @Override
                        public void onClick(View v) {
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String type = data_types.getSelectedItem().toString();
                                    String data = dataSnapshot.child(type).getValue().toString();
                                    TextView output = (TextView)findViewById(R.id.output_tv);

                                    String total_hours = hours_total(user_data_time(data)).toString();
                                    String total_minutes = minutes_total(user_data_time(data)).toString();
                                    String average_hours = time_average_hours(user_data_time(data)).toString();
                                    String average_minutes = time_average_minutes(user_data_time(data)).toString();
                                    output.setText("Total Time: " + total_hours + " Hours, and " + total_minutes + " Minutes" + "\n" +
                                                    "Average Time: " + average_hours + " Hours, and " + average_minutes + " Minutes");
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private List<String> user_data_time(String output) {
        List<String> time = new ArrayList<String>();
        //values.add(output);
        for (int i = 0; i < output.length(); i++) {
            int start = 0;
            int end = 0;
            if (output.charAt(i) == '=') {
                start = i;
                for (int j = i; j < output.length(); j++) {
                    if (output.charAt(j) == ',' || output.charAt(j) == '}') {
                        end = j;
                        String temp = output.substring(start + 1, end);
                        time.add(temp);
                        break;
                    }
                }
            }
        }
        return time;
    }//end user_data

    private List<String> user_data_financial(String output) {
        List<String> values = new ArrayList<String>();
        //values.add(output);
        for (int i = 0; i < output.length(); i++) {
            int start = 0;
            int end = 0;
            if (output.charAt(i) == '=') {
                start = i;
                for (int j = i; j < output.length(); j++) {
                    if (output.charAt(j) == ',' || output.charAt(j) == '}') {
                        end = j;
                        String temp = output.substring(start + 1, end);
                        values.add(temp);
                        break;
                    }
                }
            }
        }
        return values;
    }//end user_data

    private Integer hours_total(List<String>values){
        StringBuilder sb = new StringBuilder();
        Integer hours = 0;
        Integer minutes = 0;
        //gets the total of all the values in the database
        for (String s : values) {
            hours = hours + Integer.parseInt(s.substring(0,2));
            minutes = minutes + Integer.parseInt(s.substring(2,4));
            if(minutes >= 60){ //rounds minutes up to hours
                int temp = minutes/60;
                minutes = minutes%60;
                hours = hours + temp;
            }//end if
        }
        return hours;
    }//end output

    private Integer minutes_total(List<String>values){
        StringBuilder sb = new StringBuilder();
        Integer minutes = 0;
        //gets the total of all the values in the database
        for (String s : values) {
            minutes = minutes + Integer.parseInt(s.substring(2,4));
            if(minutes >= 60){ //rounds minutes up to hours
                minutes = minutes%60;
            }//end if
        }
        return minutes;
    }//end output

    private Integer time_average_hours(List<String>values){
        StringBuilder sb = new StringBuilder();
        Integer hours = 0;
        Integer minutes = 0;
        int count = 0;
        //gets the total of all the values in the database
        for (String s : values) {
            count++;
            hours = hours + Integer.parseInt(s.substring(0,2));
            minutes = minutes + Integer.parseInt(s.substring(2,4));
        }
        minutes = minutes + (hours*60);
        int avg_minutes = minutes/count;
        if(avg_minutes >= 60){
            hours = avg_minutes/60;
        }

        return hours;
    }//end output

    private Integer time_average_minutes(List<String>values){
        StringBuilder sb = new StringBuilder();
        Integer hours = 0;
        Integer minutes = 0;
        int count = 0;
        //gets the total of all the values in the database
        for (String s : values) {
            count++;
            hours = hours + Integer.parseInt(s.substring(0,2));
            minutes = minutes + Integer.parseInt(s.substring(2,4));
        }
        minutes = minutes + (hours*60);
        int avg_minutes = minutes/count;
        if(avg_minutes >= 60){
            minutes = avg_minutes%60;
        }

        return minutes;
    }//end output

    private Integer financial_total(List<String>values){
        StringBuilder sb = new StringBuilder();
        Integer total = 0;
        //gets the total of all the values in the database
        for (String s : values)
        {
            total = total + Integer.parseInt(s);
            sb.append(s);
            sb.append("\n");
        }
        return total;
    }//end output

    private Integer financial_average(List<String>values){
        StringBuilder sb = new StringBuilder();
        Integer total = 0;
        Integer count = 0;
        //gets the total of all the values in the database
        for (String s : values)
        {
            total = total + Integer.parseInt(s);
            count++;
            sb.append(s);
            sb.append("\n");
        }
        return (total/count);
    }//end output

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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile_info) {
            startActivity(new Intent(view_stats.this, view_profile.class));
            finish();
        } else if (id == R.id.profile_edit) {
            startActivity(new Intent(view_stats.this, edit_profile.class));
            finish();
        } else if (id == R.id.user_settings) {
            startActivity(new Intent(view_stats.this, user_settings.class));
            finish();
        } else if (id == R.id.dashboard_page){
            startActivity(new Intent(view_stats.this, Dashboard.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}