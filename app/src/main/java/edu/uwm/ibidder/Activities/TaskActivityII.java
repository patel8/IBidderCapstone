package edu.uwm.ibidder.Activities;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;

import edu.uwm.ibidder.Fragments.BidderListFragment;
import edu.uwm.ibidder.Fragments.TaskFragment;
import edu.uwm.ibidder.FrontEndSupport;
import edu.uwm.ibidder.R;
import edu.uwm.ibidder.dbaccess.BidAccessor;
import edu.uwm.ibidder.dbaccess.ReviewAccessor;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.TaskCompletedAccessor;
import edu.uwm.ibidder.dbaccess.TaskWinnerAccessor;
import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.listeners.TaskWinnerCallbackListener;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.ReviewModel;
import edu.uwm.ibidder.dbaccess.models.TaskModel;
import edu.uwm.ibidder.dbaccess.models.TaskWinnerModel;
import edu.uwm.ibidder.dbaccess.models.UserModel;
public class TaskActivityII extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MenuItem item;
    private String taskID;
    private String taskStatus;
    private Button buttonTaskComplete;
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
        buttonTaskComplete = (Button) findViewById(R.id.buttomCompleteTask);
        buttonTaskComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Todo: When Task Complete Button is clicked.
                TaskCompletedAccessor taskCompletedAccessor = new TaskCompletedAccessor();
                taskCompletedAccessor.markTaskCompleted(taskID);
                Toast.makeText(TaskActivityII.this, taskID+ "Task is Marked Successfully", Toast.LENGTH_SHORT).show();
                AlertDialog dialog = createReviewDialog(taskID);
                dialog.show();



            }
        });
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
    private AlertDialog createReviewDialog(final String TaskId) {
        final AlertDialog ad = new AlertDialog.Builder(this).create();
        final LayoutInflater inflater = this.getLayoutInflater();
        ad.setTitle("Review");
        View view = inflater.inflate(R.layout.alertdialog_review, null);
        ad.setView(view);
        final TextView userName = (TextView) view.findViewById(R.id.textViewUserNameReview);
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBarReview);
        final EditText description = (EditText) view.findViewById(R.id.editTextDescriptionReview);
        final Button submit = (Button) view.findViewById(R.id.buttonSumbitReview);


        TaskWinnerAccessor taskWinnerAccessor = new TaskWinnerAccessor();
        taskWinnerAccessor.GetTaskWinnerByTaskIdOnce(TaskId, new TaskWinnerCallbackListener() {
            @Override
            public void dataUpdate(final TaskWinnerModel um) {

                UserAccessor userAccessor = new UserAccessor();
                userAccessor.getUser(um.getWinnerId(), new UserCallbackListener() {
                    @Override
                    public void dataUpdate(final UserModel um) {
                        userName.setText(um.getFirstName()+" "+um.getLastName());
                        userName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(TaskActivityII.this, UserProfileActivity.class).putExtra("UserID", um.getUserId()));
                            }
                        });
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ReviewAccessor reviewAccessor = new ReviewAccessor();
                        ReviewModel reviewModel = new ReviewModel();
                        reviewModel.setReviewScore(ratingBar.getNumStars());
                        reviewModel.setReviewText(description.getText().toString());

                        reviewModel.setReviewWriterId(um.getTaskOwnerId());
                        reviewModel.setUserReviewedId(um.getWinnerId());

                        reviewAccessor.createReview(reviewModel);
                        ad.dismiss();

                        startActivity(new Intent(TaskActivityII.this, ProfileActivity.class));

                    }
                });
            }
        });


        return ad;
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
