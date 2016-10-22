package edu.uwm.ibidder.Fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import edu.uwm.ibidder.R;
import edu.uwm.ibidder.TaskActivity;
import edu.uwm.ibidder.dbaccess.ListAdapter;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.models.TaskModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class creator_task_in_auction extends Fragment {


    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;

    public creator_task_in_auction(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.tasks_in_auction);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_creator_task_in_auction, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_creator_task_in_auction);
        listView = (ListView)v.findViewById(R.id.creator_task_in_auction_listView);

        swipeRefreshLayout.setColorSchemeColors(Color.GREEN, Color.RED);


        final ListAdapter adapter = new ListAdapter(getContext(), R.layout.bidder_current_task_list_template);
        listView.setAdapter(adapter);

        TaskAccessor ta = new TaskAccessor();
        ta.getTasksOnce(new TaskCallbackListener(TaskModel.TaskStatusType.READY) {
            @Override
            public void dataUpdate(TaskModel tm) {
                adapter.addTask(tm);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);

                //Todo: Whatever we want to do when we refresh the screen.
                Toast.makeText(getContext(), "ListView has been Refreshed", Toast.LENGTH_SHORT).show();
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
