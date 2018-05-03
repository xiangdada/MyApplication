package com.example.administrator.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiangpengfei on 2017/11/10.
 */

public class ListViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<ListData> mDatas;
    private List<Boolean> mIsSelected;
    private boolean batch = false;

    public ListViewAdapter(Context context,@NonNull List<ListData> datas) {
        this.mContext = context;
        mIsSelected = new ArrayList<Boolean>();
        this.mDatas = datas;
        if (datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                mIsSelected.add(false);
            }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_list, viewGroup, false);
            holder = new ViewHolder();
            holder.textView = (TextView) view.findViewById(R.id.textview);
            holder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            view.setTag(holder);
        }
        holder.textView.setText(mDatas.get(i).getText());

        if (i < mIsSelected.size()) {
            Log.e("发送","getView: " + mIsSelected.get(i));
            holder.checkBox.setChecked(mIsSelected.get(i));
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox checkBox = (CheckBox) view;
                    mIsSelected.set(i,checkBox.isChecked());
                    Log.e("发送","setOnClickListener  " + checkBox.isChecked());
                }
            });
        }

        if (batch) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * 开启批量操作
     */
    public void batch() {
        mIsSelected.clear();
        for (int i = 0; i < mDatas.size(); i++) {
            mIsSelected.add(false);
        }
        this.batch = true;
        notifyDataSetChanged();
    }

    /**
     * 关闭批量操作
     */
    public void batchDisable() {
        this.batch = false;
        notifyDataSetChanged();
    }

    /**
     * 全选
     */
    public void allSelect() {
        Log.e("发送","allSelect: ");
        for(int i=0;i<mIsSelected.size();i++) {
            mIsSelected.set(i,true);
        }
        notifyDataSetChanged();
    }

    /**
     * 反选
     */
    public void reverseSelect() {
        for(int i=0;i<mIsSelected.size();i++) {
            mIsSelected.set(i,!mIsSelected.get(i));
        }
        notifyDataSetChanged();
    }

    /**
     * 重置
     */
    public void reSet() {
        for(int i=0;i<mIsSelected.size();i++) {
            mIsSelected.set(i,false);
        }
        notifyDataSetChanged();
    }


    public List<ListData> send() {
        for(int i=0;i<mIsSelected.size();i++) {
            Log.e("发送",i + "  " + mIsSelected.get(i));
        }
        List<ListData> listDatas = new ArrayList<ListData>();
        for(int i=0;i<mIsSelected.size();i++) {
            if(i < mDatas.size()) {
                if(mIsSelected.get(i)) {
                    listDatas.add(mDatas.get(i));
                }
            }
        }
       return listDatas;
    }

    static class ViewHolder {
        public TextView textView;
        public CheckBox checkBox;
    }


}
