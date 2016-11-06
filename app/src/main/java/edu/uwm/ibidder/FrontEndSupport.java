package edu.uwm.ibidder;

import android.app.Activity;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import edu.uwm.ibidder.dbaccess.models.TaskModel;

/**
 * Created by Brandon on 11/4/16.
 * <br>Contains useful utility routines.
 */
public class FrontEndSupport {

    /**
     * Returns the status type of a status string.
     * Useful for callBackListeners
     * @param status of task
     * @return statusType
     */
    public static TaskModel.TaskStatusType getStatus(String status){
        switch(status){
            case "READY":
                return TaskModel.TaskStatusType.READY;
            case "FINISHED":
                return TaskModel.TaskStatusType.FINISHED;
            case "ACCEPTED":
                return TaskModel.TaskStatusType.ACCEPTED;
            case "TIMED_OUT":
                return TaskModel.TaskStatusType.TIMED_OUT;
            default:
                return null;
        }
    }

    /**
     * Formats a time in the form of Wed Oct 26 21:08:54 CDT 2016 (default java Time.getTime().toString())
     * @param unformattedtime in form of Wed Oct 26 21:08:54 CDT 2016
     * @return "Wed. Oct 26 2016, CDT 9:08 PM"
     */
    public static String getFormattedTime(String unformattedtime){
        String[] items = unformattedtime.split(" ");
        String[] timesplit = items[3].split(":");
        String filler = "";
        int hour = Integer.parseInt(timesplit[0]);
        int mins = Integer.parseInt(timesplit[1]);
        if(mins < 10)
            filler = "0";
        String period;
        if(hour == 12){
            period = "PM";
        } else if(hour > 12){
            hour = hour % 12;
            period = "PM";
        } else if (hour == 0){
            hour = 12;
            period = "AM";
        } else{
            period = "AM";
        }

        return items[0] + ". " + items[1] + " " + items[2] + " " + items[5] +  ", " + items[4] + " " + hour + ":" + filler + mins + " " + period;
    }

    /**
     * Validates a task for creation
     * @param name of task
     * @param descr of task
     * @param price of task
     * @param expire of task
     * @param activity current activity
     * @return
     */
    public static boolean taskCreateValidation(String name, String descr, String price, Date expire, Activity activity){
        if(!name.matches("") && !descr.matches("") && !price.matches("")){
            if(Double.parseDouble(price) < 0){
                return false;
            }
            // expire date must be in the future
            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            int future = 5;
            cal.setTime(now);
            cal.add(Calendar.MINUTE, future);
            if(cal.getTimeInMillis() < expire.getTime()){
                return true;
            } else{
                Toast.makeText(activity, "Expiration time must be at least "+future+" min. from the current time", Toast.LENGTH_LONG).show();
                return false;
            }
        } else{
            Toast.makeText(activity, "Invalid information", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Takes in an abbreviated month (first three letters) and returns the month's number
     * @param month
     * @return calender month
     */
    public static int convertMonth(String month){
        month = month.toUpperCase();
        switch(month){
            case "JAN":
                return 1;
            case "FEB":
                return 2;
            case "MAR":
                return 3;
            case "APR":
                return 4;
            case "MAY":
                return 5;
            case "JUN":
                return 6;
            case "JUL":
                return 7;
            case "AUG":
                return 8;
            case "SEP":
                return 9;
            case "OCT":
                return 10;
            case "NOV":
                return 11;
            case "DEC":
                return 12;
            default:
                return -1;
        }
    }

}
