package edu.uwm.ibidder.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import edu.uwm.ibidder.R;
import edu.uwm.ibidder.dbaccess.DateTools;
import edu.uwm.ibidder.dbaccess.models.TaskModel;

/**
 * Created by Sagar on 10/27/2016.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolderForAvailTasks> {
    ArrayList<TaskModel> list;

    public RecyclerAdapter(ArrayList<TaskModel> list) {
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
        holder.Price.setText("$ " + model.getMaxPrice() + "");

        Date d1 = DateTools.epochToDate(model.getExpirationTime());
        Date d2 = new Date();
        long diff = d1.getTime() - d2.getTime();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);

        if (holder.countDownTimer != null)
            holder.countDownTimer.cancel();

        holder.countDownTimer = new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long l) {
                if (l <= 3600) holder.CountDown.setTextColor(Color.RED);
                holder.CountDown.setText(timeConversion((int) l / 1000));
            }

            @Override
            public void onFinish() {
            }
        }.start();

    }

    private String timeConversion(int totalSeconds) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        String result = "";
        result += (hours < 10) ? "0" : "";
        result += hours + ":";

        result += (minutes < 10) ? "0" : "";
        result += minutes + ":";

        result += (seconds < 10) ? "0" : "";
        result += seconds;

        return result;


    }

    public static class ViewHolderForAvailTasks extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public TextView Price;
        public TextView CountDown;
        public CountDownTimer countDownTimer;

        public ViewHolderForAvailTasks(View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.textViewListTitle);
            Price = (TextView) v.findViewById(R.id.textViewListPrice);
            CountDown = (TextView) v.findViewById(R.id.textViewListDateTime);

        }

    }


}
