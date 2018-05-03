package com.example.administrator.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.administrator.myapplication.view.piechartview.PieChart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiangpengfei on 2018/2/24.
 */

public class PieChartActivity extends AppCompatActivity {
    private PieChart piechart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piechart);
        piechart = (PieChart) findViewById(R.id.piechart);

        List<String> names = new ArrayList<>();
        names.add("android");
        names.add("ios");
        names.add("php");
        List<Float> datas = new ArrayList<>();
        datas.add(300f);
        datas.add(500f);
        datas.add(700f);

        piechart.initDatas(names, datas, "");

    }
}
