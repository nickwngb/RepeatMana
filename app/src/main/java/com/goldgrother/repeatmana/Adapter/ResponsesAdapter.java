package com.goldgrother.repeatmana.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.goldgrother.repeatmana.Asyn.LoadPhoto;
import com.goldgrother.repeatmana.ImageLoadHelp.ImageLoader;
import com.goldgrother.repeatmana.Other.BitmapTransformer;
import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.MyTime;
import com.goldgrother.repeatmana.Other.ProblemResponse;
import com.goldgrother.repeatmana.Other.UserAccount;
import com.goldgrother.repeatmana.Other.Uti;
import com.goldgrother.repeatmana.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by v on 2016/2/16.
 */
public class ResponsesAdapter extends MyBaseAdapter {
    private List<ProblemResponse> list;
    private ImageLoader imageLoader;
    private String FLaborNo;
    private String CustomerNo;

    public ResponsesAdapter(Context context, String FLaborNo, String CustomerNo, List<ProblemResponse> list) {
        super(context);
        this.list = list;
        this.imageLoader = new ImageLoader(context);
        this.FLaborNo = FLaborNo;
        this.CustomerNo = CustomerNo;
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
    public View getView(int position, View v, ViewGroup parent) {
        ProblemResponse item = (ProblemResponse) getItem(position);
        ViewHolder tag;
        if (v == null) {
            v = getInflater().inflate(R.layout.block_response, null);
            tag = new ViewHolder();
            //
            tag.f_block = (LinearLayout) v.findViewById(R.id.block_flabor);
            tag.f_content = (TextView) v.findViewById(R.id.tv_item_flabor_content);
            tag.f_photo = (CircleImageView) v.findViewById(R.id.img_item_flabor_photo);
            tag.f_name = (TextView) v.findViewById(R.id.tv_item_flabor_name);
            tag.f_datetime = (TextView) v.findViewById(R.id.tv_item_flabor_datetime);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }

        tag.f_content.setText(item.getResponseContent());
        if (item.getResponseRole().equals(Code.Flabor)) {
            imageLoader.DisplayImage(CustomerNo, FLaborNo, tag.f_photo);
        } else {
            Bitmap photo = BitmapTransformer.Base64ToBitmap(UserAccount.getUserAccount().getPhoto());
            if (photo != null)
                tag.f_photo.setImageBitmap(photo);
        }
        tag.f_name.setText(item.getResponseID());
        tag.f_datetime.setText(MyTime.convertTimeForResponse(item.getResponseDate()));

        return v;
    }

    public static class ViewHolder {
        public LinearLayout f_block;
        public TextView f_content;
        public CircleImageView f_photo;
        public TextView f_name;
        public TextView f_datetime;
    }
}