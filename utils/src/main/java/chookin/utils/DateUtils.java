package chookin.utils;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by zhuyin on 7/30/14.
 */
public class DateUtils {
    public static final long DAY_MILLISECONDS = 1000L * 3600 * 24;
    public static final long WEEK_MILLISECONDS = DAY_MILLISECONDS * 7;
    public static final long MONTH_MILLISECONDS = DAY_MILLISECONDS * 30;
    public static final long YEAR_MILLISECONDS = DAY_MILLISECONDS * 365;

    public static Date parseDate(String str){
        if(str == null){
            return null;
        }
        str = str.trim();
        if(str.equals("--") || str.isEmpty()){
            return null;
        }

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date date;
        try {
            date = new Date(sdf.parse(str).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException(str + " cannot convert to Date", e);
        }
        return date;
    }

    public static String convertToDateString(java.util.Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static java.util.Date convertDateStringToDate(String str){
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            throw new IllegalArgumentException(str + " cannot convert to Calendar", e);
        }
    }

    public static Calendar convertDateStringToCalendar(String str){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(convertDateStringToDate(str));
        return calendar;
    }

    public static boolean isWeekend(java.util.Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return isWeekend(calendar);
    }
    public static boolean isWeekend(Calendar calendar){
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        if(weekDay == 1 || weekDay == 7){//SUNDAY, SATURDAY
            return true;
        }else {
            return false;
        }
    }

    public static Calendar getPrevWorkDay(Calendar calendar){

        Calendar curDay = (Calendar) calendar.clone();
        do {
            curDay.add(Calendar.DAY_OF_YEAR, -1);
        } while (isWeekend(curDay));
        return curDay;
    }
}
