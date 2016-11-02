package edu.uwm.ibidder.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Date;

import edu.uwm.ibidder.Adapters.RecyclerAdapter;
import edu.uwm.ibidder.DividerItemDecoration;
import edu.uwm.ibidder.ItemClickSupport;
import edu.uwm.ibidder.ProfileActivity;
import edu.uwm.ibidder.R;
import edu.uwm.ibidder.TaskActivity;
import edu.uwm.ibidder.TaskActivityII;
import edu.uwm.ibidder.dbaccess.DateTools;
import edu.uwm.ibidder.dbaccess.ListAdapter;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.TaskModel;
import edu.uwm.ibidder.dbaccess.models.UserModel;

public class bidder_current_task extends android.support.v4.app.Fragment {


    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerAdapter recyclerAdapter;

    public bidder_current_task() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.asbidder_active);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bidder_current_task, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_bidder_current_task);
        recyclerView = (RecyclerView) v.findViewById(R.id.bidder_current_task_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout.setColorSchemeColors(Color.GREEN, Color.RED);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity()));
        final ArrayList<TaskModel> list = new ArrayList<TaskModel>();

        TaskAccessor taskAccessor = new TaskAccessor();
        taskAccessor.getTasksByBidderId(FirebaseAuth.getInstance().getCurrentUser().getUid(), new TaskCallbackListener(TaskModel.TaskStatusType.READY) {
            @Override
            public void dataUpdate(TaskModel tm) {
                list.add(tm);
                recyclerAdapter.notifyDataSetChanged();
            }
        });


        recyclerAdapter = new RecyclerAdapter(list);
        recyclerView.setAdapter(recyclerAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
                recyclerAdapter.notifyDataSetChanged();
            }
        });

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView rv, int position, View v) {
                TaskModel tm = list.get(position);
                Intent intent = new Intent(getActivity(), TaskActivityII.class);
                intent.putExtra("task_id", tm.getTaskId());
                intent.putExtra("task_status", tm.getStatus().toString());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(recyclerAdapter);

        return v;
    }

}
