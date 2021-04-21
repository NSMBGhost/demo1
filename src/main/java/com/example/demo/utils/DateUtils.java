package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static final String DATE_FORMAT_YEAR_MONTH_DAY = "yyyy-MM-dd";
    public static final String DATE_FORMAT_HOUR_MINUTE_SECOND = "HH:mm:ss";
    public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_ALL = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_FORMAT_SHORT_DAY = "yyyyMMdd";
    public static final String DATE_FORMAT_SIMPLE = "yyyyMMddHHmmss";

    public static final Integer MIN_UNIT = 1000 * 60;

    public static Timestamp now(){
        return new Timestamp(System.currentTimeMillis());
    }

    public static String formatNow(String format){
        DateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    public static Integer getCurrentMin(){
        DateFormat sdf = new SimpleDateFormat(DATE_FORMAT_SIMPLE);
        String nowSt = sdf.format(new Date());
        return Integer.valueOf(nowSt.substring(8,12));
    }

    public static String formatTime(Timestamp date, String format){
        return formatTime(date.getTime(), format);
    }

    public static String formatTime(Date date, String format){
        return formatTime(date.getTime(), format);
    }

    public static String formatTime(long timeInMills, String format){
        DateFormat dateFormat = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMills);
        return dateFormat.format(calendar.getTime());
    }
    public static boolean isIngnoreTradeMin(String now){
        Integer hour = Integer.parseInt(now.substring(8,10));
        Integer min = Integer.parseInt(now.substring(10,12));
        if(hour == 9 && min < 30){
            return true;
        }
        if (hour == 11 && min > 29){
            return true;
        }

        if (hour == 15 && min >0 ){
            return true;
        }
        if(hour < 9 || hour >= 16 ){
            return true;
        }

        if(hour >= 12 && hour < 13 ){
            return true;
        }
        return false;
    }
    public static Timestamp parseTimestamp(String text, String format){
        Date date = parseDate(text, format);
        return new Timestamp(date.getTime());
    }

    public static Date parseDate(String text, String format) {
        SimpleDateFormat parser = new SimpleDateFormat(format);
        try {
            return parser.parse(text);
        } catch (ParseException e) {
            log.error("parse date value: "+ text +", format: "+ format +" error", e);
        }
        return new Date(0);
    }

    public static Timestamp truncate(Timestamp date, int field){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        Date retDate = truncate(calendar.getTime(), field);
        return new Timestamp(retDate.getTime());
    }

    public static int dayDiff(String strDate1, String strDate2, String format) {
        Date t1 = DateUtils.parseDate(strDate1, format);
        Date t2 = DateUtils.parseDate(strDate2, format);
        t1 = truncateTime(t1);
        t2 = truncateTime(t2);
        return (int)((t1.getTime()-t2.getTime())/(1000*60*60*24));
    }

    public static int dayDiff(Date t1, Date t2) {
        t1 = truncateTime(t1);
        t2 = truncateTime(t2);
        return (int)((t1.getTime()-t2.getTime())/(1000*60*60*24));
    }

    public static int minDiff(Date begin, Date end) {
        Long minutes = (end.getTime() - begin.getTime()) / MIN_UNIT;
        return minutes.intValue();

    }

    public static Date truncateTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Integer getHourMins(Date date) {
        String strDate = formatNow(DATE_FORMAT_SIMPLE);
        return Integer.valueOf(strDate.substring(8,12));
    }

}
