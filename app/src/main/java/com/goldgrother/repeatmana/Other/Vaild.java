package com.goldgrother.repeatmana.Other;

import android.content.Context;
import android.widget.Toast;

import com.goldgrother.repeatmana.R;


/**
 * Created by v on 2016/2/16.
 */
public class Vaild {
    public static boolean login(Context context,String acc,String pwd){
        if(acc.isEmpty()||pwd.isEmpty()){
            Toast.makeText(context, context.getResources().getString(R.string.msg_err_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
