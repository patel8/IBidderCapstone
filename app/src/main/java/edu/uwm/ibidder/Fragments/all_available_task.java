package edu.uwm.ibidder.Fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.util.GeoUtils;
import com.github.aakira.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
    Button toggleFilterButton;
    Button applyFilterChangesButton;
    CheckBox nonLocalCheckbox;
    CheckBox localCheckbox;
    TextView searchTagsText;
    ExpandableLayout expandableFilterLayout;
    final ArrayList<TaskModel> taskList = new ArrayList<TaskModel>();

    final SortedMap<Double, TaskModel> tasksWithLocationMap = new TreeMap<Double, TaskModel>();
    final ArrayList<TaskModel> nonLocalTasksList = new ArrayList<TaskModel>();

    final ArrayList<String> searchTags = new ArrayList<String>();

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
        toggleFilterButton = (Button) v.findViewById(R.id.toggleFilter);
        expandableFilterLayout = (ExpandableLayout) v.findViewById(R.id.expandableFilterLayout);
        applyFilterChangesButton = (Button) v.findViewById(R.id.applyFilterChangesButton);
        nonLocalCheckbox = (CheckBox) v.findViewById(R.id.nonLocalCheckbox);
        localCheckbox = (CheckBox) v.findViewById(R.id.showLocalCheckbox);
        searchTagsText = (TextView) v.findViewById(R.id.searchTagsText);

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
                final GeoLocation userLocation = new GeoLocation(lat, longi);

                TaskAccessor taskAccessor = new TaskAccessor();
                taskAccessor.getTasksOnce(new TaskCallbackListener(TaskModel.TaskStatusType.READY, searchTags) {
                    @Override
                    public void dataWithLocationUpdate(TaskModel task, GeoLocation location) {
                        tasksWithLocationMap.put(GeoUtils.distance(userLocation, location), task);

                        updateTaskList();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, lat, longi, 5.0);
            }
        };

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTasks(locationService);
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

        toggleFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableFilterLayout.toggle();
            }
        });

        applyFilterChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableFilterLayout.collapse();
                refreshTasks(locationService);
            }
        });

        refreshTasks(locationService);

        return v;
    }

    private void refreshTasks(final LocationService locationService) {
        swipeRefreshLayout.setRefreshing(true);
        tasksWithLocationMap.clear();
        nonLocalTasksList.clear();

        String tagText = searchTagsText.getText().toString();
        searchTags.clear();

        if (tagText.length() > 0) {
            String[] tags = tagText.split(" ");
            searchTags.addAll(Arrays.asList(tags));
        }

        if (nonLocalCheckbox.isChecked())
            nonLocalTaskUpdate();

        if (localCheckbox.isChecked())
            locationService.updateLocation();
    }

    private void nonLocalTaskUpdate() {
        new TaskAccessor().getNonLocalTasksOnce(new TaskCallbackListener(TaskModel.TaskStatusType.READY, searchTags) {
            @Override
            public void dataUpdate(TaskModel tm) {
                nonLocalTasksList.add(tm);

                updateTaskList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateTaskList() {
        taskList.clear();

        ArrayList<Double> newDistances = new ArrayList<Double>();

        //put non local tasks first
        for (TaskModel t : nonLocalTasksList) {
            taskList.add(t);
            //We assume non local tasks have a distance of -1.0
            newDistances.add(-1.0);
        }

        for (Map.Entry<Double, TaskModel> entry : tasksWithLocationMap.entrySet()) {
            taskList.add(entry.getValue());
            //You can get the distance of a local task by calling entry.getKey() here.
            newDistances.add(entry.getKey());
        }

        recyclerAdapter.setDistances(newDistances);

        recyclerAdapter.notifyDataSetChanged();
    }

}
