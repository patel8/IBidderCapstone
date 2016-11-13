package edu.uwm.ibidder.Activities;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import edu.uwm.ibidder.Fragments.BidderListFragment;
import edu.uwm.ibidder.Fragments.TaskFragment;
import edu.uwm.ibidder.FrontEndSupport;
import edu.uwm.ibidder.R;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.TaskModel;
import edu.uwm.ibidder.dbaccess.models.UserModel;
public class TaskActivityII extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MenuItem item;
    private String taskID;
    private String taskStatus;
    private Toolbar toolbar;
    private boolean showToolBar;
    private boolean enableEditMenu = false;

    public String getTaskID()
    {
        return taskID;
    }
    public String getTaskStatus(){
        return taskStatus;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_ii);
        taskID = getIntent().getStringExtra("task_id");
        taskStatus = getIntent().getStringExtra("task_status");
        showToolBar = getIntent().getBooleanExtra("ShowToolBar", false);

        toolbar = (Toolbar) findViewById(R.id.TaskToolBar);
        toolbar.setVisibility(showToolBar? View.VISIBLE: View.GONE);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new CustomAdapter(getSupportFragmentManager(), getApplicationContext()));

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

        TaskAccessor taskAccessor = new TaskAccessor();
        taskAccessor.getTaskOnce(taskID, new TaskCallbackListener(FrontEndSupport.getStatus(taskStatus)) {
            @Override
            public void dataUpdate(TaskModel tm) {
                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(tm.getOwnerId())) {
                    enableEditMenu = true;
                } else {
                    enableEditMenu = false;
                }
            }
        });
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
        item.setVisible(enableEditMenu);
        return true;
    }
}
