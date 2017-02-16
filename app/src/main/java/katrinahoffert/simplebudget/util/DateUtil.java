package katrinahoffert.simplebudget.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    /** Converts an ISO 8601 date string (eg, "2017-02-15") into a Date object. */
    public static Date iso8601StringToDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Date " + date + " is not a valid ISO 8601 date");
        }
    }
}
