package edu.uwm.ibidder.Adapters;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private ArrayList<TaskModel> list;
    private ArrayList<Double> distances;
    private boolean hasDistances;

    public RecyclerAdapter(ArrayList<TaskModel> list) {
        this.list = list;
        hasDistances = false;
        distances = new ArrayList<Double>();
    }

    public void setDistances(ArrayList<Double> toAdd) {
        hasDistances = true;
        distances.clear();

        for (Double x : toAdd)
            distances.add(x);
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
        holder.taskItNow.setVisibility(model.getIsTaskItNow() ? View.VISIBLE : View.GONE);

        if (hasDistances) {
            Double d = distances.get(position);
            String distanceString;

            if (d >= 0)
                distanceString = Math.round(d) + "km away";
            else
                distanceString = "Non-local task";
            holder.distance.setText(distanceString);
        }

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
        public TextView Price;
        public TextView CountDown;
        public CountDownTimer countDownTimer;
        public TextView taskItNow;
        public TextView distance;

        public ViewHolderForAvailTasks(View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.textViewListTitle);
            Price = (TextView) v.findViewById(R.id.textViewListPrice);
            CountDown = (TextView) v.findViewById(R.id.textViewListDateTime);
            taskItNow = (TextView) v.findViewById(R.id.textViewTaskItNow);
            distance = (TextView) v.findViewById(R.id.textViewListDistance);

        }

    }


}
