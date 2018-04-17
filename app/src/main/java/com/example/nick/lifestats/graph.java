package com.example.nick.lifestats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class graph extends AppCompatActivity {
    private LineChart chart;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference lifestats_db = database.getReference("user");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String id = user.getUid();

    @Override
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
            //Test for package retrieval
            //Toast toast = Toast.makeText(getApplicationContext(), category_type, Toast.LENGTH_SHORT);
            //toast.show();

        final TextView tv = (TextView) findViewById(R.id.output_test); //TESTING

        if(category.equals("Financial")) {
            DatabaseReference ref = lifestats_db.child(id).child("Data").child("Financial");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(subcategory).exists()) {
                        String data = dataSnapshot.child(subcategory).getValue().toString();
                        String temp = "";
                        for (int i = 0; i < user_data(data).size(); i++) {
                            temp = temp + user_data(data).get(i) + "\n";
                        }
                        tv.setText(temp);
                    } else {
                        TextView output = (TextView) findViewById(R.id.output_tv);
                        output.setText("No data Found!");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });//end reference listener for Finance
        }
        if(category.equals("Time")){
            DatabaseReference ref = lifestats_db.child(id).child("Data").child("Time");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(subcategory).exists()) {
                        String data = dataSnapshot.child(subcategory).getValue().toString();
                        String temp = "";
                        for (int i = 0; i < user_data(data).size(); i++) {
                            temp = temp + user_data(data).get(i) + "\n";
                        }
                        tv.setText(temp);
                    } else {
                        TextView output = (TextView) findViewById(R.id.output_tv);
                        output.setText("No data Found!");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });//end reference listener for Time
        }

        chart = (LineChart) findViewById(R.id.test_chart);
        ArrayList<String> labels = new ArrayList<String>();
        ArrayList<Entry> Y = new ArrayList<Entry>();

        labels.add("1");
        labels.add("2");
        labels.add("3");
        labels.add("4");
        labels.add("5");
        labels.add("6");
        Y.add(new Entry(12f, 0));
        Y.add(new Entry(15f, 1));
        Y.add(new Entry(1f, 2));
        Y.add(new Entry(42f, 3));
        Y.add(new Entry(20f, 4));
        Y.add(new Entry(12f, 5));

        //GENERATE ALL FINANCIAL DATA IN ONE GRAPH!!!!!!! FOR ALL MONEY CATEGORIES!!!!

        LineDataSet dataset = new LineDataSet(Y, "Test");
        LineData data = new LineData(labels, dataset);
        chart.setData(data);

        chart.setDescription("Test Chart For LifeStats");
    }

    //This parses the JSON generated by the database reference call into an arraylist of entries
    private List<String> user_data(String output) {
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
}
