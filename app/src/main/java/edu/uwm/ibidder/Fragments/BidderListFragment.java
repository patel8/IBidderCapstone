package edu.uwm.ibidder.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;

import java.util.Calendar;
import java.util.Date;

import edu.uwm.ibidder.Activities.ProfileActivity;
import edu.uwm.ibidder.Activities.UserProfileActivity;
import edu.uwm.ibidder.DividerItemDecoration;
import edu.uwm.ibidder.FrontEndSupport;
import edu.uwm.ibidder.ItemClickSupport;
import edu.uwm.ibidder.R;
import edu.uwm.ibidder.Activities.TaskActivityII;
import edu.uwm.ibidder.dbaccess.BidAccessor;
import edu.uwm.ibidder.dbaccess.ReviewAccessor;
import edu.uwm.ibidder.dbaccess.TaskWinnerAccessor;
import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.BidModel;
import edu.uwm.ibidder.dbaccess.models.TaskWinnerModel;
import edu.uwm.ibidder.dbaccess.models.UserModel;
import edu.uwm.ibidder.ItemClickSupport;

/**
 * A simple {@link Fragment} subclass.
 */
public class BidderListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<BidModel, BidderListHolder> adapter;
    private boolean supportPickingBidder;

    public BidderListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        supportPickingBidder = getActivity().getIntent().getBooleanExtra("PickBidder",false);
        View v =  inflater.inflate(R.layout.fragment_bidder_list, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.bidderListFragmentRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        recyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity()));
        BidAccessor bidAccessor = new BidAccessor();
        Query q = bidAccessor.getTaskBidsQuery(((TaskActivityII)getActivity()).getTaskID());
        adapter = new FirebaseRecyclerAdapter<BidModel, BidderListHolder>(
                BidModel.class,
                R.layout.bidder_list_template,
                BidderListHolder.class,
                q
        ) {
            @Override
            protected void populateViewHolder(final BidderListHolder viewHolder, BidModel model, int position) {
                UserAccessor userAccessor = new UserAccessor();
                userAccessor.getUser(model.getBidderId(), new UserCallbackListener() {
                    @Override
                    public void dataUpdate(UserModel um) {
                        viewHolder.userName.setText(um.getFirstName());
                        ReviewAccessor reviewAccessor = new ReviewAccessor();
                        reviewAccessor.getReviewsByUserIdQuery(um.getUserId());
                    }
                });
                viewHolder.userBid.setText("$ "+model.getBidValue()+"");
            }
        };

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView rv, int position, View v) {
                BidModel tm = adapter.getItem(position);
                //Todo: What to do when user clicks on 'Bidder's List Item'. This should only be the case when they click on when their Parent Activity is 'Pick Task Winner'

                startActivity(new Intent(getContext(), UserProfileActivity.class).putExtra("UserID", tm.getBidderId()));
            }
        });

        if(supportPickingBidder)
        {
            ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClicked(RecyclerView rv, final int postion, View v) {


                    final BidModel bidModel = adapter.getItem(postion);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure that you want to pick this bidder for your task?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                        TaskWinnerAccessor taskWinnerAccessor = new TaskWinnerAccessor();

                            TaskWinnerModel taskWinnerModel = new TaskWinnerModel();
                            taskWinnerModel.setWinnerId(bidModel.getBidderId());
                            taskWinnerModel.setTaskId(bidModel.getTaskId());
                            taskWinnerModel.setTaskOwnerId(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            Toast.makeText(getContext(), "Winner has been picked", Toast.LENGTH_SHORT).show();
                            taskWinnerAccessor.CreateTaskWinner(taskWinnerModel);
                            dialog.dismiss();
                            startActivity(new Intent(getActivity(), ProfileActivity.class));

                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                }
            });

        }


        recyclerView.setAdapter(adapter);

        return v;
    }


    public static class BidderListHolder extends RecyclerView.ViewHolder{
        public TextView userName;
        public TextView userBid;
        public RatingBar userRating;

        public BidderListHolder(View v){
            super(v);
            userName = (TextView) v.findViewById(R.id.bidder_list_bidder_name);
            userBid = (TextView) v.findViewById(R.id.bidder_list_bid_amount);
            userRating = (RatingBar) v.findViewById(R.id.bidder_list_bidder_rating);
        }

    }

}
