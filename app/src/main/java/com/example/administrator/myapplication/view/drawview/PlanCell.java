package com.example.administrator.myapplication.view.drawview;

import android.graphics.Point;
import android.graphics.Rect;

import java.io.Serializable;

/**
 * Created by xiangpengfei on 2018/2/8.
 */

public class PlanCell implements Serializable {

    private Rect rect;  // 矩形
    private String text;    // 名称
    private Point test; // 测试点
    private int color;  // 颜色

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Point getTest() {
        return test;
    }

    public void setTest(Point test) {
        this.test = test;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
