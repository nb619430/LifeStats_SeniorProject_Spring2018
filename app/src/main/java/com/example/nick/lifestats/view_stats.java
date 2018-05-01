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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Core function of the application; Users can view statistics based on specific categories of their entered data
 */

public class view_stats extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Test Message to DB
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference lifestats_db = database.getReference("user");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String id = user.getUid();

    @Override
    /**
     * This initiates the page itself
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        data_types.setVisibility(View.INVISIBLE);

        final ArrayAdapter<CharSequence> adapter_financial = ArrayAdapter.createFromResource(this, R.array.data_type_array_money, android.R.layout.simple_spinner_item);
        adapter_financial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<CharSequence> adapter_time = ArrayAdapter.createFromResource(this, R.array.data_type_array_time, android.R.layout.simple_spinner_item);
        adapter_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Button dashboard = (Button) findViewById(R.id.back_dash);
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view_stats.this, Dashboard.class));
            }
        });

        final Button graph = (Button) findViewById(R.id.graph_btn);
        graph.setVisibility(View.INVISIBLE);

        final Button viewstats_finance = (Button) findViewById(R.id.view_test);
        viewstats_finance.setVisibility(View.INVISIBLE);

        final Button viewstats_time = (Button) findViewById(R.id.view_test);
        viewstats_time.setVisibility(View.INVISIBLE);

        ImageView financial_button =(ImageView) findViewById(R.id.financial_button);
        financial_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data_types.setAdapter(adapter_financial);
                data_types.setVisibility(View.VISIBLE);
                graph.setVisibility(View.VISIBLE);

                graph.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {//This button lets the graph know which category to display
                        Intent intent = new Intent(view_stats.this, graph.class);
                        intent.putExtra("category", "Financial");
                        intent.putExtra("subcategory", data_types.getSelectedItem().toString());
                        startActivity(intent);
                    }
                });

                viewstats_finance.setVisibility(View.VISIBLE);
                viewstats_finance.setOnClickListener(new View.OnClickListener() {
                    DatabaseReference ref = lifestats_db.child(id).child("Data").child("Financial");
                    @Override
                    public void onClick(View v) {
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String type = data_types.getSelectedItem().toString();
                                if(dataSnapshot.child(type).exists()) {
                                    String data = dataSnapshot.child(type).getValue().toString();
                                    TextView output = (TextView) findViewById(R.id.output_tv);
                                    if (data.equals("")) {
                                        output.setText("No Data Found!");
                                    } else {
                                        String average = financial_average(user_data_financial(data)).toString();
                                        String total = financial_total(user_data_financial(data)).toString();

                                        String max_total = max(user_data_financial(data)).toString();

                                        DecimalFormat df2 = new DecimalFormat(".##");
                                        Double variance = variance(user_data_financial(data), financial_average(user_data_financial(data)));
                                        Double stdev = Math.sqrt(variance);

                                        String statistics = "Total Spent: $" + total + ".00\n" +
                                                "Average Spent: $" + average + ".00\n" +
                                                "Largest Entry: $" + max_total + ".00\n" +
                                                "Standard Deviation: $" + df2.format(stdev);
                                        output.setText(statistics);
                                    }
                                }
                                else{
                                    TextView output = (TextView) findViewById(R.id.output_tv);
                                    output.setText("No data Found!");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        });

        ImageView time_button = (ImageView) findViewById(R.id.time_button);
        time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data_types.setAdapter(adapter_time);
                data_types.setVisibility(View.VISIBLE);
                viewstats_time.setVisibility(View.VISIBLE);
                graph.setVisibility(View.VISIBLE);

                graph.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {//This button lets the graph know which category to display
                        Intent intent = new Intent(view_stats.this, graph.class);
                        intent.putExtra("category", "Time");
                        intent.putExtra("subcategory", data_types.getSelectedItem().toString());
                        startActivity(intent);
                    }
                });

                viewstats_time.setOnClickListener(new View.OnClickListener() {
                    DatabaseReference ref = lifestats_db.child(id).child("Data").child("Time");
                    @Override
                    public void onClick(View v) {
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String type = data_types.getSelectedItem().toString();
                                if(dataSnapshot.child(type).exists()) {
                                    String data = dataSnapshot.child(type).getValue().toString();
                                    TextView output = (TextView) findViewById(R.id.output_tv);
                                    if (data.equals(null)) {
                                        output.setText("No data Found");
                                    } else {
                                        String total_hours = hours_total(user_data_time(data)).toString();
                                        String total_minutes = minutes_total(user_data_time(data)).toString();
                                        String average_hours = time_average_hours(user_data_time(data)).toString();
                                        String average_minutes = time_average_minutes(user_data_time(data)).toString();

                                        String max_total = max(user_data_time(data)).toString();
                                        int max_hours = Integer.parseInt(max_total) / 60;
                                        int max_mins = Integer.parseInt(max_total) % 60;

                                        DecimalFormat df2 = new DecimalFormat(".##");
                                        Double variance = variance(user_data_time(data), time_average_total_minutes(user_data_time(data)));
                                        Double stdev = Math.sqrt(variance);

                                        output.setText("Total Time: " + total_hours + " Hours, and " + total_minutes + " Minutes" + "\n" +
                                                "Average Time: " + average_hours + " Hours, and " + average_minutes + " Minutes" + "\n" +
                                                "Largest Entry: " + max_hours + " Hours, and " + max_mins + " Minutes" + "\n" +
                                                "Standard Deviation: " + df2.format(stdev) + " Minutes");
                                    }
                                }
                                else{
                                    TextView output = (TextView) findViewById(R.id.output_tv);
                                    output.setText("No Data Found!");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        });

    }

    /**
     * Takes all of the user-entered values and calculates the variances
     * @param values String List
     * @param average int
     * @return Double
     */
    private Double variance(List<String>values, int average){
        double avg = average;
        double temp = 0;
        int count = 0;
        for(String s : values) {
            count++;
            temp = temp + (Integer.parseInt(s) - avg) * (Integer.parseInt(s) - avg);
        }
        return (temp / (count - 1));
    }//end Variance

    /**
     * Calculates the largest single entry in the database
     * @param values String List
     * @return Integer
     */
    private Integer max(List<String>values){
        StringBuilder sb = new StringBuilder();
        Integer max = 0;
        //gets the total of all the values in the database
        for (String s : values) {
            if((Integer.parseInt(s)) > max){
                max = Integer.parseInt(s);
            }
        }
        return max;
    }//end Max

    /**
     * Parsed through the JSON for Time data and returns a string with all entries in one list
     * @param output String
     * @return String List
     */
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

    /**
     * Parsed through the JSON for Financial data and returns a string with all entries in one list
     * @param output String
     * @return String List
     */
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

    /**
     * Calculates the total number of hours for Financial Data
     * @param values String List
     * @return Integer
     */
    private Integer hours_total(List<String>values){
        StringBuilder sb = new StringBuilder();
        Integer total_minutes = 0;
        //gets the total of all the values in the database
        for (String s : values) {
            total_minutes = total_minutes + Integer.parseInt(s);
        }
        Integer hours = total_minutes / 60;
        Integer minutes = total_minutes % 60;

        return hours;
    }//end Hours_Total

    /**
     * Calculates the total number of minutes for Financial Data
     * @param values String List
     * @return Integer
     */
    private Integer minutes_total(List<String>values){
        StringBuilder sb = new StringBuilder();
        Integer total_minutes = 0;
        //gets the total of all the values in the database
        for (String s : values) {
            total_minutes = total_minutes + Integer.parseInt(s);
        }
        Integer hours = total_minutes / 60;
        Integer minutes = total_minutes % 60;

        return minutes;
    }//end minutes_total

    /**
     * Calculates the average hours from users' time data
     * @param values String List
     * @return Integer
     */
    private Integer time_average_hours(List<String>values){
        StringBuilder sb = new StringBuilder();
        Integer total_minutes = 0;
        int count = 0;
        //gets the total of all the values in the database
        for (String s : values) {
            total_minutes = total_minutes + Integer.parseInt(s);
            count++;
        }
        Integer avg_minutes_total = total_minutes/count;
        Integer avg_hours = avg_minutes_total / 60;
        Integer avg_minutes = avg_minutes_total % 60;

        return avg_hours;
    }//end output

    /**
     * Calculates the average minutes from users' time data
     * @param values String List
     * @return Integer
     */
    private Integer time_average_minutes(List<String>values){
        StringBuilder sb = new StringBuilder();
        Integer total_minutes = 0;
        int count = 0;
        //gets the total of all the values in the database
        for (String s : values) {
            total_minutes = total_minutes + Integer.parseInt(s);
            count++;
        }
        Integer avg_minutes_total = total_minutes/count;
        Integer avg_hours = avg_minutes_total / 60;
        Integer avg_minutes = avg_minutes_total % 60;

        return avg_minutes;
    }//end output

    /**
     * calculates the total time in MINUTES
     * @param values String List
     * @return Integer
     */
    private Integer time_average_total_minutes(List<String>values){
        StringBuilder sb = new StringBuilder();
        Integer total_minutes = 0;
        int count = 0;
        //gets the total of all the values in the database
        for (String s : values) {
            total_minutes = total_minutes + Integer.parseInt(s);
            count++;
        }
        Integer avg_minutes_total = total_minutes/count;

        return avg_minutes_total;
    }//end output

    /**
     * Calculates the sum of all financial data
     * @param values String List
     * @return Integer
     */
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

    /**
     * calculates the average financial input
     * @param values String List
     * @return Integer
     */
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
            Intent newIntent = new Intent(view_stats.this,view_profile.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
        } else if (id == R.id.profile_edit) {
            Intent newIntent = new Intent(view_stats.this,edit_profile.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
        } else if (id == R.id.user_settings) {
            Intent newIntent = new Intent(view_stats.this,user_settings.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
        } else if (id == R.id.dashboard_page){
            Intent newIntent = new Intent(view_stats.this,Dashboard.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}