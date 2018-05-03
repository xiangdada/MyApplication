package com.example.administrator.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication.view.drawview.DrawView;

import java.util.List;

/**
 * Created by xiangpengfei on 2018/2/24.
 */

public class DrawViewActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawView drawview;
    private FloatingActionButton fab;
    private LinearLayout ll_action;
    private TextView tv_ceshidian;
    private TextView tv_luyouqi;
    private TextView tv_changjing;
    private TextView tv_delete;
    private EditText et_changjing;
    private TextView tv_title;
    private TextView tv_cancel;
    private TextView tv_confirm;
    private Dialog dialogChangJing;
    private Rect clickRect;
    private String clickText;
    private Point clickLuYouQi;
    private Point clickFlag;
    private String scene = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawview);
        drawview = (DrawView) findViewById(R.id.drawview);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        ll_action = (LinearLayout) findViewById(R.id.ll_action);
        tv_ceshidian = (TextView) findViewById(R.id.tv_ceshidian);
        tv_luyouqi = (TextView) findViewById(R.id.tv_luyouqi);
        tv_changjing = (TextView) findViewById(R.id.tv_changjing);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        addListener();
        displaySceneDialog();
    }

    private void addListener() {
        fab.setOnClickListener(this);
        tv_ceshidian.setOnClickListener(this);
        tv_luyouqi.setOnClickListener(this);
        tv_changjing.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
        drawview.setOnClickListener(new DrawView.OnClickListener() {
            @Override
            public void onLongClick(@DrawView.State int state, Rect rect, String text, Point luyouqi, Point flag, Point click) {

            }

            @Override
            public void onClick(@DrawView.State int state, Rect rect, String text, Point luyouqi, Point flag, Point click) {
                clickRect = rect;
                clickText = text;
                clickLuYouQi = luyouqi;
                clickFlag = flag;
                if (state == DrawView.STATE_DRAW_DELETE) {
                    if (flag != null) {
                        displayDeleteDialog("确定要删除测试点吗？", "提示", "flag");
                        return;
                    }
                    if (luyouqi != null) {
                        displayDeleteDialog("确定要删除路由器吗？", "提示", "luyouqi");
                        return;
                    }
                    if (rect != null && text != null) {
                        displayDeleteDialog("确定要删除场景" + text + "吗？若场景中有路由器也将一并删除", "提示", "rect");
                        return;
                    }
                }
            }
        });

        drawview.setOnDrawCompleteListener(new DrawView.OnDrawCompleteListener() {
            @Override
            public void onDrawComplete(@DrawView.State int state, Rect rect, String text, Point flag, Point luyou) {
                if (state == DrawView.STATE_DRAW_RECT) {
                    // 绘制完成一个后设置不可绘制，必须再次开启绘制模式才能再绘制
                    drawview.setState(DrawView.STATE_NORMAL);
                } else if (state == DrawView.STATE_DRAW_LUYOU) {
                    if (drawview.isFlagOrLuyouInsideRect(luyou)) {
                        drawview.setState(DrawView.STATE_NORMAL);
                    } else {
                        Toast.makeText(DrawViewActivity.this, "路由器的位置不在场景内，请重新进行设置", Toast.LENGTH_SHORT).show();
                        drawview.deleteLuYou(luyou);
                    }
                } else if (state == DrawView.STATE_DRAW_FLAG) {
                    if (!drawview.isFlagInsideOfAppointRect(flag, scene)) {    // 测试点不在指定场景内
                        Toast.makeText(DrawViewActivity.this, "测试点的位置不在场景内，请重新进行设置", Toast.LENGTH_SHORT).show();
                        drawview.deleteFlag(flag);
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fab:
                if (ll_action.getVisibility() == View.GONE) {
                    show(300);
                } else {
                    hide(300);
                }
                break;
            case R.id.tv_ceshidian:
                if (drawview.getRect() == null || drawview.getRect().size() == 0) {
                    Toast.makeText(DrawViewActivity.this, "没有可测试的场景", Toast.LENGTH_SHORT).show();
                } else {
                    drawview.setState(DrawView.STATE_DRAW_DELETE);  // 删除
                    Toast.makeText(DrawViewActivity.this, "请点击场景图进行删除", Toast.LENGTH_SHORT).show();
                    hide(300);
                }
                break;
            case R.id.tv_luyouqi:
                drawview.setState(DrawView.STATE_NORMAL);
                if (drawview.getRect() == null || drawview.getRect().size() == 0) {
                    Toast.makeText(DrawViewActivity.this, "请先绘制测试场景", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (drawview.getLuyous() != null && drawview.getLuyous().size() > 0) {
                    Toast.makeText(DrawViewActivity.this, "路由器已经存在", Toast.LENGTH_SHORT).show();
                } else {
                    drawview.setState(DrawView.STATE_DRAW_LUYOU);
                    Toast.makeText(DrawViewActivity.this, "点击场景区域设置路由器位置", Toast.LENGTH_SHORT).show();
                    hide(300);
                }
                break;
            case R.id.tv_changjing:
                dialogChangJing.show();
                hide(300);
                break;
            case R.id.tv_delete:
                if (drawview.getRect() == null || drawview.getRect().size() == 0) {
                    Toast.makeText(DrawViewActivity.this, "没有可擦除的场景", Toast.LENGTH_SHORT).show();
                } else {
                    drawview.setState(DrawView.STATE_DRAW_DELETE);  // 删除
                    Toast.makeText(DrawViewActivity.this, "请点击场景图进行删除", Toast.LENGTH_SHORT).show();
                    hide(300);
                }
                break;
            case R.id.tv_cancel:
                dialogChangJing.dismiss();
                break;
            case R.id.tv_confirm:
                if ("".equals(et_changjing.getText().toString().trim())) {
                    Toast.makeText(DrawViewActivity.this, "请输入场景名称", Toast.LENGTH_SHORT).show();
                } else if (drawview.hasRectExist(et_changjing.getText().toString().trim())) {
                    Toast.makeText(DrawViewActivity.this, "该场景已经绘制，删除后可重新绘制", Toast.LENGTH_SHORT).show();
                } else {
                    drawview.addRect(et_changjing.getText().toString().trim());
                    drawview.setState(DrawView.STATE_DRAW_RECT);
                    dialogChangJing.dismiss();
                    hide(300);
                }
                break;
        }
    }


    private void show(long duration) {
        ll_action.setVisibility(View.VISIBLE);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
        scaleAnimation.setDuration(duration);
        ll_action.startAnimation(scaleAnimation);
        RotateAnimation rotateAnimation = new RotateAnimation(0.0f, 45.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(duration);
        rotateAnimation.setFillAfter(true);
        fab.startAnimation(rotateAnimation);

    }

    private void hide(long duration) {
        ll_action.setVisibility(View.GONE);
        RotateAnimation rotateAnimation = new RotateAnimation(45.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(duration);
        rotateAnimation.setFillAfter(true);
        fab.startAnimation(rotateAnimation);

    }

    private void displaySceneDialog() {
        View changjing = LayoutInflater.from(this).inflate(R.layout.dialog_changjing, null);
        et_changjing = (EditText) changjing.findViewById(R.id.et_changjing);
        tv_title = (TextView) changjing.findViewById(R.id.tv_title);
        tv_cancel = (TextView) changjing.findViewById(R.id.tv_cancel);
        tv_confirm = (TextView) changjing.findViewById(R.id.tv_confirm);
        tv_title.setText("场景");
        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
        dialogChangJing = new Dialog(this);
        dialogChangJing.setContentView(changjing);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.8);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogChangJing.getWindow().setAttributes(lp);
    }


    private void displayDeleteDialog(String message, String title, final String tag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("flag".equals(tag)) {   // 删除测试点
                    drawview.deleteFlag(clickFlag);
                } else if ("luyouqi".equals(tag)) { // 删除路由器
                    drawview.deleteLuYou(clickLuYouQi);
                } else if ("rect".equals(tag)) {  // 删除场景图
                    // 删除场景内的路由器
                    if (drawview.getLuyous() != null && drawview.getLuyous().size() > 0) {
                        for (int i = 0; i < drawview.getLuyous().size(); i++) {
                            if (clickRect.contains(drawview.getLuyous().get(i).x, drawview.getLuyous().get(i).y)) {
                                drawview.deleteLuYou(drawview.getLuyous().get(i));
                            }
                        }
                    }
                    // 删除场景
                    drawview.deletePlanCell(clickText);
                }

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void displaySingleDialog() {
        List<String> scenes = drawview.getTexts();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, scenes);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                drawview.addFlag(adapter.getItem(which));
                drawview.setState(DrawView.STATE_DRAW_FLAG);
                scene = adapter.getItem(which);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
