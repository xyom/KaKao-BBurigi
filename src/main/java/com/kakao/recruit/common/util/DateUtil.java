package com.kakao.recruit.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static final long minute = 60 * 1000;
    public static final long day = 24 * 60 * minute;
    public static final long ALLOCATE_EXPIRED_TIME = 10 * minute;
    public static final long INQUIRY_EXPIRED_TIME = 7 * day;

    private static long compareDateFromNow(String date) throws Exception {
        long now = new Date().getTime();
        long start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).getTime();

        return now - start;
    }

    public static boolean isExpiredAllocateTime(String date) throws Exception {
        return DateUtil.compareDateFromNow(date) >= ALLOCATE_EXPIRED_TIME;
    }

    public static boolean isExpiredInQuiryTime(String date) throws Exception {
        return DateUtil.compareDateFromNow(date) >= INQUIRY_EXPIRED_TIME;
    }
}
