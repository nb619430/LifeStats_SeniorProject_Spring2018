package com.example.nick.lifestats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Users data will be graphed and displayed on this page
 */

public class graph extends AppCompatActivity {
    private LineChart chart;

    //Database stuff
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference lifestats_db = database.getReference("user");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String id = user.getUid(); //current user ID

    @Override
    /**
     * This initiates the page itself
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_graph);

        Button back = (Button) findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(graph.this, view_stats.class);
                startActivity(intent);
            }
        });

        final String category = getIntent().getExtras().getString("category","default");
        final String subcategory = getIntent().getExtras().getString("subcategory","default");
        TextView x_label = (TextView) findViewById(R.id.xlabel);

        if(category.equals("Financial")) {
            final LineChart chart = (LineChart) findViewById(R.id.test_chart);
            DatabaseReference ref = lifestats_db.child(id).child("Data").child("Financial");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(subcategory).exists()) {
                        String data = dataSnapshot.child(subcategory).getValue().toString();

                        List<String>temp = user_data(data);
                        String tests[] = new String[temp.size()];
                        temp.toArray(tests);

                        //X axis labels
                        ArrayList<String> X = new ArrayList<String>();
                        int count_x = 0;
                        for(String s : tests) {
                            String index = String.valueOf(count_x);
                            X.add(index);
                            count_x++;
                        }

                        //adds values to the list of Y values with appropriate X value
                        ArrayList<Entry> Y = new ArrayList<Entry>();
                        int count_y = 0;
                        for(String s : tests) {
                            float test = Float.parseFloat(s);
                            Y.add(new Entry(test, count_y));
                            count_y++;
                        }

                        //               GRAPH LABELS
                        //----------------------------------------------
                        TextView y_label = (TextView) findViewById(R.id.ylabel);
                        y_label.setText("Dollars ($)");
                        LineDataSet dataset = new LineDataSet(Y, subcategory);
                        LineData graph_data = new LineData(X, dataset);
                        chart.setData(graph_data);
                        chart.invalidate();
                        chart.setDescription("Financial Data");
                        //----------------------------------------------

                    } else { //default graph if no data is found
                        ArrayList<String> X = new ArrayList<String>();
                        ArrayList<Entry> Y = new ArrayList<Entry>();
                        X.add("X");
                        Y.add(new Entry(0f, 0));
                        LineDataSet dataset = new LineDataSet(Y, "No Data Found");
                        LineData graph_data = new LineData(X, dataset);
                        chart.setData(graph_data);
                        chart.setDescription("NO DATA FOUND");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });//end reference listener for Finance
        }

        //Data is mapped differently if it is time data
        if(category.equals("Time")){
            chart = (LineChart) findViewById(R.id.test_chart);
            DatabaseReference ref = lifestats_db.child(id).child("Data").child("Time");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(subcategory).exists()) {
                        String data = dataSnapshot.child(subcategory).getValue().toString();

                        ArrayList<String> X = new ArrayList<String>();
                        for (int i = 0; i < user_data(data).size(); i++) {
                            String index = String.valueOf(i);
                            X.add(index);
                        }

                        ArrayList<Entry> Y = new ArrayList<Entry>();
                        for (int i = 0; i < user_data(data).size(); i++) {
                            float value = Float.parseFloat(user_data(data).get(user_data(data).size()-(i+1)));
                            Y.add(new Entry(value, i));
                            chart.notifyDataSetChanged();
                            chart.invalidate();
                        }

                        //               GRAPH STUFF
                        //----------------------------------------------
                        TextView y_label = (TextView) findViewById(R.id.ylabel);
                        y_label.setText("Time (Minutes)");
                        LineDataSet dataset = new LineDataSet(Y, subcategory);
                        LineData graph_data = new LineData(X, dataset);
                        chart.setData(graph_data);
                        chart.setDescription("Time Data");
                        //----------------------------------------------

                    } else {
                        ArrayList<String> X = new ArrayList<String>();
                        ArrayList<Entry> Y = new ArrayList<Entry>();
                        X.add("X");
                        Y.add(new Entry(0f, 0));
                        LineDataSet dataset = new LineDataSet(Y, "No Data Found");
                        LineData graph_data = new LineData(X, dataset);
                        chart.setData(graph_data);
                        chart.setDescription("NO DATA FOUND");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });//end reference listener for Time
        }
    }

    /**
     * Parses the JSON generated by the database reference call into an arraylist of user-entered values
     */
    private List<String> user_data(String output) {
        List<String> values = new ArrayList<String>(); //values will be stored here
        for (int i = 0; i < output.length(); i++) {
            int start = 0;
            int end = 0;
            if (output.charAt(i) == '=') {
                start = i;
                for (int j = i; j < output.length(); j++) {
                    if (output.charAt(j) == ',' || output.charAt(j) == '}') { //entries in the JSON are separated by ',' and '}'
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

}
