package edu.uwm.ibidder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Toast;

public class TaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Intent intent = getIntent();
        String taskDesc = intent.getStringExtra("task_desc");
        String taskOwn = intent.getStringExtra("task_own");
        Toast.makeText(TaskActivity.this, "Task: " + taskDesc + " Owner: " + taskOwn, Toast.LENGTH_SHORT).show();
    }
}
