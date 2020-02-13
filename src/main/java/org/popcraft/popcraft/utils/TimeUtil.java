package org.popcraft.popcraft.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/y HH:mm");

    public static String getCurrentTime() {
        return DATE_FORMAT.format(new Date());
    }

}
