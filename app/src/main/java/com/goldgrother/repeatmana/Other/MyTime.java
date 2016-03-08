package com.goldgrother.repeatmana.Other;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hao_jun on 2016/3/8.
 */
public class MyTime {
    public static String convertTime(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            Date d = dateFormat.parse(time);
            Date now = new Date();
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(d);
            c2.setTime(now);
            if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
                int hour = c1.get(Calendar.HOUR_OF_DAY);
                int min = c1.get(Calendar.MINUTE);
                if (hour >= 12) {
                    return (hour - 12) + ":" + (min < 10 ? "0" + min : min) + " p.m.";
                } else {
                    return hour + ":" + (min < 10 ? "0" + min : min) + " a.m.";
                }
            } else {
                int month = c1.get(Calendar.MONTH) + 1;
                int day = c1.get(Calendar.DAY_OF_MONTH);
                return month + "/" + day;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String convertTimeForResponse(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            Date d = dateFormat.parse(time);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(d);

            int month = c1.get(Calendar.MONTH) + 1;
            int day = c1.get(Calendar.DAY_OF_MONTH);
            int hour = c1.get(Calendar.HOUR_OF_DAY);
            int min = c1.get(Calendar.MINUTE);
            if (hour >= 12) {
                return month + "/" + day + " " + (hour - 12) + ":" + (min < 10 ? "0" + min : min) + " p.m.";
            } else {
                return month + "/" + day + " " + hour + ":" + (min < 10 ? "0" + min : min) + " p.m.";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

}
