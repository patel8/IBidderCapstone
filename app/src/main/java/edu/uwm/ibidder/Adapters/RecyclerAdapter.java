package edu.uwm.ibidder.Adapters;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.uwm.ibidder.R;
import edu.uwm.ibidder.dbaccess.models.TaskModel;

/**
 * Created by Sagar on 10/27/2016.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolderForAvailTasks> {
    ArrayList<TaskModel> list;
    public RecyclerAdapter(ArrayList<TaskModel> list)
    {
            this.list = list;
    }

    @Override
    public ViewHolderForAvailTasks onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bidder_current_task_list_template, parent, false);

        return new ViewHolderForAvailTasks(itemView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolderForAvailTasks holder, int position) {
        TaskModel model = list.get(position);
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());
        holder.Price.setText(model.getMaxPrice()+"");
        holder.DateTime.setText(model.getExpirationTime()+"");
        holder.countDownTimer = new CountDownTimer(model.getExpirationTime(), 500) {
            @Override
            public void onTick(long l) {
                holder.DateTime.setText(l+"");
            }

            @Override
            public void onFinish() {

            }
        }.start();


    }

    public static class ViewHolderForAvailTasks extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView description;
        public TextView DateTime;
        public TextView Price;
        public CountDownTimer countDownTimer;

        public ViewHolderForAvailTasks(View v){
            super(v);

            title = (TextView) v.findViewById(R.id.textViewListTitle);
            description = (TextView) v.findViewById(R.id.textViewListDescription);
            DateTime = (TextView) v.findViewById(R.id.textViewListDateTime);
            Price =   (TextView) v.findViewById(R.id.textViewListPrice);

        }

    }



}
