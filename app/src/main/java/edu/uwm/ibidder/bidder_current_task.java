package edu.uwm.ibidder;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import edu.uwm.ibidder.dbaccess.ListAdapter;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.models.TaskModel;

public class bidder_current_task extends android.support.v4.app.Fragment {


    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bidder_current_task, container, false);
        listView = (ListView)v.findViewById(R.id.bidder_current_task_listView);

        final ListAdapter adapter = new ListAdapter(getContext(), R.layout.bidder_current_task_list_template);
        listView.setAdapter(adapter);

        TaskAccessor ta = new TaskAccessor();
        ta.getTasksOnce(new TaskCallbackListener() {
            @Override
            public void dataUpdate(TaskModel tm) {
                adapter.addTask(tm);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                TaskModel task = (TaskModel) o;
                Intent taskIntent = new Intent(getActivity(), TaskActivity.class);
                taskIntent.putExtra("task_desc", task.getDescription());
                taskIntent.putExtra("task_own", task.getOwnerId());
                startActivity(taskIntent);
            }
        });

        return v;
    }


}
