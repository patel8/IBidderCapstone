package edu.uwm.ibidder.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.uwm.ibidder.Adapters.RecyclerAdapter;
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

    public all_available_task() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_all_available_task, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.available_task_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final ArrayList<TaskModel> list = new ArrayList<TaskModel>();

        TaskAccessor taskAccessor = new TaskAccessor();
        taskAccessor.getTasksOnce(new TaskCallbackListener(TaskModel.TaskStatusType.READY) {
            @Override
            public void dataUpdate(TaskModel tm) {
                list.add(tm);
                recyclerAdapter.notifyDataSetChanged();
            }
        });


        recyclerAdapter = new RecyclerAdapter(list);
        recyclerView.setAdapter(recyclerAdapter);
        return v;
    }

}
