package edu.uwm.ibidder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

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
    Boolean editTask = false;

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
                taskname.setText("Task: " + tm.getTitle());
                taskdescr.setText("Description: " + tm.getDescription());
                Date d = DateTools.epochToDate(tm.getExpirationTime());
                taskendtime.setText("Expires on: " + ProfileActivity.getFormattedTime(d.toString()));
                UserAccessor ua = new UserAccessor();
                ua.getUser(tm.getOwnerId(), new UserCallbackListener() {
                    @Override
                    public void dataUpdate(UserModel um) {
                        taskowner.setText(um.getFirstName());
                    }
                });

                if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(tm.getOwnerId())){
                    editTask = true;
                }else
                {
                    editTask = false;
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
        // TODO: edit.onclick

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_menu, menu);
        MenuItem item = menu.findItem(R.id.edit_task_menu);
        item.setVisible(editTask);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.edit_task_menu:
                //Todo - Do whatever we want when user clicks on Edit Task Button
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
