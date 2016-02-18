package com.goldgrother.repeatmana.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by v on 2016/1/2.
 */
public abstract class MyBaseAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private Resources resources;

    public MyBaseAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.resources = context.getResources();
    }

    public Context getContext() {
        return context;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public Resources getResources() {
        return resources;
    }

    public abstract int getCount();

    public abstract Object getItem(int position);

    public long getItemId(int position) {
        return 0;
    }

    public abstract View getView(int position, View v, ViewGroup parent);
}
