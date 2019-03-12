package br.com.alexandrebarboza.vacinas.Utility;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alexandre on 20/12/2017.
 */

public class Dates {

    public static Date StringToDate(String str, String language, String country, boolean full) {
        if (str == null) return null;
        Locale locale = new Locale(language, country);
        SimpleDateFormat format;
        Date result = null;
        if (full == true) {
            format = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", locale);
        } else {
            format = new SimpleDateFormat("dd/MM/yyyy", locale);
        }
        try {
            result = (Date) format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
        }
        return result;
    }

    public static Date getShortDateForString(String str) {
        if (str == null) return null;
        SimpleDateFormat format;
        Date result = null;
        format = new SimpleDateFormat("dd/MM/yy");
        try {
            result = (Date) format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
        }
        return result;
    }

    public static String DateToString(Date data, int size) {
        Locale locale = new Locale("pt", "BR");
        DateFormat format = DateFormat.getDateInstance(size, locale);
        String string = format.format(data);
        return string;
    }

    public static Date getDateForSqlString(String sql) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = (Date) format.parse(sql);
        } catch (ParseException e) {
            // e.printStackTrace();
        }
        return date;
    }

    public static String getTimeStringForSql(String sql) {
        DateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date parsed = null;
        try {
            parsed = format.parse(sql);
        } catch (ParseException e) {
            // e.printStackTrace();
            return null;
        }
        java.sql.Time sqlTime = new java.sql.Time(parsed.getTime());
        return sqlTime.toString();
    }

    public static String getShortDateForString(String arg, String language, String country) {
        Locale locale = new Locale(language, country);
        DateFormat format1 = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", locale);
        DateFormat format2 = new SimpleDateFormat("dd/MM/yyyy", locale);
        Date date1 = null;
        Date date2 = null;
        String string1, string2 = null;
        try {
            date1 = (Date) format1.parse(arg);
            string1 = format2.format(date1);
            date2 = (Date) format2.parse(string1);
            string2 = format2.format(date2);
        } catch (ParseException e) {
            // e.printStackTrace();
        }
        return string2;
    }

    public static java.sql.Date getSQLDate(String data, boolean flag) {
        SimpleDateFormat sdf;
        if (flag == true) {
            sdf = new SimpleDateFormat("dd/MM/yyyy");
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }
        Date parsed = null;
        try {
            parsed = sdf.parse(data);
        } catch (ParseException e) {
            // e.printStackTrace();
            return null;
        }
        return new java.sql.Date(parsed.getTime());
    }

    public static java.sql.Time getSQLTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        java.sql.Time result;
        long millis;
        try {
            millis = sdf.parse(time).getTime();
            result = new java.sql.Time(millis);
        } catch (ParseException e) {
            // e.printStackTrace();
            return null;
        }
        return result;
    }

    public static java.util.Date convertSQLFromDefaultDate(java.sql.Date sqlDate) {
        java.util.Date javaDate = null;
        if (sqlDate != null) {
            javaDate = new Date(sqlDate.getTime());
        }
        return javaDate;
    }

    public static java.sql.Date getSQLDateFromUtilDate(Date utilDate) {
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        return sqlDate;
    }

    public static java.sql.Time getSQLTimeFromUtilDate(Date utilDate) {
        java.sql.Time sqlTime = new java.sql.Time(utilDate.getTime());
        return sqlTime;
    }

    public static String getSQLStringFromDate(Date utilDate) {
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        return sqlDate.toString();
    }

    public static int numberOfDays(Date first, Date last) {
        int days = 0;
        try {
            long difference = last.getTime() - first.getTime();
            days = (int) TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return days;
    }

    public static Calendar puttTimeForLastDate(Date last) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min  = cal.get(Calendar.MINUTE);
        int sec  = cal.get(Calendar.SECOND);
        /*
        System.out.println(">>> LAST DATE: " + last.toString());
        System.out.println(">>> HOUR OF DAY: " + hour);
        */
        cal.setTime(last);
        cal.add(Calendar.HOUR_OF_DAY, hour);
        cal.add(Calendar.MINUTE, min);
        cal.add(Calendar.SECOND, sec +10);

        //System.out.println(">>> AND NOW DATE IS: " + cal.getTime().toString());

        return cal;
    }

    public static Calendar joinDateAndTimeSql(String date, String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        /*
        System.out.println(">>> Join to Calendar: " + calendar.getTime().toString());
        System.out.println(">>> DATE: " + date);
        System.out.println(">>> TIME: " + time);
        */
        String join = date + " " + time;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date parsed = null;
        try {
            parsed = sdf.parse(join);
        } catch (ParseException e) {
            sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            try {
                parsed = sdf.parse(join);
            } catch (ParseException e1) {
                // e1.printStackTrace();
                return null;
            }
        }

        //System.out.println(">>> DATE PARSED: " + parsed.toString());

        calendar.setTime(parsed);

        //System.out.println(">>> Calendar Joinned: " + calendar.getTime().toString());

        return calendar;
    }

    public static Date getDateLimitFor(Date start, String time, int amount) {

        // System.out.println(">>> DATA INICIO: " + start.toString());
        // System.out.println(">>> VENCE EM: " + amount + " " + time);

        Date result = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        if (time.toUpperCase().compareTo("DIAS") == 0) {
            cal.add(Calendar.DAY_OF_MONTH, amount);
        } else if (time.toUpperCase().compareTo("SEMANAS") == 0) {
            cal.add(Calendar.WEEK_OF_MONTH, amount);
        } else if (time.toUpperCase().compareTo("MESES") == 0) {
            cal.add(Calendar.MONTH, amount);
        } else if (time.toUpperCase().compareTo("ANOS") == 0) {
            cal.add(Calendar.YEAR, amount);
        } else if (time.toUpperCase().compareTo("PARA SEMPRE") == 0) {
            cal.add(Calendar.YEAR, 10);
        }
        result = cal.getTime();

        // System.out.println(">>> DATA VENCIMENTO: " + result.toString());

        return result;
    }

    public static long getDifferenceBetween(Date bigger, Date smaller) {
        Calendar cal_1 = Calendar.getInstance();
        Calendar cal_2 = Calendar.getInstance();
        cal_1.setTime(bigger);
        cal_2.setTime(smaller);
        cal_1.set(Calendar.HOUR_OF_DAY, 0);
        cal_1.set(Calendar.MINUTE, 0);
        cal_1.set(Calendar.SECOND, 0);
        cal_2.set(Calendar.HOUR_OF_DAY, 0);
        cal_2.set(Calendar.MINUTE, 0);
        cal_2.set(Calendar.SECOND, 0);
        bigger = cal_1.getTime();
        smaller = cal_2.getTime();
        return bigger.getTime() - smaller.getTime();
    }

}
