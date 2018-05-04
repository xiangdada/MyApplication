package com.example.administrator.myapplication.eventdispatch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.administrator.myapplication.R;

/**
 * Created by xiangpengfei on 2018/5/3.
 */

public class EventDispatchActivity extends AppCompatActivity {
    private static final String TAG = EventDispatchActivity.class.getSimpleName();

    private MyButton myButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_dispatch);
        myButton = (MyButton) findViewById(R.id.myButton);
        myButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("测试", TAG + " onTouch ACTION_DOWN");
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("测试", TAG + " onTouch ACTION_UP");
                        break;
                }

                return false;
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("测试", TAG + " dispatchTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("测试", TAG + " dispatchTouchEvent ACTION_UP");
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("测试", TAG + " onTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("测试", TAG + " onTouchEvent ACTION_UP");
                break;
        }
        return super.onTouchEvent(event);
    }
}
