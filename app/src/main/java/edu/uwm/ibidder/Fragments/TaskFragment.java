package edu.uwm.ibidder.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import edu.uwm.ibidder.Activities.ProfileActivity;
import edu.uwm.ibidder.FrontEndSupport;
import edu.uwm.ibidder.R;
import edu.uwm.ibidder.Activities.TaskActivity;
import edu.uwm.ibidder.Activities.TaskActivityII;
import edu.uwm.ibidder.dbaccess.BidAccessor;
import edu.uwm.ibidder.dbaccess.DateTools;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.TaskModel;
import edu.uwm.ibidder.dbaccess.models.UserModel;

public class TaskFragment extends Fragment {

    private TextView taskname;
    private TextView taskdescr;
    private TextView taskowner;
    private TextView taskendtime;
    private TextView tskdate;
    private String savedName, savedDescr;
    private Date savedDate;
    private HashMap<String, Boolean> savedTags;
    private float savedPrice;
    private boolean resetSave = false;
    private UserModel currentUser;
    private TaskModel currentTask;

    public TaskFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_task, container, false);
        initializeAllWidgets(v);
        setHasOptionsMenu(true);

        String taskid = ((TaskActivityII)getActivity()).getTaskID();
        String taskstatus = ((TaskActivityII)getActivity()).getTaskStatus();
        TaskModel.TaskStatusType status = FrontEndSupport.getStatus(taskstatus);

        Log.i("TAG", "onCreate: "+status.toString());
        TaskAccessor ta = new TaskAccessor();
        ta.getTask(taskid, new TaskCallbackListener(status) {
            @Override
            public void dataUpdate(TaskModel tm) {
                savedName = tm.getTitle();
                savedDescr = tm.getDescription();
                savedTags = tm.getTags();
                taskname.setText(savedName);
                taskdescr.setText(savedDescr);
                currentTask = tm;

                Date d = DateTools.epochToDate(tm.getExpirationTime());
                savedDate = d;
                taskendtime.setText(FrontEndSupport.getFormattedTime(d.toString()));
                UserAccessor ua = new UserAccessor();
                ua.getUser(tm.getOwnerId(), new UserCallbackListener() {
                    @Override
                    public void dataUpdate(UserModel um) {
                        taskowner.setText(um.getFirstName());
                        currentUser = um;
                    }
                });
            }
        });
        return v;
    }

    private void initializeAllWidgets(View v){
        taskname = (TextView)v.findViewById(R.id.label_taskActName);
        taskdescr = (TextView)v.findViewById(R.id.label_taskActDescr);
        taskowner = (TextView)v.findViewById(R.id.label_taskActPoster);
        taskendtime = (TextView)v.findViewById(R.id.label_taskActEndtime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.edit_task_menu:
                resetSave = true;
                AlertDialog editDialog = editAlertDialog();
                editDialog.show();
                return true;
            case R.id.place_bid_menu:
                AlertDialog bidDialog = bidAlertDialog();
                bidDialog.show();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private AlertDialog bidAlertDialog() {
        final AlertDialog ad = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ad.setTitle("Place BID");
        View view = inflater.inflate(R.layout.alert_dialog_place_bid, null);
        ad.setView(view);

        TextView BidTitle = (TextView) view.findViewById(R.id.bid_title_alert);
        final EditText BidAmount = (EditText) view.findViewById(R.id.bid_amount_alert);
        Button BidSubmit = (Button) view.findViewById(R.id.bid_submit_alert);

        BidTitle.setText("PLease Enter BID (MAX BID PRICE) $"+savedPrice);
        BidSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BidAccessor bidAccessor = new BidAccessor();
                bidAlertDialog().dismiss();
            }
        });

        return ad;

    }

    private Calendar fillCalendar(Date d, int addHour, int addMin){
        String items[] = d.toString().split(" "); // Fri Nov 18 14:00:51 CST 2016
        int year = Integer.parseInt(items[5]);
        int month = FrontEndSupport.convertMonth(items[1]);
        int day = Integer.parseInt(items[2]);
        String times[] = items[3].split(":");
        int hour = Integer.parseInt(times[0]);
        int min = Integer.parseInt(times[1]);
        int sec = Integer.parseInt(times[2]);

        hour = hour + addHour;
        min = min + addMin;

        return new GregorianCalendar(year, month, day, hour, min, sec);
    }

    private void setTimePickerAndCalendarDate(TimePicker tp, CalendarView cv){
        // format: "Wed. Oct 26 2016, CDT 9:08 PM"
        String formattedDate = FrontEndSupport.getFormattedTime(savedDate.toString());
        String[] items = formattedDate.split(" ");
        String time = items[5];
        items = time.split(":");
        tp.setHour(Integer.parseInt(items[0]));
        tp.setMinute(Integer.parseInt(items[1]));
        cv.setDate(savedDate.getTime());
    }

    private AlertDialog createTimeDialog() {
        final AlertDialog ad = new AlertDialog.Builder(getContext()).create();
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        ad.setTitle("Choose a time");
        ad.setMessage("When should your task expire?");
        View view = inflater.inflate(R.layout.alertdialog_datesetter, null);
        ad.setView(view);
        final TimePicker tp = (TimePicker) view.findViewById(R.id.timePicker);
        final CalendarView cv = (CalendarView) view.findViewById(R.id.calendarView);
        setTimePickerAndCalendarDate(tp, cv);
        ad.setButton(AlertDialog.BUTTON_NEUTRAL, "Choose", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Date d = new Date(cv.getDate());
                Calendar cal = fillCalendar(d, tp.getHour(), tp.getMinute());
                savedDate = cal.getTime();
                tskdate.setText(FrontEndSupport.getFormattedTime(savedDate.toString()));
            }
        });

        return ad;

    }

    private AlertDialog editAlertDialog(){
        if(resetSave){
            savedName = currentTask.getTitle();
            savedDescr = currentTask.getDescription();
            savedTags = currentTask.getTags();
            savedPrice = currentTask.getMaxPrice();
            Date d = DateTools.epochToDate(currentTask.getExpirationTime());
            savedDate = d;
            resetSave = false;
        }
        final AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ad.setTitle("Edit your task");
        ad.setMessage("Replaces your current task with new information.");
        View view = inflater.inflate(R.layout.alertdialog_taskcreator, null);
        ad.setView(view);
        final EditText tskname = (EditText)view.findViewById(R.id.editText_taskname);
        final EditText tskdescr = (EditText)view.findViewById(R.id.editText_taskdescription);
        final EditText tsktags = (EditText)view.findViewById(R.id.editText_tasktags);
        final EditText tskprice = (EditText)view.findViewById(R.id.editText_startprice);
        tskdate = (TextView)view.findViewById(R.id.label_taskEndTime);
        String tagbuilder = "";
        for(String key : savedTags.keySet()){
            tagbuilder += key + " ";
        }
        tskname.setText(savedName);
        tskdescr.setText(savedDescr);
        tsktags.setText(tagbuilder);
        if (savedPrice < 0.0)
            tskprice.setText("");
        else
            tskprice.setText(Float.toString(savedPrice));
        tskdate.setText(FrontEndSupport.getFormattedTime(savedDate.toString()));

        tskdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog ad = createTimeDialog();
                ad.show();
            }
        });

        ad.setButton(AlertDialog.BUTTON_NEUTRAL, "Finished", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /* Prepare new saved values */
                savedName = tskname.getText().toString();
                savedDescr = tskdescr.getText().toString();
                String tags = tsktags.getText().toString();
                String[] items = tags.split(" ");
                savedTags = new HashMap<String, Boolean>();
                for(String item : items){
                    savedTags.put(item, true);
                }
                if(!tskprice.getText().toString().equals("")){
                    savedPrice = Float.parseFloat(tskprice.getText().toString());
                } else{
                    savedPrice = -1;
                }

                /* Confirmation alert */
                new AlertDialog.Builder(getActivity()).setMessage("Are you sure?"+'\n'+"Your old task will be replaced by this new task, and you will lose all your bidders.")
                        .setPositiveButton("Do it", new DialogInterface.OnClickListener(){
                            // Delete old task, go back to new task page for the updated task
                            public void onClick(DialogInterface dialog, int id){
                                //TODO: perform Firebase password validation to replace the task with a new one
                                if(FrontEndSupport.taskCreateValidation(savedName, savedDescr, Float.toString(savedPrice), savedDate, getActivity())){
                                    final TaskAccessor ta = new TaskAccessor();
                                    TaskModel newTM = new TaskModel(savedName, savedDescr, savedPrice, currentUser.getUserId(), DateTools.dateToEpoch(savedDate), false, false, savedTags);
                                    ta.createTask(newTM);
                                    ta.removeTask(currentTask.getTaskId());//doesnt seem to be removing
                                    Intent intent = new Intent(getActivity(), TaskActivityII.class);
                                    intent.putExtra("task_id", newTM.getTaskId());
                                    intent.putExtra("task_status", newTM.getStatus().toString());
                                    startActivity(intent);
                                    Toast.makeText(getActivity(), "Successfully created", Toast.LENGTH_SHORT).show();
                                } else{
                                    Toast.makeText(getActivity(), "Invalid information", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener(){
                    // Go back to task page, no change
                    public void onClick(DialogInterface dialog, int id){
                        Toast.makeText(getActivity(), "Denied", Toast.LENGTH_SHORT).show();
                    }
                }).setNeutralButton("Back", new DialogInterface.OnClickListener(){
                    // Takes you back to edit dialog
                    public void onClick(DialogInterface dialog, int id){
                        AlertDialog editDialog = editAlertDialog();
                        editDialog.show();
                    }
                }).show();
            }
        });
        return ad;
    }



}
