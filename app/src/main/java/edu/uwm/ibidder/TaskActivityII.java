package edu.uwm.ibidder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;

import edu.uwm.ibidder.Fragments.BidderListFragment;
import edu.uwm.ibidder.Fragments.TaskFragment;
import edu.uwm.ibidder.dbaccess.DateTools;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.TaskModel;
import edu.uwm.ibidder.dbaccess.models.UserModel;
public class TaskActivityII extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    MenuItem item;
    String TaskID;
    String TaskStatus;

    public String getTaskID()
    {
        return TaskID;
    }
    public String getTaskStatus(){
        return TaskStatus;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_ii);
        TaskID = getIntent().getStringExtra("task_id");
        TaskStatus = getIntent().getStringExtra("task_status");


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new CustomAdapter(getSupportFragmentManager(),getApplicationContext()));

        tabLayout = (TabLayout) findViewById(R.id.taskTabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });


    }

    public void EditMenuVisible(boolean b) {
        item.setVisible(b);
    }

    private class CustomAdapter extends FragmentPagerAdapter {

        private String fragments [] = {"Task Information","List of Bidder"};

        public CustomAdapter(FragmentManager supportFragmentManager, Context applicationContext) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new TaskFragment();
                case 1:
                    return new BidderListFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments[position];
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_menu, menu);
        item = menu.findItem(R.id.edit_task_menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_task_menu:
                return false;
            case R.id.place_bid_menu:
                //Make AlertDialog to get Input.
                AlertDialog dialog = BidPlaceDialog();
                dialog.show();
                return true;
            default:
                break;
        }
        return false;
    }

    private AlertDialog BidPlaceDialog() {
        final AlertDialog ad = new AlertDialog.Builder(TaskActivityII.this).create();
        LayoutInflater inflater = TaskActivityII.this.getLayoutInflater();
        ad.setTitle("Place BID");
        View view = inflater.inflate(R.layout.alert_dialog_place_bid, null);
        EditText bidAmt = (EditText) view.findViewById(R.id.editText_alert_dialog_place_bid);
        Button button = (Button) view.findViewById(R.id.button_alert_dialog_place_bid);
        ad.setView(view);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Place a bid on Task
            }
        });

        return ad;
    }
}
