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
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;

import edu.uwm.ibidder.Activities.TaskActivityII;
import edu.uwm.ibidder.DividerItemDecoration;
import edu.uwm.ibidder.ItemClickSupport;
import edu.uwm.ibidder.R;
import edu.uwm.ibidder.dbaccess.ReviewAccessor;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.ReviewModel;
import edu.uwm.ibidder.dbaccess.models.TaskModel;
import edu.uwm.ibidder.dbaccess.models.UserModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment {
    RecyclerView recyclerView;
    String userId;
    FirebaseRecyclerAdapter<ReviewModel, ReviewFragmentHolder> adapter;

    public ReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment View v = inflater.inflate(R.layout.fragment_creator_task_in_auction, container, false);
        View v = inflater.inflate(R.layout.fragment_review, container, false);
        userId = getActivity().getIntent().getStringExtra("UserID");
        recyclerView = (RecyclerView) v.findViewById(R.id.review_fragment_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity()));


        ReviewAccessor reviewAccessor = new ReviewAccessor();
        Query q = reviewAccessor.getReviewsByUserIdQuery(userId);
        adapter = new FirebaseRecyclerAdapter<ReviewModel, ReviewFragmentHolder>(
                ReviewModel.class,
                R.layout.review_list_template,
                ReviewFragmentHolder.class,
                q
        ) {
            @Override
            protected void populateViewHolder(final ReviewFragmentHolder viewHolder, final ReviewModel reviewModel, int i) {

                UserAccessor userAccessor = new UserAccessor();
                userAccessor.getUser(reviewModel.getReviewWriterId(), new UserCallbackListener() {
                    @Override
                    public void dataUpdate(UserModel um) {
                        viewHolder.userName.setText(um.getFirstName()+" "+um.getLastName());
                                            }
                });
                viewHolder.ratingBar.setRating(reviewModel.getReviewScore());
                viewHolder.Description.setText(reviewModel.getReviewText());

            }
        };

        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView rv, int position, View v) {

            }
        });

        return v;
    }

    public static class ReviewFragmentHolder extends RecyclerView.ViewHolder{
         public TextView userName;
         public  TextView Description;
         public RatingBar ratingBar;
        public ReviewFragmentHolder(View v){
            super(v);
            userName = (TextView) v.findViewById(R.id.textViewReviewUserName);
            Description = (TextView) v.findViewById(R.id.textViewReviewDescription);
            ratingBar = (RatingBar) v.findViewById(R.id.ratingBarReviewRating);
        }
    }

}
