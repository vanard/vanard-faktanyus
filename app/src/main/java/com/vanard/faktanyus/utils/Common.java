package com.vanard.faktanyus.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    public static String convertUnixToHour(long dt){
        Date date = new Date(dt*1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a");
        return simpleDateFormat.format(date);
    }

    public static String convertUnixToDate(long dt){
        Date date = new Date(dt*1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d yyyy");
        return simpleDateFormat.format(date);
    }
}
