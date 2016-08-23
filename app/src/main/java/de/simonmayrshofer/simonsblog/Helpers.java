package de.simonmayrshofer.simonsblog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helpers {

    public static String convertDate(String unformattedDate) {

        SimpleDateFormat unformattedDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        SimpleDateFormat formattedDateFormat = new SimpleDateFormat("EEE, MMM d, h:mm a");

        try {
            Date date = unformattedDateFormat.parse(unformattedDate);
            return formattedDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return unformattedDate;
        }
    }

}