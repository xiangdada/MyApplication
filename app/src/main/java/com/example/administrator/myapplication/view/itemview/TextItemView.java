package com.example.administrator.myapplication.view.itemview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.util.DensityUtil;


/**
 * Created byxiangpengfei on 2018/3/16.
 */

public class TextItemView extends LinearLayout {
    private Context mContext;
    private LinearLayout ll_content;
    private LinearLayout ll_title;
    private TextView tv_identification;
    private TextView tv_title;
    private TextView tv_value;
    private ImageView iv_right;
    private View line;

    private static final float paddingLeftDefault = 10;
    private static final float paddingTopDefault = 10;
    private static final float paddingRightDefault = 10;
    private static final float paddingBottomDefault = 10;
    private static final int identificationColorDefault = Color.RED;
    private static final float leftPartWidthDefault = 100;
    private static final int titleColorDefault = Color.BLACK;
    private static final int valueColorDefault = Color.BLUE;
    private static final float insideMarginDefault = 10;
    private static final float titleSizeDefault = 15;
    private static final float valueSizeDefault = 15;
    private static final int divideColorDefault = Color.GRAY;
    private static final float divideWidthDefault = 1;

    private float paddingLeft;
    private float paddingTop;
    private float paddingRight;
    private float paddingBottom;
    private String title;
    private String value;
    private int identificationColor;
    private String identification;
    private float leftPartWidth;
    private int titleColor;
    private int valueColor;
    private int titleGravity;
    private int valueGravity;
    private float insideMargin;
    private String hint;
    private float titleSize;
    private float valueSize;
    private int drawableId;
    private boolean divide;
    private int divideColor;
    private float divideWidth;

    public TextItemView(Context context) {
        this(context, null);
    }

    public TextItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.TextItemView);
        paddingLeft = ta.getDimension(R.styleable.TextItemView_paddingLeft, paddingLeftDefault);
        paddingTop = ta.getDimension(R.styleable.TextItemView_paddingTop, paddingTopDefault);
        paddingRight = ta.getDimension(R.styleable.TextItemView_paddingRight, paddingRightDefault);
        paddingBottom = ta.getDimension(R.styleable.TextItemView_paddingBottom, paddingBottomDefault);
        title = ta.getString(R.styleable.TextItemView_title);
        title = title == null ? "" : title;
        value = ta.getString(R.styleable.TextItemView_value);
        value = value == null ? "" : value;
        identificationColor = ta.getColor(R.styleable.TextItemView_identificationColor, identificationColorDefault);
        identification = ta.getString(R.styleable.TextItemView_identification);
        identification = identification == null ? "" : identification;
        leftPartWidth = ta.getDimension(R.styleable.TextItemView_leftPartWidth, leftPartWidthDefault);
        titleColor = ta.getColor(R.styleable.TextItemView_titleColor, titleColorDefault);
        valueColor = ta.getColor(R.styleable.TextItemView_valueColor, valueColorDefault);
        titleGravity = ta.getInt(R.styleable.TextItemView_titleGravity, -1);
        valueGravity = ta.getInt(R.styleable.TextItemView_valueGravity, -1);
        insideMargin = ta.getDimension(R.styleable.TextItemView_insideMargin, insideMarginDefault);
        hint = ta.getString(R.styleable.TextItemView_hint);
        hint = hint == null ? "" : hint;
        titleSize = ta.getDimension(R.styleable.TextItemView_titleSize, titleSizeDefault);
        valueSize = ta.getDimension(R.styleable.TextItemView_valueSize, valueSizeDefault);
        drawableId = ta.getResourceId(R.styleable.TextItemView_drawableId, 0);
        divide = ta.getBoolean(R.styleable.TextItemView_divide, true);
        divideColor = ta.getColor(R.styleable.TextItemView_divideColor, divideColorDefault);
        divideWidth = ta.getDimension(R.styleable.TextItemView_divideWidth, divideWidthDefault);

        ta.recycle();

        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        Log.e("测试", "childCount = " + childCount);
        Log.e("测试", "(" + getPaddingLeft() + "," + getPaddingTop() + "," + getPaddingRight() + "," + getPaddingBottom() + ")");
        int vertical = 0;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            view.layout(getPaddingLeft(),
                    getPaddingTop() + vertical,
                    getPaddingLeft() + view.getMeasuredWidth(),
                    getPaddingTop() + vertical + view.getMeasuredHeight());
            vertical += view.getMeasuredHeight();
        }
    }

    private void initView() {
        setOrientation(VERTICAL);
        Log.e("测试","titleGravity = " + titleGravity);

        ll_content = new LinearLayout(mContext);
        ll_content.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll_content.setOrientation(HORIZONTAL);
        ll_content.setPadding(DensityUtil.dip2px(mContext, paddingLeft),
                DensityUtil.dip2px(mContext, paddingTop),
                DensityUtil.dip2px(mContext, paddingRight),
                DensityUtil.dip2px(mContext, paddingBottom));

        ll_title = new LinearLayout(mContext);
        ll_title.setOrientation(HORIZONTAL);
        ll_title.setLayoutParams(new LayoutParams(DensityUtil.dip2px(mContext, leftPartWidth), LayoutParams.WRAP_CONTENT));
        ll_content.addView(ll_title);

        tv_identification = new TextView(mContext);
        tv_identification.setTextColor(identificationColor);
        tv_identification.setText(identification);
        tv_title = new TextView(mContext);
        tv_title.setText(title);
        tv_title.setTextSize(titleSize);
        tv_title.setTextColor(titleColor);
        tv_title.setGravity(titleGravity);
        LinearLayout.LayoutParams lp_title = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_title.rightMargin = DensityUtil.dip2px(mContext, insideMargin);
        tv_title.setLayoutParams(lp_title);
        ll_title.addView(tv_identification);
        ll_title.addView(tv_title);

        tv_value = new TextView(mContext);
        tv_value.setText(value);
        tv_value.setTextSize(valueSize);
        tv_value.setTextColor(valueColor);
        tv_value.setHint(hint);
        tv_value.setGravity(valueGravity);
        tv_value.setWidth(0);
        LinearLayout.LayoutParams lp_value = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_value.weight = 1;
        tv_value.setLayoutParams(lp_value);

        ll_content.addView(tv_value);

        iv_right = new ImageView(mContext);
        LinearLayout.LayoutParams lp_right = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_right.gravity = Gravity.CENTER;
        iv_right.setLayoutParams(lp_right);
        if (drawableId != 0) {
            iv_right.setImageResource(drawableId);
        }

        ll_content.addView(iv_right);

        addView(ll_content);

        line = new View(mContext);
        line.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) divideWidth));
        line.setBackgroundColor(divideColor);
        if (divide) {
            addView(line);
        }
    }
}
