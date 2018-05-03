package com.example.administrator.myapplication.view.linechartview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.myapplication.util.DensityUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiangpengfei on 2018/2/28.
 * <p>
 * 折线图
 * <p>
 * 可设置数据区域渐变色填充，可设置当数据超过最大数据量时网格区域可以左右滑动
 */

public class LineChartView extends View implements GestureDetector.OnGestureListener, View.OnTouchListener {
    private static final String TAG = LineChartView.class.getSimpleName();

    private int mWidth; // 控件显示宽度
    private int mHeight;    // 控件显示高度

    private int axisColor = Color.parseColor("#d9d9d9");  // 坐标轴颜色
    private int axisWidthPx = 1;  // 坐标轴宽度
    private int xAxisType = AXIS_TYPE_DASHED;  // X轴样式,虚线、实线
    private int yAxisType = AXIS_TYPE_DASHED;  // Y轴样式，虚线、实线
    private boolean drawXAxisZero = true;   // 是否绘制数值为零的x轴
    private boolean drawOtherXAxis = true;  // 是否绘制其他数值的x轴
    private boolean drawYAxisZero = true;  // 是否绘制数值为零的y轴
    private boolean drawOtherYAxis = true; // 是否绘制其他数值的y轴
    private boolean drawTitle = false;  // 是否绘制标题
    private int height = 0; // 网格区域高度
    private float topRow = 0;   // 最上方y轴所在绘制的位置
    private float valueSpan = 0;    // 数据的公差
    private float rowSpanPx = 0;    // y轴方向上每个间隔的大小
    private float columSpanPx = 0;  // x轴方向上每个间隔的大小

    private float[] dash;   // 虚线显示样式

    // 网格线的样式
    public static final int AXIS_TYPE_DASHED = 0; // 虚线
    public static final int AXIS_TYPE_SOLID = 1;  // 实线

    @IntDef({AXIS_TYPE_DASHED, AXIS_TYPE_SOLID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AxisType {

    }

    // 标题文字位置
    public static final int TOP_LEFT = 0;
    public static final int TOP_CENTERE = 1;
    public static final int TOP_RIGHT = 2;
    public static final int BOTTOM_LEFT = 3;
    public static final int BOTTOM_CENTER = 4;
    public static final int BOTTOM_RIGHT = 5;

    @IntDef({TOP_LEFT, TOP_CENTERE, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TitleGravity {

    }

    private int titleColor = Color.BLACK;   // 标题文字的颜色
    private int titleSize = 18; // 标题文字的大小
    private int textColor = Color.parseColor("#333333");    // x、y轴文字的颜色
    private int textSize = 13;  // x、y轴文字的大小
    private int lineColor = Color.parseColor("#ffab1a");    // 折线颜色
    private int lineWidthDp = 1;    // 折线宽
    private int backgroudColor = Color.parseColor("#ffffff");   // 背景色
    private int pointRadiusDp = 2;  // 点的半径
    private boolean isPointSolid = false;   // 点是否是实心的
    private int gradientColorDark = Color.parseColor("#80ffab1a");  // 渐变色深色
    private int gradientColorLight = Color.parseColor("#0Dffab1a"); // 渐变色浅色

    private TextPaint sizePaint;    // 大小画笔
    private TextPaint textPaint;    // 文字画笔
    private Paint linePaint;    // 折线画笔
    private Paint axisPanit;    // x坐标轴画笔
    private Paint gradientPaint;    // 渐变色画笔
    private Path linePath;  // 折线路径
    private Path gradientPath;  // 渐变色路径
    private float animatorProgress; // 动画进度
    private Paint dataPaint;    // 数据点画笔

    private List<LineBean> mDatas;  // 数据

    private int row = 4;    // 不包含y=0
    private static final int minRowNumber = 2;  // 设置数据行数的最小限制
    private static final int maxRowNumber = 100;    // 设置数据行数的最大限制

    private float minY = 0; // y轴数据的最小值
    private float maxY = 100;   // y轴数据的最大值

    private int spaceDp = 10;   // 整体的绘制区域与空间边的间距

    private int textMarginGridDp = 5;   // x、y轴文字与表区域的间距
    // x轴数据文字的中心是与纵轴对齐的，当x数据文字宽度比较大时可以改变此值来调整x轴数据文字与x轴单位文字的间距
    private int unitXMarginColumnDp = 5;

    private int startX; // x轴方向上的起始位置

    private Rect rect = new Rect();

    private int maxWidthOfTextY = 0;    // y轴数据文字最大宽度,根据y轴数据文字计算得到

    private int maxXNumber = 12;    // x轴最大的可见数据量

    private String unitY = "";  // y轴单位
    private String unitX = "";  // x轴单位
    private String title = "";  // 标题

    private boolean enable = true;  // 是否支持手势等操作，目前只支持左右滑动
    private GestureDetector mGestureDetector;
    private int totleWidth; // 根据数据的多少计算出控件的总宽度

    private int titleGravity = TOP_CENTERE; // 标题文字显示的位置
    private boolean isGradient = true;  // x轴与折线图包裹的区域是否减半色填充

    private boolean isAutoAxis = false; // 是否根据数据值自动生成y轴文字，当此值为true时maxY、minY将根据值自动计算
    private float valueOffset = 5;  // isAutoAxis=true时结合此数值自动计算出maxY、minY


    /**
     * 设置轴线颜色
     *
     * @param axisColor
     */
    public void setAxisColor(int axisColor) {
        this.axisColor = axisColor;
    }

    /**
     * 设置轴线宽度
     *
     * @param axisWidthPx
     */
    public void setAxisWidthPx(int axisWidthPx) {
        this.axisWidthPx = axisWidthPx;
    }

    /**
     * 设置x州的显示样式，目前有实线和虚线两种
     *
     * @param xAxisType
     */
    public void setxAxisType(@AxisType int xAxisType) {
        this.xAxisType = xAxisType;
    }

    /**
     * 设置y轴的显示样式,目前有实线和虚线两种
     *
     * @param yAxisType
     */
    public void setyAxisType(@AxisType int yAxisType) {
        this.yAxisType = yAxisType;
    }

    /**
     * 是否绘制最下方的x轴
     *
     * @param drawXAxisZero
     */
    public void setDrawXAxisZero(boolean drawXAxisZero) {
        this.drawXAxisZero = drawXAxisZero;
    }

    /**
     * 是否绘制其他的x轴
     *
     * @param drawOtherXAxis
     */
    public void setDrawOtherXAxis(boolean drawOtherXAxis) {
        this.drawOtherXAxis = drawOtherXAxis;
    }

    /**
     * 是否绘制最左侧的y轴
     *
     * @param drawYAxisZero
     */
    public void setDrawYAxisZero(boolean drawYAxisZero) {
        this.drawYAxisZero = drawYAxisZero;
    }

    /**
     * 是否绘制其他的y轴
     *
     * @param drawOtherYAxis
     */
    public void setDrawOtherYAxis(boolean drawOtherYAxis) {
        this.drawOtherYAxis = drawOtherYAxis;
    }

    /**
     * 是否绘制标题
     *
     * @param drawTitle
     */
    public void setDrawTitle(boolean drawTitle) {
        this.drawTitle = drawTitle;
    }

    /**
     * 虚线显示样式的数组，下标偶数表示线段奇数表示间隙
     *
     * @param dash 长度必须是偶数且要大于等于2
     */
    public void setDash(float[] dash) {
        this.dash = dash;
    }

    /**
     * 标题颜色
     *
     * @param titleColor
     */
    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    /**
     * 标题大小
     *
     * @param titleSize
     */
    public void setTitleSize(int titleSize) {
        this.titleSize = titleSize;
    }

    /**
     * x、y轴文字颜色
     *
     * @param textColor
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    /**
     * x、y轴文字大小
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    /**
     * 折线颜色
     *
     * @param lineColor
     */
    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    /**
     * 折线宽度
     *
     * @param lineWidthDp
     */
    public void setLineWidthDp(int lineWidthDp) {
        this.lineWidthDp = lineWidthDp;
    }

    /**
     * 背景颜色
     *
     * @param backgroudColor
     */
    public void setBackgroudColor(int backgroudColor) {
        this.backgroudColor = backgroudColor;
    }

    /**
     * 点的半径
     *
     * @param pointRadiusDp
     */
    public void setPointRadiusDp(int pointRadiusDp) {
        this.pointRadiusDp = pointRadiusDp;
    }

    /**
     * 是否将数据点绘制成实心点
     *
     * @param pointSolid true实心 false空心
     */
    public void setPointSolid(boolean pointSolid) {
        isPointSolid = pointSolid;
    }

    /**
     * 设置渐变色的深色
     *
     * @param gradientColorDark
     */
    public void setGradientColorDark(int gradientColorDark) {
        this.gradientColorDark = gradientColorDark;
    }

    /**
     * 设置渐变色的浅色
     *
     * @param gradientColorLight
     */
    public void setGradientColorLight(int gradientColorLight) {
        this.gradientColorLight = gradientColorLight;
    }

    /**
     * 设置y轴方向上的间隔数
     *
     * @param row
     */
    public void setRow(int row) {
        if (row < minRowNumber) {
            Log.e(TAG, "设置的行数不可以小于" + minRowNumber);
            return;
        }
        if (row > maxRowNumber) {
            Log.e(TAG, "设置的行数不可以大于" + maxRowNumber);
            return;
        }
        this.row = row;
    }

    /**
     * 设置整体绘制区域与空间边缘的间距
     *
     * @param spaceDp
     */
    public void setSpaceDp(int spaceDp) {
        this.spaceDp = spaceDp;
    }

    /**
     * 设置x、y轴文字与网格区域的间距
     *
     * @param textMarginGridDp
     */
    public void setTextMarginGridDp(int textMarginGridDp) {
        this.textMarginGridDp = textMarginGridDp;
    }

    /**
     * 设置x轴单位文字与可见区域内最右侧的y轴之间的间距，可用于调整x轴的文字与单位之间的间距
     *
     * @param unitXMarginColumnDp
     */
    public void setUnitXMarginColumnDp(int unitXMarginColumnDp) {
        this.unitXMarginColumnDp = unitXMarginColumnDp;
    }

    /**
     * 设置x轴最大可见数据量
     *
     * @param maxXNumber
     */
    public void setMaxXNumber(int maxXNumber) {
        this.maxXNumber = maxXNumber;
    }

    /**
     * 设置空间是否支持手势操作
     *
     * @param enable
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * 设置标题文字的位置
     *
     * @param titleGravity
     */
    public void setTitleGravity(@TitleGravity int titleGravity) {
        this.titleGravity = titleGravity;
    }

    /**
     * 设置x轴与折线区域是否渐变色填充
     *
     * @param gradient
     */
    public void setGradient(boolean gradient) {
        isGradient = gradient;
    }

    /**
     * 设置数据的最大值和最小值
     *
     * @param maxY
     * @param minY
     */
    public void setMaxAndMin(float maxY, float minY) {
        this.maxY = maxY;
        this.minY = minY;
    }

    /**
     * 设置x、y轴显示文字的单位
     *
     * @param unitX
     * @param unitY
     */
    public void setUnit(String unitX, String unitY) {
        this.unitX = unitX;
        this.unitY = unitY;
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        if (title != null) {
            this.title = title;
        }
    }

    /**
     * 设置数据
     *
     * @param datas
     */
    public void setDatas(List<LineBean> datas) {
        this.mDatas = datas;
    }


    public LineChartView(Context context) {
        super(context);
        this.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(getContext(), this);
        init();
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(getContext(), this);
        init();
    }

    private void init() {
        // 大小画笔
        sizePaint = new TextPaint();
        // 文字画笔
        textPaint = new TextPaint();
        // 折线画笔
        linePaint = new Paint();
        // 数据点中心孔画笔
        dataPaint = new Paint();
        // x坐标轴画笔
        axisPanit = new Paint();
        // 渐变色画笔
        gradientPaint = new Paint();
        // 折线路径
        linePath = new Path();
        // 渐变色路径
        gradientPath = new Path();
        // 折线数据
        mDatas = new ArrayList<>();
        // 虚线设置
        dash = new float[]{DensityUtil.dip2px(getContext(), 2),
                DensityUtil.dip2px(getContext(), 2),
                DensityUtil.dip2px(getContext(), 2),
                DensityUtil.dip2px(getContext(), 2)};
        // 关闭加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    /**
     * 当属性都设置完成之后调用此方法进行数据的绘制
     */
    public void initView() {
        if (mDatas == null) {
            return;
        }
        textPaint.setAntiAlias(true);

        linePaint.setColor(lineColor);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(DensityUtil.dip2px(getContext(), lineWidthDp));

        dataPaint.setAntiAlias(true);
        dataPaint.setStrokeWidth(DensityUtil.dip2px(getContext(), lineWidthDp));

        axisPanit.setColor(axisColor);
        axisPanit.setStyle(Paint.Style.STROKE);
        axisPanit.setAntiAlias(true);
        axisPanit.setStrokeWidth(axisWidthPx);

        gradientPaint.setAntiAlias(true);

        this.setBackgroundColor(backgroudColor);

        maxWidthOfTextY = getTextWidth(subZeroAndDot(String.valueOf(maxY)), textSize);

        invalidate();
    }

    private float getMax() {
        float max = Float.MIN_VALUE;
        if (mDatas != null && mDatas.size() > 0) {
            for (int i = 0; i < mDatas.size(); i++) {
                if (mDatas.get(i) != null && mDatas.get(i).getY() > max) {
                    max = mDatas.get(i).getY();
                }
            }
        }
        return max;
    }

    private float getMin() {
        float min = Float.MAX_VALUE;
        if (mDatas != null && mDatas.size() > 0) {
            for (int i = 0; i < mDatas.size(); i++) {
                if (mDatas.get(i) != null && mDatas.get(i).getY() < min) {
                    min = mDatas.get(i).getY();
                }
            }
        }
        return min;
    }

    public float getMaxY() {
        float maxY = Float.MIN_VALUE;
        if (mDatas != null && mDatas.size() != 0) {
            for (int i = 0; i < mDatas.size(); i++) {
                if (mDatas.get(i) != null && mDatas.get(i).getY() > maxY) {
                    maxY = mDatas.get(i).getY();
                }
            }
        }
        return maxY;
    }

    public float getMinY() {
        float minY = Float.MAX_VALUE;
        if (mDatas != null && mDatas.size() != 0) {
            for (int i = 0; i < mDatas.size(); i++) {
                if (mDatas.get(i) != null && mDatas.get(i).getY() < minY) {
                    minY = mDatas.get(i).getY();
                }
            }
        }
        return minY;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();

        float sumSpan = maxY - minY; // 最大最小数据的差值;
        valueSpan = sumSpan / row;
        // 控件高度-上侧空隙-下侧空隙-x文字高度-y单位高度-y文字高度的一半-y单位与表的间距-x文字与表的间距
        height = mHeight - spacePx() * 2 - (int) (textSizePx() * 2.5) - marginPx() * 2;   // 最下与最上两条横线的高度
        topRow = spacePx() + (int) (textSizePx() * 1.5) + marginPx(); // 最上面一条横线的y坐标

        if (mDatas.size() > maxXNumber || mDatas.size() == 0) {
            columSpanPx = (mWidth - maxWidthOfTextY - marginPx() * 2 - spacePx() * 2 - getTextWidth(unitX, textSize) - marginColumnPx()) / (maxXNumber - 1);
        } else {
            columSpanPx = (mWidth - maxWidthOfTextY - marginPx() * 2 - spacePx() * 2 - getTextWidth(unitX, textSize) - marginColumnPx()) / (mDatas.size() - 1);
        }

        if (mDatas.size() > maxXNumber) {
            totleWidth = (int) (mWidth + columSpanPx * (mDatas.size() - maxXNumber) + 0.5f);
        } else {
            totleWidth = mWidth;
        }

        drawGrid(canvas);
        drawLine(canvas);
        drawText(canvas);
        drawTitle(canvas);

    }

    private void drawGrid(Canvas canvas) {
        if (mDatas == null) {
            return;
        }
        if (getMaxY() < getMinY()) {
            return;
        }
        if (drawTitle) {
            // 原本高度-标题高度-间距高度
            height = height - titleSizePx() - marginPx();
            if (titleGravity == TOP_LEFT || titleGravity == TOP_CENTERE || titleGravity == TOP_RIGHT) {
                topRow = topRow + titleSizePx() + marginPx();
            }
        }
        if (getMinY() < 0) {
            // x轴的文字绘制在图表内
            height = height + textSizePx() + marginPx();
        }
        rowSpanPx = height / row;
        // 画横线
        if (xAxisType == AXIS_TYPE_DASHED) {
            PathEffect effect = new DashPathEffect(dash, 0);
            axisPanit.setPathEffect(effect);
        } else if (xAxisType == AXIS_TYPE_SOLID) {
            PathEffect effect = new DashPathEffect(new float[]{100, 0}, 0);
            axisPanit.setPathEffect(effect);
        }
        Path pathX = new Path();
        for (int i = 0; i < row + 1; i++) {
            if (maxY - i * valueSpan == 0 && drawXAxisZero) {
                pathX.moveTo(maxWidthOfTextY + marginPx() + spacePx(), topRow + i * rowSpanPx);
                pathX.lineTo(mWidth - spacePx(), topRow + i * rowSpanPx);
                canvas.drawPath(pathX, axisPanit);
            }
            if (maxY - i * valueSpan != 0 && drawOtherXAxis) {
                pathX.moveTo(maxWidthOfTextY + marginPx() + spacePx(), topRow + i * rowSpanPx);
                pathX.lineTo(mWidth - spacePx(), topRow + i * rowSpanPx);
                canvas.drawPath(pathX, axisPanit);
            }
        }

        if (maxY >= 0 && minY <= 0) {
            if (!hasZero(maxY, minY, valueSpan)) {
                // TODO 绘制零所在的X轴
                pathX.moveTo(maxWidthOfTextY + marginPx() + spacePx(), topRow + ((maxY/(maxY-minY))*height));
                pathX.lineTo(mWidth - spacePx(),topRow + ((maxY/(maxY-minY))*height));
                canvas.drawPath(pathX,axisPanit);
            }
        }


        // 画竖线
        if (yAxisType == AXIS_TYPE_DASHED) {
            PathEffect effect = new DashPathEffect(dash, 0);
            axisPanit.setPathEffect(effect);
        } else if (yAxisType == AXIS_TYPE_SOLID) {
            PathEffect effect = new DashPathEffect(new float[]{100, 0}, 0);
            axisPanit.setPathEffect(effect);
        }
        Path pathY = new Path();
        for (int i = 0; i < mDatas.size(); i++) {
            if (i * columSpanPx - startX > mWidth - (maxWidthOfTextY + marginPx() * 2 + spacePx() * 2 + getTextWidth(unitX, textSize))) {
                break;
            }
            if (i * columSpanPx - startX < 0) {
                continue;
            }
            if (drawYAxisZero) {
                pathY.moveTo(maxWidthOfTextY + marginPx() + spacePx(), topRow);
                pathY.lineTo(maxWidthOfTextY + marginPx() + spacePx(), topRow + rowSpanPx * row);
                canvas.drawPath(pathY, axisPanit);
            }
            if (drawOtherYAxis) {
                pathY.moveTo(i * columSpanPx + maxWidthOfTextY + marginPx() + spacePx() - startX, topRow);
                pathY.lineTo(i * columSpanPx + maxWidthOfTextY + marginPx() + spacePx() - startX, topRow + rowSpanPx * row);
                canvas.drawPath(pathY, axisPanit);
            }
        }

    }

    private void drawLine(Canvas canvas) {
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        // 先将路径绘制玩在绘制点，这样当需要绘制空心点时，直接用背景色绘制圆填充点中心，如此背景色就会覆盖其区域内的直线
        linePath.reset();
        gradientPath.reset();
        gradientPath.moveTo(maxWidthOfTextY + marginPx() + spacePx(), topRow + height);
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i) != null) {
                float value = mDatas.get(i).getY();
                float coordinateY = topRow + height - (value / maxY) * height;
                if (i == 0) {
                    linePath.moveTo(maxWidthOfTextY + marginPx() + spacePx(), coordinateY);
                    gradientPath.lineTo(maxWidthOfTextY + marginPx() + spacePx(), coordinateY);
                } else {
                    linePath.lineTo(i * columSpanPx + maxWidthOfTextY + marginPx() + spacePx(), coordinateY);
                    gradientPath.lineTo(i * columSpanPx + maxWidthOfTextY + marginPx() + spacePx(), coordinateY);
                }
            }
        }
        gradientPath.lineTo((mDatas.size() - 1) * columSpanPx + maxWidthOfTextY + marginPx() + spacePx(), topRow + height);
        linePath.offset(-startX, 0);
        gradientPath.offset(-startX, 0);
        // 绘制折线
        canvas.drawPath(linePath, linePaint);
        // 绘制渐变色
        gradientPaint.setStyle(Paint.Style.FILL);
        LinearGradient linearGradient1 = new LinearGradient(0, topRow, 0, topRow + height, gradientColorDark, gradientColorLight, Shader.TileMode.CLAMP);
        gradientPaint.setShader(linearGradient1);
        if (isGradient) {
            canvas.drawPath(gradientPath, gradientPaint);
        }
        // 绘制点
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i) != null) {
                float value = mDatas.get(i).getY();
                float coordinateY = topRow + height - (value / maxY) * height;
                if (isPointSolid) {  // 实心
                    dataPaint.setColor(lineColor);
                    dataPaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(i * columSpanPx + maxWidthOfTextY + marginPx() + spacePx() - startX, coordinateY, pointRadioPx(), dataPaint);
                } else {  // 空心
                    dataPaint.setColor(backgroudColor);
                    dataPaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(i * columSpanPx + maxWidthOfTextY + marginPx() + spacePx() - startX, coordinateY, pointRadioPx(), dataPaint);
                    dataPaint.setColor(lineColor);
                    dataPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(i * columSpanPx + maxWidthOfTextY + marginPx() + spacePx() - startX, coordinateY, pointRadioPx(), dataPaint);
                }
            }
        }

        // 用控件背景图覆盖表左右侧，否则在滑动的时候表左右侧也会绘制路径
        Paint rectPaint = new Paint();
        rectPaint.setColor(backgroudColor);
        rectPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, spacePx() + maxWidthOfTextY + marginPx(), mHeight, rectPaint);
        canvas.drawRect(mWidth - spacePx(), 0, mWidth, mHeight, rectPaint);

    }

    private void drawText(Canvas canvas) {
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }

        textPaint.setColor(textColor);
        textPaint.setTextSize(DensityUtil.dip2px(getContext(), textSize));
        for (int i = 0; i < row + 1; i++) {
            String text = subZeroAndDot(String.valueOf(maxY - valueSpan * i));
            canvas.drawText(text, maxWidthOfTextY + spacePx() - getTextWidth(text, textSize),
                    topRow + i * rowSpanPx + textSizePx() / 2, textPaint);
        }

        canvas.drawText(unitY, maxWidthOfTextY + spacePx() - getTextWidth(unitY, textSize),
                topRow - textSizePx() / 2 - marginPx(), textPaint);

        for (int i = 0; i < mDatas.size(); i++) {
            if (i * columSpanPx - startX > mWidth - (maxWidthOfTextY + marginPx() * 2 + spacePx() * 2 + getTextWidth(unitX, textSize))) {
                break;
            }
            if (i * columSpanPx - startX < 0) {
                continue;
            }

            canvas.drawText(mDatas.get(i).getX(), i * columSpanPx + maxWidthOfTextY + marginPx() + spacePx() - startX
                            - getTextWidth(mDatas.get(i).getX(), textSize) / 2,
                    topRow + rowSpanPx * row + textSizePx() + marginPx(), textPaint);
        }
        canvas.drawText(unitX, mWidth - spacePx() - getTextWidth(unitX, textSize),
                topRow + rowSpanPx * row + textSizePx() + marginPx(), textPaint);

    }

    private void drawTitle(Canvas canvas) {
        if (title == null || "".equals(title)) {
            return;
        }
        if (drawTitle) {
            float titleX = 0;
            float titleY = 0;
            switch (titleGravity) {
                case TOP_LEFT:
                    titleX = spacePx() + marginPx() + maxWidthOfTextY;
                    titleY = spacePx() + titleSizePx();
                    break;
                case TOP_CENTERE:
                    titleX = spacePx() + marginPx() + maxWidthOfTextY + (mWidth - spacePx() * 2 - marginPx() - maxWidthOfTextY) / 2 - getTextWidth(title, titleSize) / 2;
                    titleY = spacePx() + titleSizePx();
                    break;
                case TOP_RIGHT:
                    titleX = mWidth - spacePx() - getTextWidth(title, titleSize);
                    titleY = spacePx() + titleSizePx();
                    break;
                case BOTTOM_LEFT:
                    titleX = spacePx() + marginPx() + maxWidthOfTextY;
                    titleY = mHeight - spacePx();
                    break;
                case BOTTOM_CENTER:
                    titleX = spacePx() + marginPx() + maxWidthOfTextY + (mWidth - spacePx() * 2 - marginPx() - maxWidthOfTextY) / 2 - getTextWidth(title, titleSize) / 2;
                    titleY = mHeight - spacePx();
                    break;
                case BOTTOM_RIGHT:
                    titleX = mWidth - spacePx() - getTextWidth(title, titleSize);
                    titleY = mHeight - spacePx();
                    break;
            }

            textPaint.setColor(titleColor);
            textPaint.setTextSize(DensityUtil.dip2px(getContext(), titleSize));
            canvas.drawText(title, titleX, titleY, textPaint);
        }


    }

    private int getTextWidth(String text, int textsize) {
        sizePaint.setTextSize(DensityUtil.dip2pxf(getContext(), textsize));
        sizePaint.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }

    private int getTextHeight(int textsize) {
        sizePaint.setTextSize(DensityUtil.dip2pxf(getContext(), textsize));
        sizePaint.getTextBounds("测试", 0, 2, rect);
        return rect.height();
    }

    private int spacePx() {
        return DensityUtil.dip2px(getContext(), spaceDp);
    }

    private int marginPx() {
        return DensityUtil.dip2px(getContext(), textMarginGridDp);
    }

    private int marginColumnPx() {
        return DensityUtil.dip2px(getContext(), unitXMarginColumnDp);
    }

    private int textSizePx() {
        return DensityUtil.dip2px(getContext(), textSize);
    }

    private int titleSizePx() {
        return DensityUtil.dip2px(getContext(), titleSize);
    }

    private int pointRadioPx() {
        return DensityUtil.dip2px(getContext(), pointRadiusDp);
    }

    private int holeRadioPx() {
        return DensityUtil.dip2px(getContext(), pointRadiusDp - lineWidthDp);
    }


    public String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");   // 去掉多余的0
            s = s.replaceAll("[.]$", "");   // 如最后一位是.则去掉
        }
        return s;
    }


    private boolean hasZero(float maxValue, float minValue, float spanValue) {
        while (maxValue > minValue) {
            if (maxValue == 0) {
                return true;
            } else {
                maxValue -= spanValue;
            }
        }
        return false;
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // distanceX向左滑为正值，向右滑为负值；distanceY向上滑为正值，向下滑为负值；
        if (totleWidth > mWidth) {
            startX += distanceX;
            startX = startX < 0 ? 0 : startX;
            startX = totleWidth < mWidth ? 0 : (startX < 0 ? 0 : startX);
            startX = ((startX + mWidth) > totleWidth && startX > 0) ? (totleWidth - mWidth) : startX;

            invalidate();
        }

        return false;

    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    private float x1 = 0;
    private float x2 = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!enable) {
            return false;
        }
        return mGestureDetector.onTouchEvent(event);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // 处理与可滑动的父控件质检的滑动冲突问题，当滑动到左右两端时将滑动事件交个父控件处理
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            x1 = event.getX();
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        if (action == MotionEvent.ACTION_MOVE) {
            x2 = event.getX();
            if (getParent() != null) {
                if ((x2 - x1 > 0 && startX > 0) || (x2 - x1 < 0 && startX + mWidth < totleWidth)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    ViewGroup viewGroup = (ViewGroup) getParent();
                    if (viewGroup != null) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
            }

        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public static class LineBean {
        private String x;
        private float y;

        public LineBean(String x, float y) {
            this.x = x;
            this.y = y;
        }

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
}
