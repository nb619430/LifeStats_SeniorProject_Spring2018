package com.example.nick.lifestats;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class finance_graph extends AppCompatActivity {
    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_graph);

        chart = (LineChart) findViewById(R.id.test_chart);
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("1");
        labels.add("2");
        labels.add("3");
        labels.add("4");
        labels.add("5");
        labels.add("6");

        ArrayList<Entry> Y = new ArrayList<Entry>();
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
}
