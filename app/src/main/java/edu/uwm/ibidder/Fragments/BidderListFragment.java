package edu.uwm.ibidder.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;

import edu.uwm.ibidder.DividerItemDecoration;
import edu.uwm.ibidder.R;
import edu.uwm.ibidder.dbaccess.BidAccessor;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.listeners.BidCallbackListener;
import edu.uwm.ibidder.dbaccess.models.BidModel;
import edu.uwm.ibidder.dbaccess.models.TaskModel;
import edu.uwm.ibidder.dbaccess.models.UserModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class BidderListFragment extends Fragment {


    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<UserModel, viewHolder> adapter;
    public BidderListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_bidder_list, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.bidder_won_tasks_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity()));
        TaskAccessor ta = new TaskAccessor();
        Query q = ta.getTasksByOwnerIdQuery(FirebaseAuth.getInstance().getCurrentUser().getUid()
                , TaskModel.TaskStatusType.ACCEPTED.toString());
        adapter = new FirebaseRecyclerAdapter<UserModel, viewHolder>(
                UserModel.class,
                R.layout.bidder_current_task_list_template,
               viewHolder.class,
                q
        ) {
            @Override
            protected void populateViewHolder(viewHolder viewHolder, UserModel model, int position) {
                viewHolder.title.setText(model.getFirstName());
                viewHolder.description.setText(model.getLastName());
                viewHolder.DateTime.setText(model.getEmail() + "");
                viewHolder.Price.setText(model.getPhoneNumber() + "");
            }

        };


        return v;
    }

}
