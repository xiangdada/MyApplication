package com.example.administrator.myapplication.view.drawview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Vibrator;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.util.DensityUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by xiangpengfei on 2018/2/2.
 */

public class DrawView extends View implements GestureDetector.OnGestureListener, View.OnTouchListener {
    private static final String TAG = DrawView.class.getSimpleName();
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
    private Context mContext;

    private int row = 50;  // 行
    private int column = 50;   // 列
    private int lengthDp = 20;   // 单元格的边长
    private int lengthPx;   // 单元格的边长
    private int flagRadioDp = 3;    // 测试点半径
    private int fdqRadioDp = 5; // 放大器半径

    private int gridColor = getResources().getColor(R.color.line_color);
    private int textColor = Color.WHITE;

    private TextPaint textPaint;
    private Paint gridPaint;
    private Paint rectPaint;
    private Paint flagPaint;

    private int width;  // 控件可见宽度
    private int height; // 控件可见高度
    private int totalWidth; // 控件总的宽度
    private int totalHeight;    // 控件总的高度
    private int startX = 0; // 控件可见区域起点坐标x,当控件可滑动时值随着滑动而变化
    private int startY = 0; // 控件可见区域起点坐标y,当控件可滑动时值随着滑动而变化

    private GestureDetector mGestureDetector;
    private Vibrator vibrator;  // 震动

    private int rectStartX; // 绘制矩形手势按下点，以总的尺寸作为参照
    private int rectStartY;
    private int rectEndX;   // 绘制矩形手势抬起点，以总的尺寸作为参照
    private int rectEndY;
    private int flagStartX; // 绘制测试点的位置，以总的尺寸作为参照
    private int flagStartY;
    private int luyouStartX;    // 绘制路由器的位置，以总的尺寸作为参照
    private int luyouStartY;
    int dragX1 = 0; // 移动矩形时手指长按的落点
    int dragY1 = 0;

    private boolean enable = true;  // 若果设置为false则所有手势及点击事件都无效
    List<PlanCell> planCells = new ArrayList<>();   // 绘制的场景图列表
    private PlanCell planCell;  // 当前正在绘制的场景图

    private Rect rect;  // 当前正在绘制的矩形
    // 手势滑动过程中滑动的位移
    private float scrollX;
    private float scrollY;

    private String text = "";   // 当前绘制场景的名称
    //    private int area = 0;   // 当前绘制场景的面积,当为0或者超出了整个控件范围时则表示不限制绘制
    private int[] area = new int[2];
    private Point flag; // 当前正在绘制的测试点
    List<Point> luyous = new ArrayList<>(); // 绘制的路由器列表
    private Point luyou;    // 当前正在绘制的路由器
    List<Point> fangdaqis = new ArrayList<>();  // 绘制的放大器列表
    private Rect moveRect;  // 当前正在移动的矩形

    private boolean canScrool = false;  // 默认控件不支持整体上下左右的移动
    private boolean longPressMoveRect = true;   // 默认长按绘制好的矩形可以根据手势移动
    private int enlargeMonitorDp = 5;  // 扩大路由器和测试点的点击监听区域范围，否则由于路由器和测试点太小很难选中

    int lyqBitmapWidth; // 路由器的绘制宽度
    int lyqBitmapHeight;    // 路由器的绘制高度

    private String addFlagRectName; // 当前绘制测试点指定的场景

    public static final int STATE_NORMAL = 0;   // 普通模式不可以绘制，如果canScrool==true则改模式下面可以滑动
    public static final int STATE_DRAW_RECT = 1;    // 绘制矩形模式
    public static final int STATE_DRAW_FLAG = 2;    // 绘制测试点
    public static final int STATE_DRAW_LUYOU = 3;   // 绘制路由
    public static final int STATE_DRAW_DELETE = 4;  // 可删除矩形状态
    public static final int STATE_MOVE = 5;   // 可移动矩形状态

    @IntDef({STATE_NORMAL, STATE_DRAW_RECT, STATE_DRAW_FLAG, STATE_DRAW_LUYOU, STATE_DRAW_DELETE, STATE_MOVE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public static final int DOMN = 1;
    public static final int SCROLL = 2;
    public static final int UP = 3;

    private int state = STATE_NORMAL;   // 默认控件的当前状态是普通不可编辑的模式

    private Rect lastRect = new Rect();

    public void setState(@State int state) {
        this.state = state;
    }

    public void setLongPressMoveRect(boolean longPressMoveRect) {
        this.longPressMoveRect = longPressMoveRect;
    }

    private void setMoveRect(Rect moveRect) {
        this.moveRect = moveRect;
    }

    public int getLengthPx() {
        return lengthPx;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<PlanCell> getPlanCells() {
        return planCells;
    }

    public void setPlanCells(List<PlanCell> planCells) {
        if (planCells != null) {
            this.planCells = planCells;
        }
    }

    public void setFangDaQis(List<Point> fangdaqis) {
        if (fangdaqis != null) {
            this.fangdaqis = fangdaqis;
        }
    }

    public void setCanScrool(boolean canScrool) {
        this.canScrool = canScrool;
    }

    public List<Point> getLuyous() {
        return luyous;
    }

    public void setLuyous(List<Point> luyous) {
        if (luyous != null) {
            this.luyous = luyous;
        }
    }

    /**
     * 添加场景
     *
     * @param text 场景名称
     */
    public void addRect(String text) {
        if (text != null) {
            this.text = text;
        } else {
            this.text = "";
        }
        this.area[0] = 0;
        this.area[1] = 0;
    }

    /**
     * 添加场景
     *
     * @param text 场景名称
     * @param area 场景面积
     */
    public void addRect(String text, int area) {
        if (text != null) {
            this.text = text;
        } else {
            this.text = "";
        }
        if (area <= 0) {
            this.area[0] = 0;
            this.area[1] = 0;
        } else if (area == 1 || area == 2 || !isPrimeNumber(area)) {
            this.area[0] = area;
            this.area[1] = area;
        } else {
            if (isPrimeNumber(area)) {
                this.area[0] = area - 1;
                this.area[1] = area + 1;
            }
        }
    }

    public int[] getArea() {
        return area;
    }

    /**
     * 判断一个数是不是质数
     *
     * @param number
     * @return
     */
    private boolean isPrimeNumber(int number) {
        if (number <= 1) {
            return false;
        }
        int length = (int) Math.sqrt(number);
        for (int i = 2; i <= length; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 添加测试点到指定的场景中
     *
     * @param rectName
     */
    public void addFlag(String rectName) {
        if (rectName != null) {
            this.addFlagRectName = rectName;
        }
    }

    public void setStart(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
    }

    public void refresh() {
        invalidate();
    }

    /**
     * 路由器点击监听生效的矩形区域范围
     *
     * @param luyouqi 路由器
     * @return 路由器图标所在矩形加上扩大区域监听范围组成的矩形
     */
    private Rect getLuYouqiRect(Point luyouqi) {
        Rect luYouQiRect = new Rect();
        if (luyouqi != null) {
            luYouQiRect.left = luyouqi.x - (lyqBitmapWidth / 2) - DensityUtil.dip2px(mContext, enlargeMonitorDp);
            luYouQiRect.top = luyouqi.y - lyqBitmapHeight - DensityUtil.dip2px(mContext, enlargeMonitorDp);
            luYouQiRect.right = luyouqi.x + (lyqBitmapWidth / 2) + DensityUtil.dip2px(mContext, enlargeMonitorDp);
            luYouQiRect.bottom = luyouqi.y + DensityUtil.dip2px(mContext, enlargeMonitorDp);
            return luYouQiRect;
        } else {
            return null;
        }
    }

    /**
     * 测试点点击监听生效的矩形区域范围
     *
     * @param flag 测试点
     * @return 测试点图标所在矩形加上扩大区域监听范围组成的矩形
     */
    private Rect getFlagRect(Point flag) {
        Rect flagRect = new Rect();
        if (flag != null) {
            flagRect.left = flag.x - DensityUtil.dip2px(mContext, flagRadioDp) - DensityUtil.dip2px(mContext, enlargeMonitorDp);
            flagRect.top = flag.y - DensityUtil.dip2px(mContext, flagRadioDp) - DensityUtil.dip2px(mContext, enlargeMonitorDp);
            flagRect.right = flag.x + DensityUtil.dip2px(mContext, flagRadioDp) + DensityUtil.dip2px(mContext, enlargeMonitorDp);
            flagRect.bottom = flag.y + DensityUtil.dip2px(mContext, flagRadioDp) + DensityUtil.dip2px(mContext, enlargeMonitorDp);
            return flagRect;
        } else {
            return null;
        }
    }

    /**
     * 获取绘制的矩形列表
     *
     * @return
     */
    public List<Rect> getRect() {
        List<Rect> rects = new ArrayList<>();
        for (PlanCell planCell : planCells) {
            if (planCell != null && planCell.getRect() != null) {
                rects.add(planCell.getRect());
            }
        }
        return rects;
    }

    /**
     * 获取绘制矩形名称的列表
     *
     * @return
     */
    public List<String> getTexts() {
        List<String> texts = new ArrayList<>();
        for (PlanCell planCell : planCells) {
            if (planCell != null && planCell.getText() != null) {
                texts.add(planCell.getText());
            }
        }
        return texts;
    }

    /**
     * 获取绘制矩形名称的字符串
     *
     * @return
     */
    public String getSaveTexts() {
        JSONArray jsonArray = new JSONArray();
        List<String> allTexts = getTexts();
        if (allTexts != null && allTexts.size() > 0) {
            for (int i = 0; i < allTexts.size(); i++) {
                if (allTexts.get(i) != null) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("rectName", allTexts.get(i));
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        return jsonArray.toString();
    }

    /**
     * @return 所有场景中的测试点, 如果得到的测试点为null表示该场景还未设置测试点
     */
    public List<Point> getFlags() {
        List<Point> texts = new ArrayList<>();
        for (PlanCell planCell : planCells) {
            if (planCell != null) {
                texts.add(planCell.getTest());
            }
        }
        return texts;
    }

    /**
     * 判断该场景是否存在
     *
     * @param text 场景名称
     * @return true存在 false不存在
     */
    public boolean hasRectExist(String text) {
        if (text == null) {
            return false;
        }
        boolean exist = false;
        for (int i = 0; i < planCells.size(); i++) {
            if (text.equals(planCells.get(i).getText())) {
                exist = true;
            }
        }
        return exist;
    }

    /**
     * 删除场景中的所有东西，包括矩形、名称、测试点
     *
     * @param text 名称
     */
    public void deletePlanCell(String text) {
        if (text == null) {
            return;
        }
        if (state == STATE_DRAW_DELETE) {   // 必须先设置为删除模式
            int position = -1;
            for (int i = 0; i < planCells.size(); i++) {
                if (planCells.get(i) != null) {
                    if (text.equals(planCells.get(i).getText())) {
                        position = i;
                        break;
                    }
                }
            }
            if (position != -1) {
                planCells.remove(position);
                invalidate();
            }
        }
    }

    /**
     * 单独删除测试点
     *
     * @param point 测试点
     */
    public void deleteFlag(Point point) {
        if (point == null) {
            return;
        }
        List<Point> flags = new ArrayList<>();
        int position = -1;
        for (int i = 0; i < planCells.size(); i++) {
            Point flag = planCells.get(i).getTest();
            if (flag != null && flag.x == point.x && flag.y == point.y) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            planCells.get(position).setTest(null);
            invalidate();
        }
    }

    /**
     * 单独删除路由器
     *
     * @param point 路由器
     */
    public void deleteLuYou(Point point) {
        if (point == null) {
            return;
        }
        if (luyous.contains(point)) {
            luyous.remove(point);
        }
        invalidate();
    }

    /**
     * 指定的测试点或者路由器是否在绘制好的场景之内
     *
     * @param point
     * @return
     */
    public boolean isFlagOrLuyouInsideRect(Point point) {
        boolean inside = false;
        if (point != null) {
            for (int i = 0; i < planCells.size(); i++) {
                if (planCells.get(i) != null && planCells.get(i).getRect() != null) {
                    if (planCells.get(i).getRect().contains(point.x, point.y)) {
                        inside = true;
                        break;
                    }
                }
            }
        }
        return inside;
    }

    /**
     * 指定的测试点是否在指定的场景区域之内
     *
     * @param point
     * @param text
     * @return
     */
    public boolean isFlagInsideOfAppointRect(Point point, String text) {
        boolean inside = false;
        if (point != null && text != null) {
            int position = -1;
            for (int i = 0; i < planCells.size(); i++) {
                if (planCells.get(i) != null && planCells.get(i).getText() != null) {
                    if (text.equals(planCells.get(i).getText())) {
                        position = i;
                        break;
                    }
                }
            }
            if (position != -1) {
                if (planCells.get(position) != null && planCells.get(position).getRect() != null
                        && planCells.get(position).getRect().contains(point.x, point.y)) {
                    inside = true;
                }
            }
        }
        return inside;
    }

    /**
     * 获取整个绘制矩形区域的最左、上、右、下
     *
     * @return
     */
    public Rect getDrawArea(List<PlanCell> planCells) {
        if (planCells == null || planCells.size() == 0) {
            return null;
        }
        Rect rect = new Rect();
        int mostLeft = Integer.MAX_VALUE;
        int mostTop = Integer.MAX_VALUE;
        int mostRight = -1;
        int mostBotton = -1;
        for (int i = 0; i < planCells.size(); i++) {
            if (planCells.get(i) != null && planCells.get(i).getRect() != null) {
                if (planCells.get(i).getRect().left < mostLeft) {
                    mostLeft = planCells.get(i).getRect().left;
                }
                if (planCells.get(i).getRect().top < mostTop) {
                    mostTop = planCells.get(i).getRect().top;
                }
                if (planCells.get(i).getRect().right > mostRight) {
                    mostRight = planCells.get(i).getRect().right;
                }
                if (planCells.get(i).getRect().bottom > mostBotton) {
                    mostBotton = planCells.get(i).getRect().bottom;
                }
            }
        }

        if (mostLeft == Integer.MAX_VALUE || mostTop == Integer.MAX_VALUE || mostRight == -1 || mostBotton == -1) {
            return null;
        } else {
            rect.left = mostLeft;
            rect.top = mostTop;
            rect.right = mostRight;
            rect.bottom = mostBotton;
            return rect;
        }

    }

    public DrawView(Context context) {
        super(context);
        mContext = context;
        this.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(mContext, this);
        initPaint();
        initData();
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(mContext, this);
        initPaint();
        initData();
    }

    private void initPaint() {
        textPaint = new TextPaint();
        textPaint.setColor(textColor);

        gridPaint = new Paint();
        gridPaint.setAntiAlias(true);
        gridPaint.setColor(gridColor);
        gridPaint.setStrokeWidth(1);

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.FILL);

        flagPaint = new Paint();
        flagPaint.setAntiAlias(true);
        flagPaint.setColor(Color.GREEN);
        flagPaint.setStyle(Paint.Style.FILL);
    }

    private void initData() {
        lengthPx = DensityUtil.dip2px(mContext, lengthDp);
        totalWidth = column * lengthPx;
        totalHeight = row * lengthPx;
        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();
        // 绘制网格线
        drawGrid(canvas);
        // 绘制矩形图
        drawRect(canvas);
        // 绘制测试点
        drawFlag(canvas);
        // 绘制路由器
        drawLuyou(canvas);
        // 绘制放大器
        drawFangDaQi(canvas);

    }

    private void drawGrid(Canvas canvas) {
        drawRows(canvas, lengthPx);
        drawColumn(canvas, lengthPx);
    }

    private void drawRect(Canvas canvas) {
        for (int i = 0; i < planCells.size(); i++) {
            if (planCells.get(i) != null) {
                Rect rect = planCells.get(i).getRect();
                if (rect != null) {
                    if (planCells.get(i).getColor() != 0) {
                        rectPaint.setColor(planCells.get(i).getColor());
                    } else {
                        rectPaint.setColor(colors[i % colors.length]);
                        planCells.get(i).setColor(colors[i % colors.length]);
                    }
                    // 绘制矩形
                    canvas.drawRect(rect.left - startX, rect.top - startY, rect.right - startX, rect.bottom - startY, rectPaint);
                    // 绘制文字
                    String text = planCells.get(i).getText();
                    if (text != null && text.length() > 0) {
                        int textSize = Math.min((Math.abs(rect.bottom - rect.top)) / 2, (Math.abs(rect.right - rect.left)) / (text.length() * 2));
                        textSize = textSize > DensityUtil.sp2px(mContext, 20) ? DensityUtil.sp2px(mContext, 20) : textSize;
                        textPaint.setTextSize(textSize);
                        textPaint.setColor(textColor);
                        int x = ((rect.right + rect.left) / 2) - (text.length() * textSize / 2);
                        int y = (rect.bottom + rect.top) / 2 + (textSize / 2);
                        canvas.drawText(text, x - startX, y - startY, textPaint);
                    }
                }
            }
        }
    }

    private void drawFlag(Canvas canvas) {
        flagPaint.setColor(Color.GREEN);
        for (int i = 0; i < planCells.size(); i++) {
            Point point = planCells.get(i).getTest();
            if (point != null) {
                canvas.drawCircle(point.x - startX, point.y - startY, DensityUtil.dip2px(mContext, flagRadioDp), flagPaint);

            }
        }

    }

    private void drawFangDaQi(Canvas canvas) {
        flagPaint.setColor(Color.RED);
        for (int i = 0; i < fangdaqis.size(); i++) {
            Point point = fangdaqis.get(i);
            if (point != null) {
                canvas.drawCircle(point.x - startX, point.y - startY, DensityUtil.dip2px(mContext, fdqRadioDp), flagPaint);

            }
        }
    }

    private void drawLuyou(Canvas canvas) {
        float max = lengthPx / 1.5f;
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_router);
        float rateX = bitmap.getWidth() / max;
        float reteY = bitmap.getHeight() / max;
        float rate = Math.max(rateX, reteY);
        Matrix matrix = new Matrix();
        matrix.postScale(1 / rate, 1 / rate);
        Bitmap newBitmap = bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        lyqBitmapWidth = newBitmap.getWidth();
        lyqBitmapHeight = newBitmap.getHeight();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < luyous.size(); i++) {
            Point point = luyous.get(i);
            if (point != null) {
                // 路由器的实际点是绘制图形的底部中心
                canvas.drawBitmap(newBitmap, point.x - startX - newBitmap.getWidth() / 2, point.y - startY - newBitmap.getHeight(), paint);
            }
        }
    }

    private void drawRows(Canvas canvas, int length) {
        for (int i = 1; i <= row; i++) {
            if (i * length - startY > height) { // 如果该水平线的位置超过了屏幕则后续的都不绘制
                break;
            }
            if (i * length - startY < 0) {    // 如果该水平线的位置在屏幕上方则跳过
                continue;
            }
            canvas.drawLine(0, i * length - startY, width, i * length - startY, gridPaint);
        }
    }

    private void drawColumn(Canvas canvas, int length) {
        for (int i = 1; i <= row; i++) {
            if (i * length - startX > width) {
                break;
            }
            if (i * length - startX < 0) {
                continue;
            }
            canvas.drawLine(i * length - startX, 0, i * length - startX, height, gridPaint);
        }
    }

    /**
     * -------------------------- OnGestureListener -----------------
     **/
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        switch (state) {
            case STATE_NORMAL:  // 不可编辑状态
                break;
            case STATE_DRAW_RECT:   // 绘制矩形状态

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    rect = new Rect();
                    planCell = new PlanCell();
                    planCell.setRect(rect);
                    planCell.setText(text);
                    planCells.add(planCell);
                    rectStartX = (int) (startX + motionEvent.getX());
                    rectStartY = (int) (startY + motionEvent.getY());

                    if (mOnDrawRectEvent != null) {
                        mOnDrawRectEvent.drawRectEvent(DOMN, 0, false, rect);
                    }

                    // 自动对齐网格线
                    int leftCell = rectStartX / lengthPx;
                    int exceedX = rectStartX - leftCell * lengthPx;  // 不会小于零
                    if (exceedX > (lengthPx / 2)) {
                        rectStartX = (leftCell + 1) * lengthPx;
                    } else {
                        rectStartX = leftCell * lengthPx;
                    }
                    int topCell = rectStartY / lengthPx;
                    int exceedY = rectStartY - topCell * lengthPx;    // 不会小于零
                    if (exceedY > (lengthPx / 2)) {
                        rectStartY = (topCell + 1) * lengthPx;
                    } else {
                        rectStartY = topCell * lengthPx;
                    }

                    // 若新绘制矩形的起点在已有矩形之内则自动定位到最近边
                    for (int i = 0; i < planCells.size() - 1; i++) {
                        Rect drawRect = planCells.get(i).getRect();
                        if (drawRect != null) {
                            Point centerPoint = new Point();
                            centerPoint.x = (drawRect.left + drawRect.right) / 2;
                            centerPoint.y = (drawRect.top + drawRect.bottom) / 2;
                            if (rectStartX >= centerPoint.x && rectStartX < drawRect.right
                                    && rectStartY > drawRect.top && rectStartY <= centerPoint.y) { // 1象限
                                if (Math.abs(rectStartX - drawRect.right) >= Math.abs(rectStartY - drawRect.top)) {
                                    rectStartY = drawRect.top;
                                } else {
                                    rectStartX = drawRect.right;
                                }
                            } else if (rectStartX >= centerPoint.x && rectStartX < drawRect.right
                                    && rectStartY > centerPoint.y && rectStartY < drawRect.bottom) {  // 2象限
                                if (Math.abs(rectStartX - drawRect.right) > Math.abs(rectStartY - drawRect.bottom)) {
                                    rectStartY = drawRect.bottom;
                                } else {
                                    rectStartX = drawRect.right;
                                }
                            } else if (rectStartX < centerPoint.x && rectStartX > drawRect.left
                                    && rectStartY >= centerPoint.y && rectStartY < drawRect.bottom) { // 3象限
                                if (Math.abs(rectStartX - drawRect.left) > Math.abs(rectStartY - drawRect.bottom)) {
                                    rectStartY = drawRect.bottom;
                                } else {
                                    rectStartX = drawRect.left;
                                }
                            } else if (rectStartX < centerPoint.x && rectStartX > drawRect.left
                                    && rectStartY > drawRect.top && rectStartY < centerPoint.y) {  // 4象限
                                if (Math.abs(rectStartX - drawRect.left) > Math.abs(rectStartY - drawRect.top)) {
                                    rectStartY = drawRect.top;
                                } else {
                                    rectStartX = drawRect.left;
                                }
                            }
                        }
                    }
                    // 如果超过了屏幕边缘则自动定位到边缘
                    rectStartX = ((rectStartX < 0) ? 0 : rectStartX);
                    rectStartX = ((rectStartX > (startX + width))) ? (startX + width) : rectStartX;
                    rectStartY = ((rectStartY < 0) ? 0 : rectStartY);
                    rectStartY = ((rectStartY > (startY + height))) ? (startY + height) : rectStartY;

                }
                break;
            case STATE_DRAW_FLAG:
                flag = new Point();
                flag.x = (int) (startX + motionEvent.getX());
                flag.y = (int) (startY + motionEvent.getY());
                flagStartX = flag.x;
                flagStartY = flag.y;
                if (addFlagRectName != null && !"".equals(addFlagRectName)) {
                    for (int i = 0; i < planCells.size(); i++) {
                        if (planCells.get(i) != null && planCells.get(i).getText() != null) {
                            if (addFlagRectName.equals(planCells.get(i).getText())) {
                                planCells.get(i).setTest(flag);
                                invalidate();
                                break;
                            }
                        }
                    }
                }
                break;
            case STATE_DRAW_LUYOU:
                luyou = new Point();
                luyou.x = (int) (startX + motionEvent.getX());
                luyou.y = (int) (startY + motionEvent.getY());
                luyouStartX = luyou.x;
                luyouStartY = luyou.y;
                luyous.add(luyou);
                invalidate();
                break;
        }
        return true;
    }


    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        if (!enable) {
            return false;
        }
        int x = (int) (startX + motionEvent.getX());
        int y = (int) (startY + motionEvent.getY());

        Rect rect = null;
        String text = null;
        Point luyouqi = null;
        Point flag = null;
        Point click = new Point(x, y);
        // 获取点击的矩形图
        for (int i = planCells.size() - 1; i >= 0; i--) {
            if (planCells.get(i) != null) {
                if (planCells.get(i).getRect() != null && planCells.get(i).getRect().contains(x, y)) {
                    rect = planCells.get(i).getRect();
                    text = planCells.get(i).getText();
                    break;
                }
            }
        }
        // 获取点击的路由器
        for (int i = luyous.size() - 1; i >= 0; i--) {
            if (luyous.get(i) != null) {
                if (getLuYouqiRect(luyous.get(i)).contains(x, y)) {
                    luyouqi = luyous.get(i);
                    break;
                }
            }
        }
        // 获取点击的测试点
        for (int i = planCells.size() - 1; i >= 0; i--) {
            if (planCells.get(i) != null) {
                if (planCells.get(i).getTest() != null && getFlagRect(planCells.get(i).getTest()).contains(x, y)) {
                    flag = planCells.get(i).getTest();
                    break;
                }
            }
        }
        if (onClickListener != null) {
            onClickListener.onClick(state, rect, text, luyouqi, flag, click);
        }

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float distanceY) {
        // distanceX向左滑为正值，向右滑为负值；distanceY向上滑为正值，向下滑为负值；
        if (state == STATE_NORMAL && canScrool) {  // 不可编辑状态
            if (totalWidth > width) {   // 计算绘制表格的x起点
                startX += distanceX;
                startX = startX < 0 ? 0 : startX;
                startX = totalWidth < width ? 0 : (startX < 0 ? 0 : startX);
                startX = ((startX + width) > totalWidth && startX > 0) ? (totalWidth - width) : startX;
            }
            if (totalHeight > height) { // 计算绘制表格的y起点
                startY += distanceY;
                startY = startY < 0 ? 0 : startY;
                startY = totalHeight < height ? 0 : (startY < 0 ? 0 : startY);
                startY = ((startY + height) > totalHeight && startY > 0) ? (totalHeight - height) : startY;
            }
            if (totalWidth > width || totalHeight > height) {   // 未到边界重新绘制
                invalidate();
            }
            return true;
        } else if (state == STATE_DRAW_RECT) { // 绘制矩形状态
            if (rect != null) {
//                if (isDrawBig(rect)) {
//                    if(mOnDrawRectEvent != null) {
//                        if(getCellCount(rect) == area[0] || getCellCount(rect)==area[1]) {
//                            mOnDrawRectEvent.drawRectEvent(SCROLL,getCellCount(rect),true);
//                        }else {
//                            mOnDrawRectEvent.drawRectEvent(SCROLL,getCellCount(rect),false);
//                        }
//                    }
//                    invalidate();
//                    return true;
//                }
                // x、y单方向上面滑动的距离
                scrollX += distanceX;
                scrollY += distanceY;
                // 取整计算矩形对角线上另一个顶点的坐标
                rectEndX = (int) (rectStartX - scrollX);
                rectEndY = (int) (rectStartY - scrollY);

                // 自动对齐网格线
                int leftCell = rectEndX / lengthPx;
                int exceedX = rectEndX - leftCell * lengthPx;  // 不会小于零
                if (exceedX > (lengthPx / 2)) {
                    rectEndX = (leftCell + 1) * lengthPx;
                } else {
                    rectEndX = leftCell * lengthPx;
                }
                int topCell = rectEndY / lengthPx;
                int exceedY = rectEndY - topCell * lengthPx;    // 不会小于零
                if (exceedY > (lengthPx / 2)) {
                    rectEndY = (topCell + 1) * lengthPx;
                } else {
                    rectEndY = topCell * lengthPx;
                }

                if (rectStartX > rectEndX) {
                    rectEndX = (rectEndX < 0) ? 0 : rectEndX;
                    rect.left = rectEndX;
                    rect.right = rectStartX;
                } else {
                    rectEndX = (rectEndX > width + startX) ? width + startX : rectEndX;
                    rect.left = rectStartX;
                    rect.right = rectEndX;
                }
                if (rectStartY > rectEndY) {
                    rectEndY = (rectEndY < 0) ? 0 : rectEndY;
                    rect.top = rectEndY;
                    rect.bottom = rectStartY;
                } else {
                    rectEndY = (rectEndY > height + startY) ? height + startY : rectEndY;
                    rect.top = rectStartY;
                    rect.bottom = rectEndY;
                }


                if (isDrawBig(rect)) {
                    if (getCellCount(rect) >= area[0] && getCellCount(rect) <= area[1]) {
                        if (mOnDrawRectEvent != null) {
                            mOnDrawRectEvent.drawRectEvent(SCROLL, getCellCount(rect), true, rect);
                        }
                        lastRect.left = rect.left;
                        lastRect.top = rect.top;
                        lastRect.right = rect.right;
                        lastRect.bottom = rect.bottom;
                        invalidate();
                    } else {
                        rect.left = lastRect.left;
                        rect.top = lastRect.top;
                        rect.right = lastRect.right;
                        rect.bottom = lastRect.bottom;
                        if (getCellCount(rect) >= area[0] && getCellCount(rect) <= area[1]) {
                            if (mOnDrawRectEvent != null) {
                                mOnDrawRectEvent.drawRectEvent(SCROLL, getCellCount(rect), true, rect);
                            }
                        } else {
                            if (mOnDrawRectEvent != null) {
                                mOnDrawRectEvent.drawRectEvent(SCROLL, getCellCount(rect), false, rect);
                            }
                        }

                        /**
                         * 当计算得到的面积大于指定面积时，则将指定面积之后的滑动量不计算在总的滑动量之内
                         * 否则当想把面积画小时反向反动手指，会出现要反向滑动很多距离之后图像才会更新画小
                         */
                        scrollX -= distanceX;
                        scrollY -= distanceY;

                    }
                } else {
                    if (mOnDrawRectEvent != null) {
                        if (area[1] <= 0 || area[0] >= getAllCellCount()) {  // 指定的绘制面积不合理
                            mOnDrawRectEvent.drawRectEvent(SCROLL, getCellCount(rect), true, rect);
                        } else {
                            if (getCellCount(rect) >= area[0] && getCellCount(rect) <= area[1]) {
                                // 当绘制面积小于较大指定面积但刚好等于较小指定面积时绘制也是属于合格的
                                mOnDrawRectEvent.drawRectEvent(SCROLL, getCellCount(rect), true, rect);
                            } else {
                                mOnDrawRectEvent.drawRectEvent(SCROLL, getCellCount(rect), false, rect);
                            }
                        }
                    }
                    lastRect.left = rect.left;
                    lastRect.top = rect.top;
                    lastRect.right = rect.right;
                    lastRect.bottom = rect.bottom;
                    invalidate();
                }


//                if(mOnDrawRectEvent != null) {
//                    mOnDrawRectEvent.drawRectEvent(SCROLL,getCellCount(rect),false);
//                }

//                invalidate();

                return true;
            }
            return false;
        } else if (state == STATE_DRAW_FLAG) {
            // x、y单方向上面滑动的距离
            scrollX += distanceX;
            scrollY += distanceY;
            flag.x = (int) (flagStartX - scrollX);
            flag.y = (int) (flagStartY - scrollY);
            if (flag.x < 0) {
                flag.x = 0;
            }
            if (flag.x > startX + width) {
                flag.x = startX + width;
            }
            if (flag.y < 0) {
                flag.y = 0;
            }
            if (flag.y > startX + height) {
                flag.y = startX + height;
            }
            invalidate();
            return true;
        } else if (state == STATE_DRAW_LUYOU) {
            // x、y单方向上面滑动的距离
            scrollX += distanceX;
            scrollY += distanceY;
            luyou.x = (int) (luyouStartX - scrollX);
            luyou.y = (int) (luyouStartY - scrollY);
            if (luyou.x < 0) {
                luyou.x = 0;
            }
            if (luyou.x > startX + width) {
                luyou.x = startX + width;
            }
            if (luyou.y < 0) {
                luyou.y = 0;
            }
            if (luyou.y > startX + height) {
                luyou.y = startX + height;
            }
            invalidate();
            return true;
        }

        return false;
    }

    /**
     * 绘制的矩形大小是否超过了指定的面积,当指定面积有两个时这里匹配的是较大的那个
     * 当指定的面积小于等于零或超过了可绘制的最大面积时返回false
     *
     * @param rect
     * @return
     */
    private boolean isDrawBig(Rect rect) {
        if (rect != null) {
            if (area[1] <= getAllCellCount()) {    // 都在最大绘制面积之内
                if (area[1] > 0 && area[1] <= getCellCount(rect)) {
                    return true;
                }
            } else if (area[0] <= getAllCellCount() && area[1] > getAllCellCount()) {    // 只有一个在最大绘制面积之内
                if (area[0] > 0 && area[0] <= getCellCount(rect)) {
                    return true;
                }
            } else if (area[0] > getAllCellCount()) {  // 都不在最大绘制面积之内
                return false;
            }
        }
        return false;
    }

    /**
     * 绘制的矩形是否不合格
     *
     * @param rect
     * @return
     */
    private boolean isDrawUnqualified(Rect rect) {
        if (area[0] == 0 || area[1] == 0) {
            return false;
        }
        if (rect != null) {
            if (area[1] <= getAllCellCount()) {    // 都在最大绘制面积之内
                if (area[0] > getCellCount(rect) || area[1] < getCellCount(rect)) {
                    return true;
                }
            } else if (area[0] <= getAllCellCount() && area[1] > getAllCellCount()) {    // 只有一个在最大绘制面积之内
                if (area[0] > getCellCount(rect)) {
                    return true;
                }
            } else if (area[0] > getAllCellCount()) {  // 都不在最大绘制面积之内
                return false;
            }
        }
        return false;
    }

    private int getCellCount(Rect rect) {
        int count = 0;
        if (rect != null) {
            count = ((rect.right - rect.left) / lengthPx) * ((rect.bottom - rect.top) / lengthPx);
        }
        return count;
    }

    private int getAllCellCount() {
        if (canScrool) {
            return row * column;
        } else {
            return (width / lengthPx) * (height / lengthPx);
        }
    }


    int dragRectWidth = 0;  // 当前拖动矩形的宽
    int dragRectHeight = 0; // 当前拖动举行的高
    int toLeft = 0; // 当前可拖动矩形左侧距离长按手指落点距离
    int toTop = 0;  // 当前可拖动矩形上侧距离长按手指落点距离

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        int x = (int) (startX + motionEvent.getX());
        int y = (int) (startY + motionEvent.getY());

        Rect rect = null;
        String text = null;
        Point luyouqi = null;
        Point flag = null;
        Point click = new Point(x, y);
        // 获取点击的矩形图
        for (int i = planCells.size() - 1; i >= 0; i--) {
            if (planCells.get(i) != null) {
                if (planCells.get(i).getRect() != null && planCells.get(i).getRect().contains(x, y)) {
                    rect = planCells.get(i).getRect();
                    text = planCells.get(i).getText();
                    break;
                }
            }
        }
        // 获取点击的路由器
        for (int i = luyous.size() - 1; i >= 0; i--) {
            if (luyous.get(i) != null) {
                if (getLuYouqiRect(luyous.get(i)).contains(x, y)) {
                    luyouqi = luyous.get(i);
                    break;
                }
            }
        }
        // 获取点击的测试点
        for (int i = planCells.size() - 1; i >= 0; i--) {
            if (planCells.get(i) != null) {
                if (planCells.get(i).getTest() != null && getFlagRect(planCells.get(i).getTest()).contains(x, y)) {
                    flag = planCells.get(i).getTest();
                    break;
                }
            }
        }
        if (onClickListener != null) {
            onClickListener.onLongClick(state, rect, text, luyouqi, flag, click);
        }
        if (longPressMoveRect && rect != null) {
            vibrator.vibrate(100);
            state = STATE_MOVE;
            setMoveRect(rect);
            if (moveRect != null) {
                // 计算出可拖动矩形的宽高
                dragRectWidth = moveRect.right - moveRect.left;
                dragRectHeight = moveRect.bottom - moveRect.top;
                // 手指长按落点
                dragX1 = (int) motionEvent.getX();
                dragY1 = (int) motionEvent.getY();
                // 矩形左侧距离手指长按落点的距离
                toLeft = moveRect.left - dragX1;
                // 矩形上册距离手指长按落点的距离
                toTop = moveRect.top - dragY1;
            }
        }
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    /**
     * -------------------------- OnTouchListener -----------------
     **/

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!enable) {
            return false;
        }
        return mGestureDetector.onTouchEvent(motionEvent);
    }

    private boolean toastEnable = true;

    public boolean isToastEnable() {
        return toastEnable;
    }

    public void setToastEnable(boolean toastEnable) {
        this.toastEnable = toastEnable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!enable) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {   // 每次滑动结束之后将绘制矩形的数据重置
            if (state == STATE_DRAW_RECT) {
                PlanCell lastPlanCell = planCells.get(planCells.size() - 1);

                if (lastPlanCell.getRect() != null) {
                    if (isDrawUnqualified(lastPlanCell.getRect())) { // 绘制的矩形面积不合格
                        if (toastEnable) {
                            Toast.makeText(mContext, "场景面积绘制不正确，请重新进行绘制！", Toast.LENGTH_SHORT).show();
                        }

                        if (mOnDrawRectEvent != null) {
                            mOnDrawRectEvent.drawRectEvent(UP, getCellCount(lastPlanCell.getRect()), false, lastPlanCell.getRect());
                        }

                        planCells.remove(lastPlanCell);

                        invalidate();
                    } else {    // 不需要限制图形绘制大小
                        if (lastPlanCell.getRect() == null ||
                                lastPlanCell.getRect().left == lastPlanCell.getRect().right ||
                                lastPlanCell.getRect().top == lastPlanCell.getRect().bottom) {   // 没有成功绘制矩形
                            if (mOnDrawRectEvent != null) {
                                mOnDrawRectEvent.drawRectEvent(UP, getCellCount(lastPlanCell.getRect()), false, lastPlanCell.getRect());
                            }
                            planCells.remove(lastPlanCell);
                        } else {
                            if (onDrawCompleteListener != null) {
                                onDrawCompleteListener.onDrawComplete(state, rect, text, null, null);
                            }
                            if (mOnDrawRectEvent != null) {
                                mOnDrawRectEvent.drawRectEvent(UP, getCellCount(lastPlanCell.getRect()), true, lastPlanCell.getRect());
                            }
                        }
                    }
                }

                rectStartX = 0;
                rectEndX = 0;
                rectStartY = 0;
                rectEndY = 0;
                scrollX = 0;
                scrollY = 0;
            } else if (state == STATE_DRAW_FLAG) {
                scrollX = 0;
                scrollY = 0;
                flagStartX = 0;
                flagStartY = 0;
                if (onDrawCompleteListener != null) {
                    onDrawCompleteListener.onDrawComplete(state, null, null, flag, null);
                }
            } else if (state == STATE_DRAW_LUYOU) {
                scrollX = 0;
                scrollY = 0;
                luyouStartX = 0;
                luyouStartY = 0;
                if (onDrawCompleteListener != null) {
                    onDrawCompleteListener.onDrawComplete(state, null, null, null, luyou);
                }
            } else if (state == STATE_MOVE) {   // 矩形拖动结束之后将状态和可拖动矩形重置
                state = STATE_NORMAL;
                moveRect = null;
            }

            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (state == STATE_MOVE) {
                if (moveRect != null) {
                    // 拖动过程中刷新矩形左侧和上侧位置
                    moveRect.left = (int) (startX + event.getX() + toLeft);
                    moveRect.top = (int) (+startY + event.getY() + toTop);
                    // 自动对齐网格线
                    int leftCell = moveRect.left / lengthPx;
                    int exceedX = moveRect.left - leftCell * lengthPx;  // 不会小于零
                    if (exceedX > (lengthPx / 2)) {
                        moveRect.left = (leftCell + 1) * lengthPx;
                    } else {
                        moveRect.left = leftCell * lengthPx;
                    }
                    int topCell = moveRect.top / lengthPx;
                    int exceedY = moveRect.top - topCell * lengthPx;    // 不会小于零
                    if (exceedY > (lengthPx / 2)) {
                        moveRect.top = (topCell + 1) * lengthPx;
                    } else {
                        moveRect.top = topCell * lengthPx;
                    }
                    //  根据左侧和上侧位置刷新右侧和下侧位置
                    moveRect.right = moveRect.left + dragRectWidth;
                    moveRect.bottom = moveRect.top + dragRectHeight;
                    // 调整矩形显示的位置
                    if (moveRect.right > startX + width) {
                        moveRect.right = startX + width;
                        moveRect.left = moveRect.right - dragRectWidth;
                    }
                    if (moveRect.left < startX) {
                        moveRect.left = startX;
                        moveRect.right = moveRect.left + dragRectWidth;
                    }
                    if (moveRect.bottom > startY + height) {
                        moveRect.bottom = startY + height;
                        moveRect.top = moveRect.bottom - dragRectHeight;
                    }
                    if (moveRect.top < startY) {
                        moveRect.top = startY;
                        moveRect.bottom = moveRect.top + dragRectHeight;
                    }

                    invalidate();
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }


    private OnDrawCompleteListener onDrawCompleteListener;

    /**
     * 每次绘制完成的监听
     */
    public interface OnDrawCompleteListener {
        /**
         * @param state 状态
         * @param rect  场景图
         * @param text  场景名称
         * @param flag  测试点
         * @param luyou 路由器
         */
        void onDrawComplete(@State int state, Rect rect, String text, Point flag, Point luyou);
    }

    public void setOnDrawCompleteListener(OnDrawCompleteListener onDrawCompleteListener) {
        this.onDrawCompleteListener = onDrawCompleteListener;
    }

    private OnRectClickListener onRectClickListener;

    /**
     * 点击场景图的监听
     */
    public interface OnRectClickListener {
        /**
         * @param state 点击场景时控件处于的状态
         * @param rect  点击的场景图
         * @param text  点击的场景名称
         */
        void onLongClick(@State int state, Rect rect, String text);

        void onClick(@State int state, Rect rect, String text);
    }

    public void setOnRectClickListener(OnRectClickListener onRectClickListener) {
        this.onRectClickListener = onRectClickListener;
    }

    private OnClickListener onClickListener;

    /**
     * 整个控件点击任何一处的监听
     */
    public interface OnClickListener {
        /**
         * @param state   点击控件时控件处于的状态
         * @param rect    点击到的场景图，没有则为null，有多个则为最上面的那一个
         * @param text    点击到的场景名称，没有则为null,有多个则为最上面的哪一个
         * @param luyouqi 点击到的路由器，没有则为null,有多个则为最上面的那一个
         * @param flag    点击到的测试点,没有则为null,有多个则为最上面的那一个
         * @param click   点击处相对于总的尺寸的坐标
         */
        void onLongClick(@State int state, Rect rect, String text, Point luyouqi, Point flag, Point click);

        void onClick(@State int state, Rect rect, String text, Point luyouqi, Point flag, Point click);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private OnDrawRectEvent mOnDrawRectEvent;

    public void setOnDrawRectEvent(OnDrawRectEvent onDrawRectEvent) {
        this.mOnDrawRectEvent = onDrawRectEvent;
    }

    public interface OnDrawRectEvent {
        /**
         * @param event       当前绘制动作，1按下、2滑动、3抬起
         * @param area        当前绘制面积，也即当前绘制矩形所占单元格的个数
         * @param isQualified 绘制面积是否合格，若不需要限制绘制面积则恒为true,否则根据绘制面积与指定面积是否匹配来判断
         */
        void drawRectEvent(int event, int area, boolean isQualified, Rect rect);
    }

}
