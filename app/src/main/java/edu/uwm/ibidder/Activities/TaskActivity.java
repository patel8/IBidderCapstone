package edu.uwm.ibidder.Activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.HashMap;

import edu.uwm.ibidder.FrontEndSupport;
import edu.uwm.ibidder.R;
import edu.uwm.ibidder.dbaccess.DateTools;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.TaskModel;
import edu.uwm.ibidder.dbaccess.models.UserModel;

public class TaskActivity extends AppCompatActivity {

    TextView taskname;
    TextView taskdescr;
    TextView taskowner;
    TextView taskendtime;
    EditText userbid;
    Button submitbid;
    MenuItem item;
    Boolean isOwner = false;
    String savedName, savedDescr, savedDateLabel;
    Date savedDate;
    HashMap<String, Boolean> savedTags;
    float savedPrice;
    UserModel currentUser;
    TaskModel currentTask;
    boolean resetSave = false;
    TextView tskdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        initializeAllWidgets();
        Intent intent = getIntent();
        String taskid = intent.getStringExtra("task_id");
        String taskstatus = intent.getStringExtra("task_status");
        TaskModel.TaskStatusType status = getStatus(taskstatus);

        Log.i("TAG", "onCreate: "+status.toString());
        TaskAccessor ta = new TaskAccessor();
        ta.getTask(taskid, new TaskCallbackListener(status) {
            @Override
            public void dataUpdate(TaskModel tm) {
                savedName = tm.getTitle();
                savedDescr = tm.getDescription();
                savedTags = tm.getTags();
                taskname.setText("Task: " + savedName);
                taskdescr.setText("Description: " + savedDescr);
                currentTask = tm;

                Date d = DateTools.epochToDate(tm.getExpirationTime());
                taskendtime.setText("Expires on: " + FrontEndSupport.getFormattedTime(d.toString()));
                UserAccessor ua = new UserAccessor();
                ua.getUser(tm.getOwnerId(), new UserCallbackListener() {
                    @Override
                    public void dataUpdate(UserModel um) {
                        taskowner.setText(um.getFirstName());
                        currentUser = um;
                    }
                });

                if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(tm.getOwnerId())){
                    isOwner = true;
                }else
                {
                    isOwner = false;
                }
            }
        });
    }

    private TaskModel.TaskStatusType getStatus(String status){
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

    private void initializeAllWidgets(){
        taskname = (TextView)findViewById(R.id.label_taskActName);
        taskdescr = (TextView)findViewById(R.id.label_taskActDescr);
        taskowner = (TextView)findViewById(R.id.label_taskActPoster);
        taskendtime = (TextView)findViewById(R.id.label_taskActEndtime);
//        userbid = (EditText)findViewById(R.id.editText_taskActUserBid);
//        userbid.setOnTouchListener(new View.OnTouchListener(){
//            @Override
//            public boolean onTouch(View v, MotionEvent event){
//                v.setFocusable(true);
//                v.setFocusableInTouchMode(true);
//                return false;
//            }
//        });
//        submitbid = (Button)findViewById(R.id.button_taskActBidSubmit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_menu, menu);
        item = menu.findItem(R.id.edit_task_menu);
        // Menu only visible to the creator of the task
        item.setVisible(isOwner);
        return true;
    }

    private void setTimePickerAndCalendarDate(TimePicker tp, CalendarView cv){
        // gross hack
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
        final AlertDialog ad = new AlertDialog.Builder(this).create();
        final LayoutInflater inflater = this.getLayoutInflater();
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
                d.setHours(tp.getHour());
                d.setMinutes(tp.getMinute());
                savedDate = d;
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
        final AlertDialog ad = new AlertDialog.Builder(TaskActivity.this).create();
        LayoutInflater inflater = TaskActivity.this.getLayoutInflater();
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
                new AlertDialog.Builder(TaskActivity.this).setMessage("Are you sure?"+'\n'+"Your old task will be replaced by this new task, and you will lose all your bidders.")
                        .setPositiveButton("Do it", new DialogInterface.OnClickListener(){
                    // Delete old task, go back to new task page for the updated task
                    public void onClick(DialogInterface dialog, int id){
                        //TODO: perform Firebase password validation to replace the task with a new one
                        if(FrontEndSupport.taskCreateValidation(savedName, savedDescr, Float.toString(savedPrice), savedDate, TaskActivity.this)){
                            final TaskAccessor ta = new TaskAccessor();
                            TaskModel newTM = new TaskModel(savedName, savedDescr, savedPrice, currentUser.getUserId(), DateTools.dateToEpoch(savedDate), false, false, savedTags);
                            ta.createTask(newTM);
                            ta.removeTask(currentTask.getTaskId());//doesnt seem to be removing
                            Intent intent = new Intent(TaskActivity.this, TaskActivity.class);
                            intent.putExtra("task_id", newTM.getTaskId());
                            intent.putExtra("task_status", newTM.getStatus().toString());
                            startActivity(intent);
                            Toast.makeText(TaskActivity.this, "Successfully created", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(TaskActivity.this, "Invalid information", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener(){
                    // Go back to task page, no change
                    public void onClick(DialogInterface dialog, int id){
                        Toast.makeText(TaskActivity.this, "Denied", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.edit_task_menu:
                resetSave = true;
                AlertDialog editDialog = editAlertDialog();
                editDialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}