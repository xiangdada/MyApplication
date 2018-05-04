package com.example.administrator.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.administrator.myapplication.eventdispatch.EventDispatchActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_multiselect;
    private Button btn_eventdispatch;
    private Button btn_drawview;
    private Button btn_piechart;
    private Button btn_linechart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_multiselect = (Button) findViewById(R.id.btn_multiselect);
        btn_eventdispatch = (Button) findViewById(R.id.btn_eventdispatch);
        btn_drawview = (Button) findViewById(R.id.btn_drawview);
        btn_piechart = (Button) findViewById(R.id.btn_piechart);
        btn_linechart = (Button) findViewById(R.id.btn_linechart);

        addListener();
    }

    private void addListener() {
        btn_multiselect.setOnClickListener(this);
        btn_eventdispatch.setOnClickListener(this);
        btn_drawview.setOnClickListener(this);
        btn_piechart.setOnClickListener(this);
        btn_linechart.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_multiselect:
                startActivity(new Intent(MainActivity.this, MultiselectActivity.class));
                break;
            case R.id.btn_eventdispatch:
                startActivity(new Intent(MainActivity.this, EventDispatchActivity.class));
                break;
            case R.id.btn_drawview:
                startActivity(new Intent(MainActivity.this, DrawViewActivity.class));
                break;
            case R.id.btn_piechart:
                startActivity(new Intent(MainActivity.this, PieChartActivity.class));
                break;
            case R.id.btn_linechart:
                startActivity(new Intent(MainActivity.this, LineChartActivity.class));
                break;
        }
    }
}
