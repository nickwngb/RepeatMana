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
    public static String convertTimeForProblem(String time) {
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
                    return (hour - 12) + ":" + (min < 10 ? "0" + min : min) + " PM";
                } else {
                    return hour + ":" + (min < 10 ? "0" + min : min) + " AM";
                }
            } else if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR) - 1) {
                return "Yesterday";
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
            Date now = new Date();
            Calendar origan = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            origan.setTime(d);
            c2.setTime(now);
            int month = origan.get(Calendar.MONTH) + 1;
            int day = origan.get(Calendar.DAY_OF_MONTH);
            int hour = origan.get(Calendar.HOUR_OF_DAY);
            int min = origan.get(Calendar.MINUTE);
            if (origan.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && origan.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {

                if (hour >= 12) {
                    return (hour - 12) + ":" + (min < 10 ? "0" + min : min) + " PM";
                } else {
                    return hour + ":" + (min < 10 ? "0" + min : min) + " AM";
                }
            } else if (origan.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && origan.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR) - 1) {

                if (hour >= 12) {
                    return "Yesterday " + (hour - 12) + ":" + (min < 10 ? "0" + min : min) + " PM";
                } else {
                    return "Yesterday " + hour + ":" + (min < 10 ? "0" + min : min) + " AM";
                }
            } else {
                if (hour >= 12) {
                    return month + "/" + day + " " + (hour - 12) + ":" + (min < 10 ? "0" + min : min) + " PM";
                } else {
                    return month + "/" + day + " " + hour + ":" + (min < 10 ? "0" + min : min) + " AM";
                }
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

}
