package com.thymeleaf.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class DateUtil {
    public static String dateToString(Timestamp date){
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatDate.format(date);
    }
}
