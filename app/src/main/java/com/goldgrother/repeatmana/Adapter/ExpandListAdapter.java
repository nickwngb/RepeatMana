package com.goldgrother.repeatmana.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.goldgrother.repeatmana.Other.MyTime;
import com.goldgrother.repeatmana.Other.ProblemRecord;
import com.goldgrother.repeatmana.Other.Worker;
import com.goldgrother.repeatmana.R;

import java.util.List;

/**
 * Created by v on 2016/1/4.
 */
public class ExpandListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Worker> groups;

    public ExpandListAdapter(Context context, List<Worker> groups) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.groups = groups;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<ProblemRecord> chList = groups.get(groupPosition).getItems();
        return chList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View v, ViewGroup parent) {

        final ProblemRecord child = (ProblemRecord) getChild(groupPosition, childPosition);
        ChildTag tag;
        if (v == null) {
            tag = new ChildTag();
            v = inflater.inflate(R.layout.item_exlist_child, null);
            tag.date = (TextView) v.findViewById(R.id.tv_item_exlist_date);
            tag.content = (TextView) v.findViewById(R.id.tv_item_exlist_content);
            v.setTag(tag);
        } else {
            tag = (ChildTag) v.getTag();
        }
        tag.content.setText(child.getResponseContent());
        tag.date.setText(MyTime.convertTime(child.getResponseDate()));
        return v;
    }

    class ChildTag {
        TextView date;
        TextView content;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<ProblemRecord> chList = groups.get(groupPosition).getItems();
        return chList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View v, ViewGroup parent) {
        Worker group = (Worker) getGroup(groupPosition);
        GroupTag tag = null;
        if (v == null) {
            tag = new GroupTag();
            v = inflater.inflate(R.layout.item_exlist_group, null);
            tag.workerNo = (TextView) v.findViewById(R.id.tv_item_exlist_workerno);
            v.setTag(tag);
        } else {
            tag = (GroupTag) v.getTag();
        }
        tag.workerNo.setText(group.getWorkerNo());
        return v;
    }

    class GroupTag {
        TextView workerNo;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
