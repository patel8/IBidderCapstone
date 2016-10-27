package edu.uwm.ibidder.Fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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

import java.util.Date;

import edu.uwm.ibidder.DividerItemDecoration;
import edu.uwm.ibidder.ItemClickSupport;
import edu.uwm.ibidder.ProfileActivity;
import edu.uwm.ibidder.R;
import edu.uwm.ibidder.TaskActivity;
import edu.uwm.ibidder.dbaccess.DateTools;
import edu.uwm.ibidder.dbaccess.ListAdapter;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.TaskModel;
import edu.uwm.ibidder.dbaccess.models.UserModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class creator_task_history extends Fragment {


    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseRecyclerAdapter<TaskModel, bidder_bid_history.viewHolder> adapter;

    public creator_task_history()
    {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.task_history);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_creator_task_history, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_creator_task_history);
        recyclerView = (RecyclerView) v.findViewById(R.id.creator_task_history_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout.setColorSchemeColors(Color.GREEN, Color.RED);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity()));
        TaskAccessor ta = new TaskAccessor();
        Query q = ta.getTasksByOwnerIdQuery(FirebaseAuth.getInstance().getCurrentUser().getUid()
                , TaskModel.TaskStatusType.TIMED_OUT.toString());
        adapter = new FirebaseRecyclerAdapter<TaskModel, bidder_bid_history.viewHolder>(
                TaskModel.class,
                R.layout.bidder_current_task_list_template,
                bidder_bid_history.viewHolder.class,
                q
        ) {
            @Override
            protected void populateViewHolder(bidder_bid_history.viewHolder viewHolder, TaskModel taskModel, int i) {
                viewHolder.title.setText(taskModel.getTitle());
                viewHolder.description.setText(taskModel.getDescription());
                viewHolder.DateTime.setText(taskModel.getExpirationTime() + "");
                viewHolder.Price.setText(taskModel.getMaxPrice() + "");
            }
        };

        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView rv, int position, View v) {
                TaskModel tm = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), TaskActivity.class);
                intent.putExtra("task_id", tm.getTaskId());
                intent.putExtra("task_status", tm.getStatus().toString());
                startActivity(intent);
            }
        });

        return v;
    }
    public static class viewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public TextView description;
        public TextView DateTime;
        public TextView Price;

        public viewHolder(View v){
            super(v);

            title = (TextView) v.findViewById(R.id.textViewListTitle);
            description = (TextView) v.findViewById(R.id.textViewListDescription);
            DateTime = (TextView) v.findViewById(R.id.textViewListDateTime);
            Price = (TextView) v.findViewById(R.id.textViewListPrice);


        }
    }

}
