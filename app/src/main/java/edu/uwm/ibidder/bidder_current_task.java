package edu.uwm.ibidder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import edu.uwm.ibidder.dbaccess.ListAdapter;
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
        TaskModel task1 = new TaskModel();
        task1.setDescription("Sagar");
        task1.setMaxPrice(3243.4);
        TaskModel task2 = new TaskModel();
        task2.setDescription("TEST");
        task2.setMaxPrice(123.4);
        listView = (ListView)v.findViewById(R.id.bidder_current_task_listView);

        ArrayList<TaskModel> list = new ArrayList<>();
        list.add(task1);
        list.add(task2);

        ListAdapter adapter = new ListAdapter(getContext(), list, R.layout.bidder_current_task_list_template);
        listView.setAdapter(adapter);

        return v;
        }


}
