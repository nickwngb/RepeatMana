package com.goldgrother.repeatmana.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldgrother.repeatmana.Asyn.LoadPhoto;
import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.MyTime;
import com.goldgrother.repeatmana.Other.ProblemRecord;
import com.goldgrother.repeatmana.Other.Uti;
import com.goldgrother.repeatmana.R;

import java.util.List;

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
            v = getInflater().inflate(R.layout.item_list_problem, null);
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
        tag.datetime.setText(MyTime.convertTime(item.getResponseDate()));
        //item.getProblemStatus();

        // photo
//        if (item.getResponseRole().equals(Code.Flabor)) {
//            LoadPhoto(tag.photo, item.getResponseRole(), item.getFLaborNo(), item.getCustomerNo());
//        } else {
//            LoadPhoto(tag.photo, item.getResponseRole(), item.getResponseID());
//        }
        return v;
    }

    class ViewTag {
        public CircleImageView photo;
        public TextView name;
        public TextView content;
        public TextView datetime;
        public ImageView status;
    }

    private void LoadPhoto(CircleImageView circleImageView, String... datas) {
        if (Uti.isNetWork(getContext())) {
            LoadPhoto task = new LoadPhoto(circleImageView, new HttpConnection(), new LoadPhoto.OnLoadPhotoListener() {
                public void finish() {

                }
            });
            task.execute(datas);
        }
    }
}
