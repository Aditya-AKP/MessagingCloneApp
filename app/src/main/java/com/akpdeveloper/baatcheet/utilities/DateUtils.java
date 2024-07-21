package com.akpdeveloper.baatcheet.utilities;


import android.annotation.SuppressLint;

import com.akpdeveloper.baatcheet.models.DateModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;


@SuppressLint("SimpleDateFormat")
public class DateUtils {

    public static DateModel getTimestampFromLong(Long l){
        return new DateModel(l,0);
    }

    public static Long getLongFromTimestamp(DateModel t){
        return t.getSeconds();
    }

    public static String getDateFromTimestamp(DateModel t){
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy");
        return sfd.format(t.toDate());
    }

    public static String getTimeFromTimestamp(DateModel t){
        SimpleDateFormat sfd = new SimpleDateFormat("hh:mm aa");
        return sfd.format(t.toDate());
    }

    public static boolean isNewDay(DateModel oldDate,DateModel newDate){
        Calendar oldCal = Calendar.getInstance();
        oldCal.setTime(oldDate.toDate());
        Calendar newCal = Calendar.getInstance();
        newCal.setTime(newDate.toDate());
        if(oldCal.get(Calendar.DAY_OF_MONTH)!=newCal.get(Calendar.DAY_OF_MONTH)){
            return true;
        }else{
            if(oldCal.get(Calendar.MONTH)!=newCal.get(Calendar.MONTH)){
                return true;
            }else{
                return oldCal.get(Calendar.YEAR) != newCal.get(Calendar.YEAR);
            }
        }
    }

    public static boolean is24HourComplete(DateModel date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date.toDate());
        return System.currentTimeMillis() >= cal.getTimeInMillis()*24*60*60*1000;
    }
}
