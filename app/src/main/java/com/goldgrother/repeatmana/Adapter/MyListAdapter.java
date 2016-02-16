package com.goldgrother.repeatmana.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldgrother.repeatmana.Other.ProblemRecord;
import com.goldgrother.repeatmana.R;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by v on 2016/1/2.
 */
public class MyListAdapter extends MyBaseAdapter {

    private List<ProblemRecord> list;

    public MyListAdapter(Context context, List<ProblemRecord> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewTag tag;
        if (v == null) {
            v = getInflater().inflate(R.layout.item_list, null);
            tag = new ViewTag();
            tag.photo = (CircleImageView) v.findViewById(R.id.img_item_photo);
            tag.name = (TextView) v.findViewById(R.id.tv_item_name);
            tag.content = (TextView) v.findViewById(R.id.tv_item_responsecontent);
            tag.datetime = (TextView) v.findViewById(R.id.tv_item_responsedatetime);
            tag.status = (ImageView) v.findViewById(R.id.img_item_status);
            v.setTag(tag);
        } else {
            tag = (ViewTag) v.getTag();
        }
        ProblemRecord item = (ProblemRecord) getItem(position);
        // setText
        tag.name.setText(item.getResponseID());
        tag.content.setText(item.getResponseContent());
        tag.datetime.setText(item.getResponseDate());
        //item.getProblemStatus();
        return v;
    }

    class ViewTag {
        public CircleImageView photo;
        public TextView name;
        public TextView content;
        public TextView datetime;
        public ImageView status;
    }
}
