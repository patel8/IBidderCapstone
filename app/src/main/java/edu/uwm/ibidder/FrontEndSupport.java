package edu.uwm.ibidder;

import android.app.Activity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
     * Configures task expiry date
     * @param d - determined from CalendarView
     * @param hour - determined from TimePicker
     * @param min - determined from TimePicker
     * @return
     */
    public static Calendar fillCalendar(Date d, int hour, int min){
        String items[] = d.toString().split(" "); // Fri Nov 18 14:00:51 CST 2016
        int year = Integer.parseInt(items[5]);
        int month = FrontEndSupport.convertMonth(items[1]);
        int day = Integer.parseInt(items[2]);
        String times[] = items[3].split(":");
        int sec = Integer.parseInt(times[2]);

        return new GregorianCalendar(year, month, day, hour, min, sec);
    }

    /**
     * Takes in an abbreviated month (first three letters) and returns the month's number (0 based)
     * @param month
     * @return calender month
     */
    public static int convertMonth(String month){
        month = month.toUpperCase();
        switch(month){
            case "JAN":
                return 0;
            case "FEB":
                return 1;
            case "MAR":
                return 2;
            case "APR":
                return 3;
            case "MAY":
                return 4;
            case "JUN":
                return 5;
            case "JUL":
                return 6;
            case "AUG":
                return 7;
            case "SEP":
                return 8;
            case "OCT":
                return 9;
            case "NOV":
                return 10;
            case "DEC":
                return 11;
            default:
                return -1;
        }
    }

    public static boolean isCurrentUser(String id){
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(id)){
            return true;
        }

        return false;
    }

}
