package de.simonmayrshofer.simonsblog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helpers {

    public static String convertDate(String unformattedDate) {

        if (unformattedDate == null)
            return "Date not found.";

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

    public static String getCommentCountString(int count) {
        if (count == 1)
            return "1 Comment";
        else
            return count + " Comments";
    }

}