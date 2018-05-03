package com.example.administrator.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.myapplication.view.linechartview.LineChartView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiangpengfei on 2018/2/28.
 */

public class LineChartActivity extends AppCompatActivity {
    private LineChartView linechart;
    private ViewPager viewpager;
    private List<View> views;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linechart);
        linechart = (LineChartView) findViewById(R.id.linechart);
        viewpager = (ViewPager) findViewById(R.id.viewpager);

        List<LineChartView.LineBean> datas = new ArrayList<>();
        datas.add(new LineChartView.LineBean("1", 3000f));
        datas.add(new LineChartView.LineBean("2", 1234f));
        datas.add(new LineChartView.LineBean("3", 6823f));
        datas.add(new LineChartView.LineBean("4", 8000f));
        datas.add(new LineChartView.LineBean("5", 2222f));
        datas.add(new LineChartView.LineBean("6", 1000f));
        datas.add(new LineChartView.LineBean("7", 7012f));
        datas.add(new LineChartView.LineBean("8", 2091f));
        datas.add(new LineChartView.LineBean("9", 8000f));
        datas.add(new LineChartView.LineBean("10", 2000f));
        datas.add(new LineChartView.LineBean("11", 2000f));
        datas.add(new LineChartView.LineBean("12", 2000f));
        datas.add(new LineChartView.LineBean("13", 2000f));
        datas.add(new LineChartView.LineBean("14", 2000f));
        datas.add(new LineChartView.LineBean("15", 2000f));
        datas.add(new LineChartView.LineBean("16", 2000f));
        datas.add(new LineChartView.LineBean("17", 2000f));
        datas.add(new LineChartView.LineBean("18", 2000f));
        datas.add(new LineChartView.LineBean("19", 2000f));
        datas.add(new LineChartView.LineBean("20", 2000f));

        linechart.setDatas(datas);
        linechart.setUnit("(时)", "(个)");
        linechart.setMaxAndMin(8000, 0);
        linechart.setDrawTitle(true);
        linechart.setTitle("折线图");
        linechart.setGradient(false);
        linechart.setDrawOtherYAxis(false);
        linechart.setDrawYAxisZero(false);
        linechart.initView();
        viewpager(datas);

    }

    private void viewpager(List<LineChartView.LineBean> datas) {
        views = new ArrayList<>();
        LineChartView view1 = new LineChartView(this);
        view1.setLineColor(Color.parseColor("#ffab1a"));
        view1.setGradientColorDark(Color.parseColor("#80ffab1a"));
        view1.setGradientColorLight(Color.parseColor("#0Dffab1a"));
        view1.setUnit("(时)", "(个)");
        view1.setDrawYAxisZero(false);
        view1.setDrawOtherYAxis(false);
        view1.setSpaceDp(5);
        view1.setMaxAndMin(8000, 0);
        view1.setDatas(datas);
        view1.initView();
        LineChartView view2 = new LineChartView(this);
        view2.setLineColor(Color.parseColor("#2bd965"));
        view2.setGradientColorDark(Color.parseColor("#802bd965"));
        view2.setGradientColorLight(Color.parseColor("#0D2bd965"));
        view2.setUnit("(时)", "(个)");
        view2.setDrawYAxisZero(false);
        view2.setDrawOtherYAxis(false);
        view2.setSpaceDp(5);
        view2.setMaxAndMin(8000, 0);
        view2.setDatas(datas);
        view2.initView();
        views.add(view1);
        views.add(view2);

        PagerAdapter adapter = new VpAdapter(views);
        viewpager.setAdapter(adapter);
    }

    private class VpAdapter extends PagerAdapter {
        private List<View> mDatas;

        public VpAdapter(List<View> datas) {
            if (datas != null) {
                mDatas = datas;
            } else {
                mDatas = new ArrayList<>();
            }
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mDatas.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mDatas.get(position));
            return mDatas.get(position);
        }
    }
}
