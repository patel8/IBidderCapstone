package edu.uwm.ibidder.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;

import java.util.Calendar;
import java.util.Date;

import edu.uwm.ibidder.Activities.TaskActivityII;
import edu.uwm.ibidder.DividerItemDecoration;
import edu.uwm.ibidder.FrontEndSupport;
import edu.uwm.ibidder.ItemClickSupport;
import edu.uwm.ibidder.R;
import edu.uwm.ibidder.Activities.TaskActivityII;
import edu.uwm.ibidder.dbaccess.DateTools;
import edu.uwm.ibidder.dbaccess.ListAdapter;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.models.TaskModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class creator_completed_task_auctions extends Fragment {


    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseRecyclerAdapter<TaskModel, viewHolder> adapter;

    public creator_completed_task_auctions(){}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.ascreator_pickwin);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_creator_completed_task_auctions, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_creator_completed_task_auctions);
        recyclerView = (RecyclerView) v.findViewById(R.id.creator_completed_task_auctions_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout.setColorSchemeColors(Color.GREEN, Color.RED);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity()));
        TaskAccessor ta = new TaskAccessor();
        Query q = ta.getTasksByOwnerIdQuery(FirebaseAuth.getInstance().getCurrentUser().getUid()
                , TaskModel.TaskStatusType.TIMED_OUT.toString());
        adapter = new FirebaseRecyclerAdapter<TaskModel, viewHolder>(
                TaskModel.class,
                R.layout.bidder_current_task_list_template,
                viewHolder.class,
                q
        ) {
            @Override
            protected void populateViewHolder(viewHolder viewHolder, TaskModel taskModel, int i) {
                viewHolder.title.setText(taskModel.getTitle());
                viewHolder.DateTime.setText(FrontEndSupport.getFormattedTime(DateTools.epochToDate(taskModel.getExpirationTime()).toString())+"");
                viewHolder.Price.setText("$"+taskModel.getMaxPrice() + "");
                viewHolder.distance.setVisibility(View.GONE);
                viewHolder.taskItNow.setVisibility(taskModel.getIsTaskItNow()?View.VISIBLE:View.GONE);
            }
        };

        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView rv, int position, View v) {
                TaskModel tm = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), TaskActivityII.class);
                intent.putExtra("task_id", tm.getTaskId());
                intent.putExtra("task_status", tm.getStatus().toString());
                intent.putExtra("PickBidder", true);
                startActivity(intent);
            }
        });


        return v;
    }



}
