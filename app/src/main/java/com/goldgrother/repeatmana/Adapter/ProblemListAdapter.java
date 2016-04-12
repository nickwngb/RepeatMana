package com.goldgrother.repeatmana.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldgrother.repeatmana.Asyn.LoadPhoto;
import com.goldgrother.repeatmana.ImageLoadHelp.ImageLoader;
import com.goldgrother.repeatmana.Other.BitmapTransformer;
import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.MyTime;
import com.goldgrother.repeatmana.Other.ProblemRecord;
import com.goldgrother.repeatmana.Other.UserAccount;
import com.goldgrother.repeatmana.Other.Uti;
import com.goldgrother.repeatmana.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by v on 2016/1/2.
 */
public class ProblemListAdapter extends MyBaseAdapter {

    private List<ProblemRecord> list;
    private ImageLoader imageLoader;

    public ProblemListAdapter(Context context, List<ProblemRecord> list) {
        super(context);
        this.list = list;
        this.imageLoader = new ImageLoader(context);
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

        tag.datetime.setText(item.getResponseDate() != null ? MyTime.convertTimeForProblem(item.getResponseDate()) : "");
        // set status
        if (item.getProblemStatus() != null) {
            switch (item.getProblemStatus()) {
                case Code.Untreated:
                    tag.status.setBackground(getResources().getDrawable(R.drawable.status_untreate));
                    break;
                case Code.Processing:
                    tag.status.setBackground(getResources().getDrawable(R.drawable.status_processing));
                    break;
                case Code.Completed:
                    tag.status.setBackground(getResources().getDrawable(R.drawable.status_completed));
                    break;
            }
        }
        if (item.getResponseRole() != null) {
            if (item.getResponseRole().equals(Code.Manager)) {
                tag.photo.setImageBitmap(BitmapTransformer.Base64ToBitmap(UserAccount.getUserAccount().getPhoto()));
            } else {
                imageLoader.DisplayImage(item.getCustomerNo(), item.getFLaborNo(), tag.photo);
            }
        }
        return v;
    }

    static class ViewTag {
        public CircleImageView photo;
        public TextView name;
        public TextView content;
        public TextView datetime;
        public ImageView status;
    }


}
