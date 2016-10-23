package edu.uwm.ibidder;

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

public class TaskActivity extends AppCompatActivity {

    TextView taskname;
    TextView taskdescr;
    TextView taskowner;
    TextView taskendtime;
    EditText userbid;
    Button submitbid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        initializeAllWidgets();
        Intent intent = getIntent();
        String tskDesc = intent.getStringExtra("task_desc");
        String tskOwn = intent.getStringExtra("task_own");
        String tskName = intent.getStringExtra("task_name");
        String tskEnd = intent.getStringExtra("task_end");
        String tskPrice = intent.getStringExtra("task_price");
        taskname.setText(tskName);
        taskdescr.setText(tskDesc);
        taskowner.setText(tskOwn);
        taskendtime.setText(tskEnd);
        Log.i("TAG", "---------------onCreate: endtime: " + tskEnd + " taskprice: " + tskPrice);

        //Toast.makeText(TaskActivity.this, "Task: " + taskDesc + " Owner: " + taskOwn, Toast.LENGTH_SHORT).show();
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
