package org.popcraft.popcraft.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/y HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
