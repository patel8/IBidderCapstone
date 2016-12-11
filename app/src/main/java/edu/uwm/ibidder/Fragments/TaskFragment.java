package edu.uwm.ibidder.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import edu.uwm.ibidder.Activities.ProfileActivity;
import edu.uwm.ibidder.Activities.UserProfileActivity;
import edu.uwm.ibidder.FrontEndSupport;
import edu.uwm.ibidder.R;
import edu.uwm.ibidder.Activities.TaskActivityII;
import edu.uwm.ibidder.dbaccess.BidAccessor;
import edu.uwm.ibidder.dbaccess.DateTools;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.BidCallbackListener;
import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.BidModel;
import edu.uwm.ibidder.dbaccess.models.TaskModel;
import edu.uwm.ibidder.dbaccess.models.UserModel;

public class TaskFragment extends Fragment {

    private final String FRAGMENT_NAME = "TaskFragment";
    private TextView taskname;
    private TextView taskdescr;
    private TextView taskowner;
    private TextView taskendtime;
    private TextView taskdate;
    private TextView tasklowbid;
    private String savedName, savedDescr;
    private Date savedDate;
    private HashMap<String, String> savedTags;
    private float savedPrice;
    private boolean resetSave = false;
    private UserModel taskOwner;
    private TaskModel currentTask;
    private LinearLayout userProfileLayout;
    private float lowestBid = Float.MAX_VALUE;
    private String bidderID;
    private String bidIDToUpdate;
    private boolean needsUpdate = false;

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
        bidderID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userProfileLayout = (LinearLayout) v.findViewById(R.id.userProfileLayout);


        Log.i("TAG", "onCreate: "+status.toString());
        TaskAccessor ta = new TaskAccessor();
        ta.getTask(taskid, new TaskCallbackListener(status) {
            @Override
            public void dataUpdate(TaskModel tm) {
                savedName = tm.getTitle();
                savedDescr = tm.getDescription();
                savedTags = tm.getTags();
                savedPrice = tm.getMaxPrice();
                taskname.setText(savedName);
                taskdescr.setText(savedDescr);
                currentTask = tm;
                BidAccessor ba = new BidAccessor();
                ba.getTaskBids(tm.getTaskId(), new BidCallbackListener() {
                    @Override
                    public void dataUpdate(BidModel bm) {
                        if(bm.getBidValue() < lowestBid){
                            lowestBid = bm.getBidValue();
                            tasklowbid.setText("$" + Float.toString(lowestBid));
                        }
                        if(bm.getBidderId().equals(bidderID)){
                            bidIDToUpdate = bm.getBidId();
                            needsUpdate = true;
                        }
                    }
                });
                Date d = DateTools.epochToDate(tm.getExpirationTime());
                savedDate = d;
                taskendtime.setText(FrontEndSupport.getFormattedTime(d.toString()));
                UserAccessor ua = new UserAccessor();
                ua.getUser(tm.getOwnerId(), new UserCallbackListener() {
                    @Override
                    public void dataUpdate(UserModel um) {
                        taskowner.setText(um.getFirstName());
                        taskOwner = um;
                    }
                });

            }
        });
        userProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), UserProfileActivity.class).putExtra("UserID", taskOwner.getUserId()));
            }
        });



        return v;
    }

    private void initializeAllWidgets(View v){
        taskname = (TextView)v.findViewById(R.id.label_taskActName);
        taskdescr = (TextView)v.findViewById(R.id.label_taskActDescr);
        taskowner = (TextView)v.findViewById(R.id.label_taskActPoster);
        taskendtime = (TextView)v.findViewById(R.id.label_taskActEndtime);
        tasklowbid = (TextView)v.findViewById(R.id.label_taskActLowestBid);
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
                if(!currentTask.getIsTaskItNow()) {
                    AlertDialog bidDialog = bidAlertDialog();
                    bidDialog.show();
                    return true;
                } else {
                    AlertDialog taskNowDialog = taskNowDialog();
                    taskNowDialog.show();
                    return true;
                }
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private AlertDialog taskNowDialog(){
        final AlertDialog ad = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ad.setTitle("Task it now!");
        View view = inflater.inflate(R.layout.alertdialog_taskitnow, null);
        ad.setView(view);
        ad.setMessage("Would you like this task for $" + currentTask.getMaxPrice() + "?");
        Button taskAccept = (Button)view.findViewById(R.id.taskitnow_accept);
        Button taskDecline = (Button)view.findViewById(R.id.taskitnow_decline);
        taskAccept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                BidAccessor ba = new BidAccessor();
                BidModel newBid = new BidModel();
                newBid.setBidderId(bidderID);
                newBid.setTaskId(currentTask.getTaskId());
                newBid.setBidValue(currentTask.getMaxPrice());
                currentTask.setExpirationTime(0l);
                ba.createBid(newBid);
                Toast.makeText(getContext(), "Task accepted for $" + currentTask.getMaxPrice(), Toast.LENGTH_SHORT).show();
                ad.dismiss();
            }
        });
        taskDecline.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ad.dismiss();
            }
        });
        return ad;
    }

    private AlertDialog bidAlertDialog() {
        final AlertDialog ad = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ad.setTitle("Place BID");
        View view = inflater.inflate(R.layout.alert_dialog_place_bid, null);
        ad.setView(view);

        TextView bidTitle = (TextView) view.findViewById(R.id.bid_title_alert);
        final EditText bidAmount = (EditText) view.findViewById(R.id.bid_amount_alert);
        Button bidSubmit = (Button) view.findViewById(R.id.bid_submit_alert);

        bidTitle.setText("Please Enter BID (MAX BID PRICE) $" + savedPrice);
        bidSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!bidAmount.getText().toString().isEmpty()){
                    final BidAccessor bidAccessor = new BidAccessor();
                    final BidModel newBid = new BidModel();
                    newBid.setBidderId(bidderID);
                    newBid.setTaskId(currentTask.getTaskId());
                    newBid.setBidValue(Float.parseFloat(bidAmount.getText().toString()));

                    if(newBid.getBidValue() > currentTask.getMaxPrice())
                    {
                        Toast.makeText(getContext(), "Cannot place Bid higher than Max Price", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        if (needsUpdate) {
                            newBid.setBidId(bidIDToUpdate);
                            bidAccessor.updateBid(bidIDToUpdate, newBid);
                            //Log.i("TAG", "----------onClick: just updated a bid");

                        } else {
                            bidAccessor.createBid(newBid);
                            bidIDToUpdate = newBid.getBidId();
                            needsUpdate = true;
                            //Log.i("TAG", "----------onClick: just created a bid");
                        }

                        if (lowestBid > newBid.getBidValue()) {
                            lowestBid = newBid.getBidValue();
                            tasklowbid.setText("$" + Float.toString(lowestBid));
                        }

                        Toast.makeText(getContext(), "Bid placed: $" + bidAmount.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                ad.dismiss();
            }
        });

        return ad;

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
        final Calendar calendar = Calendar.getInstance();

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
            }
        });

        Button chooseButton = (Button) view.findViewById(R.id.buttonPickTimeDate);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date d = new Date(cv.getDate());
                Calendar cal = FrontEndSupport.fillCalendar(d, tp.getHour(), tp.getMinute());
                savedDate = cal.getTime();
                taskdate.setText(FrontEndSupport.getFormattedTime(savedDate.toString()));
                ad.dismiss();
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
        taskdate = (TextView)view.findViewById(R.id.label_taskEndTime);
        String tagbuilder = "";
        for(String key : savedTags.keySet()){
            String tag = savedTags.get(key);
            tagbuilder += tag + " ";
        }
        tskname.setText(savedName);
        tskdescr.setText(savedDescr);
        tsktags.setText(tagbuilder);
        if (savedPrice < 0.0)
            tskprice.setText("");
        else
            tskprice.setText(Float.toString(savedPrice));
        taskdate.setText(FrontEndSupport.getFormattedTime(savedDate.toString()));

        taskdate.setOnClickListener(new View.OnClickListener() {
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
                savedTags = new HashMap<String, String>();
                int i = 0;
                for(String item : items){
                    savedTags.put(Integer.toString(i) + "x", item);
                    i++;
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
                                if(FrontEndSupport.taskCreateValidation(savedName, savedDescr, Float.toString(savedPrice), savedDate, getActivity())){
                                    final TaskAccessor ta = new TaskAccessor();
                                    TaskModel newTM = new TaskModel(savedName, savedDescr, savedPrice, taskOwner.getUserId(), DateTools.dateToEpoch(savedDate), false, false, savedTags);
                                    ta.createTask(newTM);
                                    ta.removeTask(currentTask.getTaskId());
                                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
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
