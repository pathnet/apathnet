package com.pathnet.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class BaseListAdapter<T> extends BaseAdapter {
    public List<T> mBaseDatas;
    public Context mContext;
    public int mBasePosition = -1;

    public BaseListAdapter(List<T> mDatas, Context context) {
        super();
        this.mBaseDatas = mDatas;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return null == mBaseDatas ? 0 : mBaseDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mBaseDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    public void setPosition(int position) {
        this.mBasePosition = position;
        notifyDataSetChanged();
    }

    public int getBasePosition() {
        return mBasePosition;
    }


}
