package chookin.utils;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by chookin on 7/30/14.
 */
public class DateUtils {
    public static final long DAY_MILLISECONDS = 1000L * 3600 * 24;
    public static final long WEEK_MILLISECONDS = DAY_MILLISECONDS * 7;
    public static final long MONTH_MILLISECONDS = DAY_MILLISECONDS * 30;
    public static final long YEAR_MILLISECONDS = DAY_MILLISECONDS * 365;

    public static Date parseDate(String strDate){
        if(strDate == null){
            return null;
        }
        strDate = strDate.trim();
        if(strDate.equals("--") || strDate.isEmpty()){
            return null;
        }

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date date = null;
        try {
            date = new Date(sdf.parse(strDate).getTime());
        } catch (ParseException e) {
        }
        return date;
    }

    public static String convertToDateString(java.util.Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
