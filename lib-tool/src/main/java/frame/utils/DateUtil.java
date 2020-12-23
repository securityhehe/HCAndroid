package frame.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Author: TinhoXu
 * E-mail: xth@erongdu.com
 * Date: 2016/7/28 13:49
 * <p>
 * Description: 常用的日期格式化方法
 */
@SuppressWarnings("unused")
public class DateUtil {
    public enum Format {
        /**
         * 日期 + 时间类型格式，到秒
         */
        SECOND("yyyy-MM-dd HH:mm:ss"),
        /**
         * 日期 + 时间类型格式，到分
         */
        MINUTE("yyyy-MM-dd HH:mm"),
        /**
         * 日期类型格式，日-月-年
         */
        DATE("dd-MM-yyyy"),
        /**
         * 日期类型格式，年-月-日
         */
        DATE2("yyyy-MM-dd"),
        /**
         * 日期类型格式，到月
         */
        MONTH("MM-yyyy"),
        /**
         * 日期类型格式，到月
         */
        MONTH_CHINA("yyyy年MM月"),
        /**
         * 时间类型的格式
         */
        TIME("HH:mm:ss");
        // 格式化格式
        private String value;

        Format(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


    // 注意SimpleDateFormat不是线程安全的
    private static SoftHashMap<String, ThreadLocal<SimpleDateFormat>> map = new SoftHashMap<>();

    /**
     * 日期格式化
     */
    public static String formatter(Format format, Object date) {
        if (date == null) {
            return "";
        } else {
            SimpleDateFormat sdf = null;
            String key = format.getValue();
            if (map.containsKey(key)) {
                sdf = map.get(key).get();
            }
            if (null == sdf) {
                sdf = new SimpleDateFormat(key, Locale.getDefault());
                ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<>();
                threadLocal.set(sdf);
                map.put(key, threadLocal);
            }
            if (RegularUtil.isInteger(date.toString())) {
                return sdf.format(new Date(ConverterUtil.getLong(date.toString())));
            } else {
                return date.toString();
            }
        }
    }

    /**
     * 计算两个时间戳之间的天数 -- 按时间
     */
    public static int computeDays(long start, long end) {
        long timeInterval = end - start;
        if (timeInterval > 0) {
            double days = timeInterval / 1000f / 60 / 60 / 24;
            return (int) Math.ceil(days);
        }
        return 0;
    }

    /**
     * 输出的Date格式为：YY-MM-DD_HH-mm-ss
     *
     * @return
     */
    public static String getFormatDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return simpleDateFormat.format(new Date());
    }

    /**
     * 把传入的日期对象，转换成指定格式的日期字符串
     *
     * @param date    日期对象
     * @param pattern 指定转换格式
     * @return String 格式化后的日期字符串
     */
    public static final String getFormat2DateStr(Date date, Format pattern) {
        SimpleDateFormat df = null;
        String returnValue = "";
        if (date != null) {
            df = new SimpleDateFormat(pattern.getValue());
            returnValue = df.format(date);
        }
        return returnValue;
    }

    /**
     * 把传入的日期字符串，转换成指定格式的日期对象
     *
     * @param dateString 日期字符串
     * @param pattern    指定转换格式
     * @return Date  日期对象
     */
    public static Date getFormat2Date(String dateString, Format pattern) {
        SimpleDateFormat df = null;
        Date date = null;
        if (dateString != null) {
            try {
                df = new SimpleDateFormat(pattern.getValue());
                date = df.parse(dateString);
            } catch (Exception e) {
            }
        }
        return date;
    }


    ////////////////////////
    public static String datePattern = "yyyy-MM-dd";
    public static String datePattern_YYYYMM = "yyyy-MM";
    public static String datePattern_yyyyMMdd = "yyyyMMdd";
    public static String timePattern = "HH:mm:ss";

    @SuppressWarnings("deprecation")
    public static Date dateAddMins(Date date, int minCnt) {
        Date d = new Date(date.getTime());
        d.setMinutes(d.getMinutes() + minCnt);
        return d;
    }

    /**
     * 日期相减
     *
     * @param date  日期
     * @param date1 日期
     * @return 返回相减后的日期
     */
    public static int diffDate(Date date, Date date1) {
        return (int) ((getMillis(date) - getMillis(date1)) / (24 * 3600 * 1000));
    }

    public static long getMillis(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis();
    }

    /**
     * 日期 相减
     *
     * @param date  日期
     * @param date1 日期
     * @return int 返回相减后的分钟
     */
    public static int diffDateToMin(Date date, Date date1) {
        return (int) (Math.abs((getMillis(date) - getMillis(date1))) / (1000 * 60));
    }

    /**
     * 根据默认日期格式，返回日期字符串。
     *
     * @param aDate 日期对象
     * @return String '年月日'日期字符串
     */
    public static final String getDate(Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";
        if (aDate != null) {
            df = new SimpleDateFormat(datePattern);
            returnValue = df.format(aDate);
        }
        return (returnValue);
    }

    /**
     * 把传入的日期对象，转换成指定格式的日期字符串
     *
     * @param date    日期对象
     * @param pattern 指定转换格式
     * @return String 格式化后的日期字符串
     */
    public static final String getDate(Date date, String pattern) {
        SimpleDateFormat df = null;
        String returnValue = "";
        if (date != null) {
            df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return (returnValue);
    }

    /**
     * 把传入的日期字符串，转换成指定格式的日期对象
     *
     * @param dateString 日期字符串
     * @param pattern    指定转换格式
     * @return Date  日期对象
     */
    public static Date getDate(String dateString, String pattern) {
        SimpleDateFormat df = null;
        Date date = null;
        if (dateString != null) {
            try {
                df = new SimpleDateFormat(pattern);
                date = df.parse(dateString);
            } catch (Exception e) {
            }
        }
        return date;
    }


    /**
     * 获取指定时间天的开始时间
     *
     * @param date
     * @return
     */
    public static Date getDayStartTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DATE), 0, 0, 0);
        return cal.getTime();
    }

    /**
     * 获取指定时间天的结束时间
     *
     * @param date
     * @return
     */
    public static Date getDayEndTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DATE), 23, 59, 59);
        return cal.getTime();
    }

    /**
     * String转化Date格式
     *
     * @param date
     * @param type
     * @return
     */
    public static Date parse(String date, String type) {
        SimpleDateFormat formatter = new SimpleDateFormat(type);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(date, pos);
        return strtodate;

    }

    /**
     * 得到指定日期之间的天数集合
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<Date> dateSplit(Date startDate, Date endDate)
            throws Exception {
        if (!startDate.before(endDate))
            throw new Exception("开始时间应该在结束时间之后");
        Long spi = endDate.getTime() - startDate.getTime();
        Long step = spi / (24 * 60 * 60 * 1000);// 相隔天数

        List<Date> dateList = new ArrayList<Date>();

        dateList.add(endDate);
        for (int i = 1; i <= step; i++) {
            dateList.add(new Date(dateList.get(i - 1).getTime()
                    - (24 * 60 * 60 * 1000)));// 比上一天减一
        }
        return dateList;
    }

    /**
     * 得到指定日期之间的月数集合
     *
     * @param minDate
     * @param maxDate
     * @return
     * @throws ParseException
     */
    public static List<String> getMonthBetween(String minDate, String maxDate) throws ParseException {
        ArrayList<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(sdf.parse(minDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

        max.setTime(sdf.parse(maxDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }

        return result;
    }

    /**
     * 得到指定之前的前后几天
     *
     * @param day
     * @param date
     * @return
     */
    @SuppressWarnings("static-access")
    public static Date getDateBefore(int day, Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, day);//把日期往后增加一天.整数往后推,负数往前移动

        date = calendar.getTime();
        return date;
    }

    @SuppressWarnings("deprecation")
    public static Date dateAddDays(Date date, int days) {
        Date d = new Date(date.getTime());
        d.setDate(d.getDate() + days);
        return d;
    }

    @SuppressWarnings("deprecation")
    public static Date dateAddMonth(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, days);
        return calendar.getTime();
    }

    /**
     * @param dt1
     * @param dt2
     * @return int    返回类型
     * @Title: compareDate
     * @Description: 比较时间大小（第一个时间在第二个时间之前返回-1；第一个时间在第二个时间之后返回1，相同返回0）
     */
    public static int compareDate(Date dt1, Date dt2) {
        try {
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 计算两个时间点之间的天数
     */
    public static int getBetweenDay(String startTime, String endTime) {
        try {
            Calendar startCal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);
            startCal.setTime(startDate);
            endCal.setTime(endDate);
            // 得到两个日期相差的天数 还可通过去除后面的数 求得相差的小时数,分钟数,秒 等等
            return ((int) (endCal.getTime().getTime() / 1000) - (int) (startCal.getTime().getTime() / 1000)) / 3600 / 24;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 处理2019-01-31T16:00:00.000+0000 格式为  00-00-0000 [日-月-年]
     * @param dateStr
     * @return
     */
    public static String dealDateTFormat(String dateStr,Format format) {
        if (TextUtil.isEmpty(dateStr)) return "00-00-0000";
        Date date1 = null;
        DateFormat df2 = null;
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = df.parse(dateStr);
            df2 = new SimpleDateFormat(format.value);
            return df2.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "00-00-0000";
    }
}
