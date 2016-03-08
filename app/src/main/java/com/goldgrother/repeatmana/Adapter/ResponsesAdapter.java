package com.goldgrother.repeatmana.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.goldgrother.repeatmana.Asyn.LoadPhoto;
import com.goldgrother.repeatmana.Other.Code;
import com.goldgrother.repeatmana.Other.HttpConnection;
import com.goldgrother.repeatmana.Other.MyTime;
import com.goldgrother.repeatmana.Other.ProblemResponse;
import com.goldgrother.repeatmana.Other.Uti;
import com.goldgrother.repeatmana.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by v on 2016/2/16.
 */
public class ResponsesAdapter extends MyBaseAdapter {
    private List<ProblemResponse> list;
    private String FLaborNo;
    private String CustomerNo;

    public ResponsesAdapter(Context context, String FLaborNo, String CustomerNo, List<ProblemResponse> list) {
        super(context);
        this.FLaborNo = FLaborNo;
        this.CustomerNo = CustomerNo;
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
    public View getView(int position, View v, ViewGroup parent) {
        ProblemResponse item = (ProblemResponse) getItem(position);
        ViewHolder tag;
        if (v == null) {
            v = getInflater().inflate(R.layout.block_response, null);
            tag = new ViewHolder();
            tag.background = (ImageView) v.findViewById(R.id.bg_item_who);
            //
            tag.f_block = (LinearLayout) v.findViewById(R.id.block_flabor);
            tag.f_content = (TextView) v.findViewById(R.id.tv_item_flabor_content);
            tag.f_photo = (CircleImageView) v.findViewById(R.id.img_item_flabor_photo);
            tag.f_name = (TextView) v.findViewById(R.id.tv_item_flabor_name);
            tag.f_datetime = (TextView) v.findViewById(R.id.tv_item_flabor_datetime);
            //
            tag.m_block = (LinearLayout) v.findViewById(R.id.block_manager);
            tag.m_content = (TextView) v.findViewById(R.id.tv_item_manager_content);
            tag.m_photo = (CircleImageView) v.findViewById(R.id.img_item_manager_photo);
            tag.m_name = (TextView) v.findViewById(R.id.tv_item_manager_name);
            tag.m_datetime = (TextView) v.findViewById(R.id.tv_item_manager_datetime);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        if (item.getResponseRole().equals(Code.Flabor)) {
            tag.f_content.setText(item.getResponseContent());
            //if (tag.f_photo.getTag() != null)
            //LoadPhoto(tag.f_photo,item.getResponseRole(),FLaborNo,CustomerNo);
            tag.f_name.setText(item.getResponseID());
            tag.f_datetime.setText(MyTime.convertTimeForResponse(item.getResponseDate()));
        } else {
            tag.m_content.setText(item.getResponseContent());
//            if (tag.m_photo.getTag() != null)
//                LoadPhoto(tag.m_photo, item.getResponseRole(), item.getResponseID());
            tag.m_name.setText(item.getResponseID());
            tag.m_datetime.setText(MyTime.convertTimeForResponse(item.getResponseDate()));
        }

        // background
        if (item.getResponseRole().equals(Code.Flabor)) {
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tag.background.setBackgroundDrawable(getResources().getDrawable(R.drawable.textbox_content));
            } else {
                tag.background.setBackground(getResources().getDrawable(R.drawable.textbox_content));
            }
            tag.f_block.setVisibility(View.VISIBLE);
            tag.m_block.setVisibility(View.GONE);

        } else {
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tag.background.setBackgroundDrawable(getResources().getDrawable(R.drawable.textbox_reponse));
            } else {
                tag.background.setBackground(getResources().getDrawable(R.drawable.textbox_reponse));
            }
            tag.f_block.setVisibility(View.GONE);
            tag.m_block.setVisibility(View.VISIBLE);
        }
        return v;
    }

    public static class ViewHolder {
        public ImageView background;
        /**
         * Flabor
         */
        public LinearLayout f_block;
        public TextView f_content;
        public CircleImageView f_photo;
        public TextView f_name;
        public TextView f_datetime;
        /**
         * Manager
         */
        public LinearLayout m_block;
        public TextView m_content;
        public CircleImageView m_photo;
        public TextView m_name;
        public TextView m_datetime;
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