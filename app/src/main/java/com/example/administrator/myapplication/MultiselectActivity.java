package com.example.administrator.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiangpengfei on 2018/2/2.
 */

public class MultiselectActivity extends Activity {

    private TextView send;
    private TextView edit;
    private TextView reset;
    private TextView allselect;
    private TextView reverseselect;
    private ListView listview;
    private ListViewAdapter adapter;
    private List<ListData> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiselect);
        Log.e("发送", "onCreate");
        send = (TextView) findViewById(R.id.send);
        edit = (TextView) findViewById(R.id.edit);
        reset = (TextView) findViewById(R.id.reset);
        allselect = (TextView) findViewById(R.id.allselect);
        reverseselect = (TextView) findViewById(R.id.reverseselect);
        listview = (ListView) findViewById(R.id.listview);
        datas = new ArrayList<ListData>();
        adapter = new ListViewAdapter(this, datas);
        listview.setAdapter(adapter);
        data();
        adapter.notifyDataSetChanged();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("编辑".equals(edit.getText().toString())) {
                    send.setVisibility(View.VISIBLE);
                    reset.setVisibility(View.VISIBLE);
                    allselect.setVisibility(View.VISIBLE);
                    reverseselect.setVisibility(View.VISIBLE);
                    edit.setText("取消");
                    adapter.batch();
                } else if ("取消".equals(edit.getText().toString())) {
                    send.setVisibility(View.GONE);
                    reset.setVisibility(View.GONE);
                    allselect.setVisibility(View.GONE);
                    reverseselect.setVisibility(View.GONE);
                    edit.setText("编辑");
                    adapter.batchDisable();
                }

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("发送", "AAAAAAAAAAAAa");
                List<ListData> datas = adapter.send();
                if (datas != null) {
                    Log.e("发送", datas.size() + "");
                    for (int i = 0; i < datas.size(); i++) {
                        Log.e("发送", datas.get(i).getCode());
                    }
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.reSet();
            }
        });

        allselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.allSelect();
            }
        });

        reverseselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.reverseSelect();
            }
        });

    }

    private void data() {
        for (int i = 0; i < 20; i++) {
            ListData listData = new ListData();
            listData.setCode("" + i + 1);
            listData.setText("第" + i + 1 + "条");
            datas.add(listData);
        }
    }
}
