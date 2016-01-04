package com.goldgrother.repeatmana.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldgrother.repeatmana.Other.ProblemRecord;
import com.goldgrother.repeatmana.R;

import java.util.List;

/**
 * Created by v on 2016/1/2.
 */
public class ListAdapter extends MyBaseAdapter {

    private List<ProblemRecord> list;

    public ListAdapter(Context context, List<ProblemRecord> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ProblemRecord getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewTag tag = null;
        if (v == null) {
            tag = new ViewTag();
            v = getInflater().inflate(R.layout.item_list, null);
            tag.CreateProblemDate = (TextView) v.findViewById(R.id.tv_item_createproblemdate);
            tag.ProblemDescription = (TextView) v.findViewById(R.id.tv_item_problemdescription);
            tag.Background = (LinearLayout) v.findViewById(R.id.layout_background);
            v.setTag(tag);
        } else {
            tag = (ViewTag) v.getTag();
        }
        // setText
        tag.CreateProblemDate.setText(getItem(position).getCreateProblemDate());
        tag.ProblemDescription.setText(getItem(position).getProblemDescription());
        // setBackground
        //tag.Background.setBackgroundResource(getResources().getDrawable(R.drawable.,null));
        return v;
    }

    class ViewTag {
        TextView CreateProblemDate;
        TextView ProblemDescription;
        LinearLayout Background;
    }
}
