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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.util.GeoUtils;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.google.firebase.auth.FirebaseAuth;

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

    private final String FRAGMENT_NAME = "all_available_task";
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    Button toggleFilterButton;
    Button applyFilterChangesButton;
    CheckBox nonLocalCheckbox;
    CheckBox localCheckbox;
    TextView searchTagsText;
    NumberPicker distancePicker;
    ExpandableLayout expandableFilterLayout;
    final ArrayList<TaskModel> taskList = new ArrayList<TaskModel>();

    final SortedMap<Double, ArrayList<TaskModel>> tasksWithLocationMap = new TreeMap<Double, ArrayList<TaskModel>>();
    final ArrayList<TaskModel> nonLocalTasksList = new ArrayList<TaskModel>();

    final ArrayList<String> searchTags = new ArrayList<String>();

    LocationService disposableLocation;

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
        distancePicker = (NumberPicker) v.findViewById(R.id.distancePicker);

        recyclerView = (RecyclerView) v.findViewById(R.id.available_task_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity()));

        recyclerAdapter = new RecyclerAdapter(taskList);
        recyclerView.setAdapter(recyclerAdapter);

        distancePicker.setMaxValue(100);
        distancePicker.setMinValue(1);
        distancePicker.setValue(5);

        final LocationService locationService = new LocationService(getContext()) {
            @Override
            public void getCoordinates(double lat, double longi) {
                final GeoLocation userLocation = new GeoLocation(lat, longi);
                final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                TaskAccessor taskAccessor = new TaskAccessor();
                taskAccessor.getTasksOnce(new TaskCallbackListener(TaskModel.TaskStatusType.READY, searchTags) {
                    @Override
                    public void dataWithLocationUpdate(TaskModel task, GeoLocation location) {
                        //TODO: uncomment this code and its ending brace to prevent users from seeing their own tasks
                        //if (!task.getTaskId().equals(userId)) {

                        double distance = GeoUtils.distance(userLocation, location);
                        if (tasksWithLocationMap.get(distance) == null) {
                            ArrayList<TaskModel> newList = new ArrayList<TaskModel>();
                            newList.add(task);
                            tasksWithLocationMap.put(distance, newList);
                        } else {
                            tasksWithLocationMap.get(distance).add(task);
                        }

                        updateTaskList();
                        swipeRefreshLayout.setRefreshing(false);


                        //}
                    }
                }, lat, longi, distancePicker.getValue());
            }
        };

        disposableLocation = locationService;

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
                intent.putExtra("ShowToolBar", true);
                intent.putExtra("ShowReportTask", true);
                intent.putExtra("caller", FRAGMENT_NAME);
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

    @Override
    public void onResume() {
        super.onResume();
        startGPS();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopGPS();
    }

    @Override
    public void onStart() {
        super.onStart();
        startGPS();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopGPS();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopGPS();
    }

    private void stopGPS() {
        if (disposableLocation != null)
            disposableLocation.dispose();
    }

    private void startGPS() {
        if (disposableLocation != null)
            disposableLocation.rebuild();
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

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //hide loader if nothing is found in 5 seconds (probably no tasks)
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                    updateTaskList();
                }
            }
        }, 5000);
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
            newDistances.add(-1.0);
        }

        for (Map.Entry<Double, ArrayList<TaskModel>> listEntry : tasksWithLocationMap.entrySet()) {
            for (TaskModel entry : listEntry.getValue()) {
                taskList.add(entry);
                newDistances.add(listEntry.getKey());
            }
        }

        recyclerAdapter.setDistances(newDistances);

        recyclerAdapter.notifyDataSetChanged();
    }

}
