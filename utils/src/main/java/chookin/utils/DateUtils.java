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

    /**

     G 年代标志符
     y 年
     M 月
     d 日
     h 时 在上午或下午 (1~12)
     H 时 在一天中 (0~23)
     m 分
     s 秒
     S 毫秒
     E 星期
     D 一年中的第几天
     F 一月中第几个星期几
     w 一年中第几个星期
     W 一月中第几个星期
     a 上午 / 下午 标记符
     k 时 在一天中 (1~24)
     K 时 在上午或下午 (0~11)
     z 时区

     * yyyy-MM-dd
     */
    public static java.util.Date convertDateStringToDate(String str, String dateformat){
        SimpleDateFormat sdf= new SimpleDateFormat(dateformat);
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            throw new IllegalArgumentException(str + " cannot convert to Calendar", e);
        }
    }

    public static Calendar convertDateStringToCalendar(String strDate, String dateformat){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(convertDateStringToDate(strDate, dateformat));
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
