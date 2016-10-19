package edu.uwm.ibidder.dbaccess;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

import edu.uwm.ibidder.R;
import edu.uwm.ibidder.dbaccess.models.TaskModel;

/**
 * Created by Sagar on 10/18/2016.
 */

public class ListAdapter extends BaseAdapter {

    protected Context context;
    protected static LayoutInflater layoutInflater = null;
    protected List<TaskModel> list;
    int layout;


    public ListAdapter(Context context, List<TaskModel> list, int layout)
    {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
        this.layout = layout;
    }

    public void addTask(TaskModel tm){
        list.add(tm);
        this.notifyDataSetChanged();
    }

    @Override
    public TaskModel getItem(int i) {

        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder viewHolder;
        if (v == null) {
            v = layoutInflater.inflate(layout, null);
            viewHolder = new ViewHolder();
            viewHolder.description = (TextView) v.findViewById(R.id.TestTextView);
            viewHolder.price = (TextView) v.findViewById(R.id.TestTextView2);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        viewHolder.description.setText(list.get(position).getDescription());
        viewHolder.price.setText(list.get(position).getMaxPrice()+"");

        return v;
    }

    public class ViewHolder{
        public TextView description;
        public TextView price;
    }
}
