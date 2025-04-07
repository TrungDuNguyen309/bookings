package com.example.hrms.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    // Định dạng ngày tháng giờ
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm";

    // Phương thức trả về đối tượng DateTimeFormatter với định dạng đã định sẵn
    public static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    }

    // Phương thức chuyển đổi chuỗi thành LocalDateTime theo định dạng
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, getDateTimeFormatter());
    }

    // Phương thức chuyển đổi LocalDateTime thành chuỗi theo định dạng
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(getDateTimeFormatter());
    }
}
