package edu.uwm.ibidder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.models.TaskModel;

public class TaskActivity extends AppCompatActivity {

    TextView taskname;
    TextView taskdescr;
    TextView taskowner;
    TextView taskendtime;
    EditText userbid;
    Button submitbid;
    Fragment fragment;

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
                taskname.setText(tm.getTitle());
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
        userbid = (EditText)findViewById(R.id.editText_taskActUserBid);
        userbid.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        submitbid = (Button)findViewById(R.id.button_taskActBidSubmit);
    }


}
