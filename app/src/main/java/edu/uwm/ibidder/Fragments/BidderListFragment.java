package edu.uwm.ibidder.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import edu.uwm.ibidder.DividerItemDecoration;
import edu.uwm.ibidder.R;
import edu.uwm.ibidder.TaskActivityII;
import edu.uwm.ibidder.dbaccess.BidAccessor;
import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.BidModel;
import edu.uwm.ibidder.dbaccess.models.UserModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class BidderListFragment extends Fragment {


    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<BidModel, BidderListHolder> adapter;
    public BidderListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_bidder_list, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.bidderListFragmentRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity()));
        BidAccessor bidAccessor = new BidAccessor();
        Query q = bidAccessor.getTaskBidsQuery(((TaskActivityII)getActivity()).getTaskID()); //TODO: need actual taskId here
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
                    }
                });
                viewHolder.userBid.setText("$ "+model.getBidValue()+"");
            }
        };

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
