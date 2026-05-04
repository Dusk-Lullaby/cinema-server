package com.lullaby.cinema.sys.util;

import com.lullaby.cinema.sys.entity.Film;
import com.lullaby.cinema.sys.entity.FilmPlan;

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
        return simpleDateFormat.format(date);
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

    /**
     * 播放计划是否冲突
     * @param plan1 计划1
     * @param plan2 计划2
     * @return 冲突返回true，不冲突返回false
     */
    public static boolean isConflictPlan(FilmPlan plan1, FilmPlan plan2) {
        Date begin1 = plan1.getBegin();
        Date begin2 = plan2.getBegin();
        Date end1 = plan1.getEnd();
        Date end2 = plan2.getEnd();
        // 首先保证是同一个影厅
        if (plan1.getFilmHall().equals(plan2.getFilmHall())) {
            boolean case1 = begin2.before(begin1) && end2.after(begin1) && end2.before(end1);
            boolean case2 = begin2.after(begin1) && begin2.before(end2) && end2.before(end1);
            boolean case3 = begin2.after(begin1) && begin2.before(end2) && end2.after(end1);
            return case1 || case2 || case3;
        }
        return false;
    }
}
