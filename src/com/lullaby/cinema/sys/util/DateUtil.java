package com.lullaby.cinema.sys.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {

    /**
     * 日期转字符串
     * @param date 日期
     * @return 字符串
     */
    public static String date2str(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(simpleDateFormat);
    }

    /**
     * 字符串转日期
     * @param dateStr 字符串
     * @return 日期
     * @throws ParseException 当传入的字符串格式不符合 "yyyy-MM-dd HH:mm:ss" 规范时抛出
     */
    public static Date str2Date(String dateStr) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.parse(dateStr);
    }
}
