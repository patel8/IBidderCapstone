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

 abstract class viewHolder extends RecyclerView.ViewHolder{
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
