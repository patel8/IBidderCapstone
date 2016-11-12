package edu.uwm.ibidder.Fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.uwm.ibidder.Activities.TaskActivityII;
import edu.uwm.ibidder.Adapters.RecyclerAdapter;
import edu.uwm.ibidder.DividerItemDecoration;
import edu.uwm.ibidder.ItemClickSupport;
import edu.uwm.ibidder.Location.LocationService;
import edu.uwm.ibidder.R;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.models.TaskModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class all_available_task extends Fragment {


    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    final ArrayList<TaskModel> taskList = new ArrayList<TaskModel>();

    public all_available_task() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_all_available_task, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.fragment_all_available_swipeRefresh);
        swipeRefreshLayout.setColorSchemeColors(Color.GREEN, Color.RED);
        recyclerView = (RecyclerView) v.findViewById(R.id.available_task_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity()));

        recyclerAdapter = new RecyclerAdapter(taskList);
        recyclerView.setAdapter(recyclerAdapter);

        final LocationService locationService = new LocationService(getContext()) {
            @Override
            public void getCoordinates(double lat, double longi) {
                swipeRefreshLayout.setRefreshing(true);
                TaskAccessor taskAccessor = new TaskAccessor();
                taskAccessor.getTasksOnce(new TaskCallbackListener(TaskModel.TaskStatusType.READY) {
                    @Override
                    public void dataUpdate(TaskModel tm) {
                        taskList.add(tm);
                        recyclerAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, lat, longi, 5.0);
            }
        };

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                locationService.updateLocation();
            }
        });

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView rv, int position, View v) {
                TaskModel tm = taskList.get(position);
                Intent intent = new Intent(getContext(), TaskActivityII.class);
                intent.putExtra("task_id", tm.getTaskId());
                intent.putExtra("task_status", tm.getStatus());
                startActivity(intent);
            }
        });

        locationService.updateLocation();

        return v;
    }

}
