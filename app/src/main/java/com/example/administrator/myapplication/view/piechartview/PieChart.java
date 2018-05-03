package com.example.administrator.myapplication.view.piechartview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiangpengfei on 2018/2/24.
 */

public class PieChart extends View {
    private static final String TAG = PieChart.class.getSimpleName();

    private static final int[] colors = new int[]{
            Color.parseColor("#A171D7"),
            Color.parseColor("#C96B83"),
            Color.parseColor("#C5BD75"),
            Color.parseColor("#C68D6F"),
            Color.parseColor("#87C5C2"),
            Color.parseColor("#483D8B"),
            Color.parseColor("#EEE8AA"),
            Color.parseColor("#FF8C00"),
            Color.parseColor("#EECBAD"),
            Color.parseColor("#336699"),
    };

    private List<String> names;
    private List<Float> datas;
    private String title;

    private int width;
    private int height;

    private Paint dataPain;
    private TextPaint textPaint;

    private RectF rectF;

    public PieChart(Context context) {
        super(context);
        rectF = new RectF();
    }

    public PieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        dataPain = new Paint();
        dataPain.setAntiAlias(true);
        dataPain.setStyle(Paint.Style.FILL);
        textPaint = new TextPaint();

        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();
        int max = (int) (Math.min(width, height) * 0.7);
        rectF.left = (width - max) / 2;
        rectF.top = (height - max) / 2;
        rectF.right = (width + max) / 2;
        rectF.bottom = (height + max) / 2;

        drawWithAnimator(canvas, datas);
    }

    public void initDatas(List<String> names, List<Float> datas, String title) {
        if (names == null || datas == null || names.size() != datas.size()) {
            Log.e(TAG, "参数错误");
            return;
        }

        this.names = names;
        this.datas = datas;
        this.title = title;

        value = 0;
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                value = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();

    }

    int value = 100;

    private void drawWithAnimator(Canvas canvas, List<Float> datas) {
        float angle = ((float) value / 100) * 360;
        List<Float> angles = getAngles(datas);
        float sumAngles = 0;
        for (int i = 0; i < angles.size(); i++) {
            sumAngles += angles.get(i);
            if (sumAngles > angle) {
                return;
            } else {
                dataPain.setColor(colors[i % colors.length]);
                if (i < 1) {
                    if (angle - angles.get(i) >= 0) {
                        canvas.drawArc(rectF, angle, -angles.get(i), true, dataPain);
                    } else {
                        canvas.drawArc(rectF, angle, -angle, true, dataPain);
                    }
                } else {
                    if (angle - angles.get(i - 1) - angles.get(i) >= 0) {
                        canvas.drawArc(rectF, angle - angles.get(i - 1), -angles.get(i), true, dataPain);
                    } else {
                        canvas.drawArc(rectF, angle - angles.get(i - 1), -angle + angles.get(i - 1), true, dataPain);
                    }

                }
            }
        }


    }

    private float getTotleValue(List<Float> datas) {
        float sum = 0;
        for (float f : datas) {
            sum += f;
        }
        return sum;
    }

    private List<Float> getAngles(List<Float> datas) {
        float sum = getTotleValue(datas);
        List<Float> angles = new ArrayList<>();
        for (float f : datas) {
            angles.add((f / sum) * 360);
        }
        return angles;
    }


}
