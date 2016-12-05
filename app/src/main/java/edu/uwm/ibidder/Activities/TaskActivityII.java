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
import android.util.Log;
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
import edu.uwm.ibidder.dbaccess.ReportAccessor;
import edu.uwm.ibidder.dbaccess.ReviewAccessor;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.TaskCompletedAccessor;
import edu.uwm.ibidder.dbaccess.TaskWinnerAccessor;
import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.listeners.TaskWinnerCallbackListener;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.ReportModel;
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
    private String caller;
    private Button buttonTaskComplete;
    private Toolbar toolbar;
    private boolean showToolBar;
    private boolean enableEditMenu = false;
    private boolean enableBidMenu = false;
    private boolean enableReportTask = false;
    private Button ReportTaskButton;
    private boolean enableCompleteTask = false;
    private Menu menu;

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
        caller = getIntent().getStringExtra("caller");

        //Get all visibility for Bottom Visibility
        showToolBar = getIntent().getBooleanExtra("ShowToolBar", false);
        enableReportTask = getIntent().getBooleanExtra("ShowReportTask", false);
        enableCompleteTask = getIntent().getBooleanExtra("ShowCompleteTask", false);

        // Initialize All Bottom App bar Visibility
        buttonTaskComplete = (Button) findViewById(R.id.buttomCompleteTask);
        ReportTaskButton = (Button) findViewById(R.id.buttonReportTask);
        toolbar = (Toolbar) findViewById(R.id.TaskToolBar);

        //Set Visibility
        ReportTaskButton.setVisibility(enableReportTask ? View.VISIBLE : View.GONE);
        toolbar.setVisibility(showToolBar? View.VISIBLE: View.GONE);
        buttonTaskComplete.setVisibility(enableCompleteTask ? View.VISIBLE: View.GONE);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        buttonTaskComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When 'Complete Task' is clicked
                AlertDialog dialog = createReviewDialog(taskID);
                dialog.show();
            }
        });

        ReportTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = reportTaskDialog(taskID);
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
                // outer if statement determines if the calling fragment is one in which a user
                // should be able to edit/bid on the task still
                if (caller.equals("all_available_task") || caller.equals("bidder_live_task") || caller.equals("creator_task_in_auction") || caller.equals("TaskFragment")) {
                    if (FrontEndSupport.isCurrentUser(tm.getOwnerId())) {
                        enableEditMenu = true;
                        enableBidMenu = false;
                    } else {
                        enableEditMenu = false;
                        enableBidMenu = true;
                    }
                } else {
                    enableEditMenu = false;
                    enableBidMenu = false;
                }

                if (menu != null)
                    optionMenuUpdate(menu);
            }
        });
    }
    private AlertDialog reportTaskDialog(final String TaskId) {
        final AlertDialog ad = new AlertDialog.Builder(this).create();
        final LayoutInflater inflater = this.getLayoutInflater();
        ad.setTitle("Report");
        View view = inflater.inflate(R.layout.alertdialog_addreport, null);
        ad.setView(view);
        final EditText Description = (EditText) view.findViewById(R.id.editTextDescriptionReport);
        final Button submit = (Button) view.findViewById(R.id.buttonSubmitReport);
    submit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ReportAccessor reportAccessor = new ReportAccessor();
            ReportModel reportModel = new ReportModel();
            reportModel.setDescription(Description.getText().toString().trim());
            reportModel.setTaskId(taskID);
            reportModel.setReporterId(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reportAccessor.createReport(reportModel);
            Toast.makeText(TaskActivityII.this, "Report has been placed", Toast.LENGTH_SHORT).show();
            ad.dismiss();
            startActivity(new Intent(TaskActivityII.this, ProfileActivity.class));
        }
    });

        return ad;
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
                        // Add review
                        ReviewAccessor reviewAccessor = new ReviewAccessor();
                        ReviewModel reviewModel = new ReviewModel();
                        reviewModel.setReviewScore(ratingBar.getNumStars());
                        reviewModel.setReviewText(description.getText().toString());

                        reviewModel.setReviewWriterId(um.getTaskOwnerId());
                        reviewModel.setUserReviewedId(um.getWinnerId());
                        reviewModel.setAssociatedTaskId(TaskId);
                        reviewModel.setIsBidderReview(false);

                        reviewAccessor.createReview(reviewModel);

                        // Task completed
                        TaskCompletedAccessor taskCompletedAccessor = new TaskCompletedAccessor();
                        taskCompletedAccessor.markTaskCompleted(taskID);
                        Toast.makeText(TaskActivityII.this, "Task has been completed.", Toast.LENGTH_SHORT).show();

                        ad.dismiss();

                        startActivity(new Intent(TaskActivityII.this, ProfileActivity.class));

                    }
                });
            }
        });


        return ad;
    }


    private class CustomAdapter extends FragmentPagerAdapter {

        private String fragments[] = {"Task Information", "List of Bidder"};

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

    private void optionMenuUpdate(Menu menu) {
        item = menu.findItem(R.id.edit_task_menu);
        item.setVisible(enableEditMenu);
        item = menu.findItem(R.id.place_bid_menu);
        item.setVisible(enableBidMenu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_menu, menu);
        this.menu = menu;
        optionMenuUpdate(menu);
        return true;
    }
}
