package edu.uwm.ibidder.Fragments;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import edu.uwm.ibidder.Fragments.bidder_bid_history;
import edu.uwm.ibidder.R;
import edu.uwm.ibidder.dbaccess.models.TaskModel;

/**
 * Created by Sagar on 10/25/2016.
 */

  class viewHolder extends RecyclerView.ViewHolder{
    public TextView title;
    public TextView distance;
    public TextView DateTime;
    public TextView Price;
    public TextView taskItNow;

    public viewHolder(View v){
        super(v);

        distance = (TextView) v.findViewById(R.id.textViewListDistance);
        title = (TextView) v.findViewById(R.id.textViewListTitle);
        DateTime = (TextView) v.findViewById(R.id.textViewListDateTime);
        Price = (TextView) v.findViewById(R.id.textViewListPrice);
        taskItNow = (TextView) v.findViewById(R.id.textViewTaskItNow);


    }

}
