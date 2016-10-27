package edu.uwm.ibidder;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ListMenuItemView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Date;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.firebase.auth.FirebaseAuth;

import edu.uwm.ibidder.Fragments.*;
import edu.uwm.ibidder.dbaccess.DateTools;
import edu.uwm.ibidder.Location.LocationService;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.TaskModel;
import edu.uwm.ibidder.dbaccess.models.UserModel;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    FloatingActionButton fabuttonTaskCreator;
    TextView userProfileName;
    TextView userEmailAddress;
    ImageView userImageURI;
    TextView dateLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeAllWidgets();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header= navigationView.getHeaderView(0);
        userProfileName = (TextView)header.findViewById(R.id.ProfileTextViewUserName);
        userEmailAddress = (TextView) header.findViewById(R.id.ProfileUserEmail);
        userImageURI = (ImageView) header.findViewById(R.id.imageView);
        UserAccessor UA = new UserAccessor();
        UA.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), new UserCallbackListener() {
            @Override
            public void dataUpdate(UserModel um) {
                if(um == null) return;
                userProfileName.setText(um.getFirstName()+" "+ um.getLastName());
                userEmailAddress.setText(um.getEmail());
                if(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()!=null)
                    userImageURI.setImageURI(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
            }
        });
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                3);
    }

    private void initializeAllWidgets(){
        fabuttonTaskCreator = (FloatingActionButton) findViewById(R.id.fabutton_createtask);
        fabuttonTaskCreator.setOnClickListener(this);
        final UserAccessor userAccessor = new UserAccessor();

        userAccessor.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), new UserCallbackListener() {
            @Override
            public void dataUpdate(UserModel um) {

                if(um.isValid()) {
                    //Case where user is Signed in for the first Time. Set all the fields. Ask for Required Fields.
                    if (um == null) {

                        um = new UserModel();
                        um.setFirstName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        um.setLastName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        um.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());


                    }
                    final AlertDialog taskCreateDialog = createAlertDialogForUsers(um);
                    taskCreateDialog.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        final MenuItem currentProfileMenu = menu.findItem(R.id.action_UserName);
        UserAccessor userAccessor = new UserAccessor();
        userAccessor.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), new UserCallbackListener() {
            @Override
            public void dataUpdate(UserModel um) {
                currentProfileMenu.setTitle(um.getFirstName());
            }
        });


        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       switch(id){
           case R.id.action_settings:
               startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
               break;
           case R.id.action_logOut:
               FirebaseAuth.getInstance().signOut();
               startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
               break;
       }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                3);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class  fragmentClass = null;
// Handle Each Item Accordinly.
        // Create Fragment with List Items of Selected Task and Inflate Layout.
        if (id == R.id.bidder_current_task) {
            fragmentClass = bidder_current_task.class;
        } else if (id == R.id.bidder_history_task) {
            fragmentClass = bidder_bid_history.class;
        } else if (id == R.id.bidder_won_tasks) {
            fragmentClass = bidder_won_tasks.class;
        } else if (id == R.id.creator_task_in_auction) {
            fragmentClass = creator_task_in_auction.class;
        } else if (id == R.id.creator_completed_task_auctions) {
            fragmentClass = creator_completed_task_auctions.class;
        } else if (id == R.id.creator_task_in_progress) {
            fragmentClass = creator_task_in_progress.class;
        }else if(id == R.id.creator_task_history){
            fragmentClass = creator_task_history.class;
        }
        else if(id == R.id.user_profile){
            startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
        }

        try{
            fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean taskCreateValidation(String name, String descr, String price){
        if(!name.matches("") && !descr.matches("") && !price.matches("")){
            return true;
        }

        Toast.makeText(ProfileActivity.this, "Invalid information", Toast.LENGTH_SHORT).show();
        return false;
    }

    private AlertDialog createAlertDialogForUsers(UserModel userModel){
        final AlertDialog ad = new AlertDialog.Builder(ProfileActivity.this).create();
        LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
        ad.setTitle("User Fields");
        View view = inflater.inflate(R.layout.alertdialog_userfields, null);
        ad.setView(view);
        final EditText FirstName = (EditText)view.findViewById(R.id.alertDialog_EditText_FirstName);
        final EditText LastName = (EditText)view.findViewById(R.id.alertDialog_EditText_LastName);
        final EditText PhoneNumber = (EditText)view.findViewById(R.id.alertDialog_EditText_PhoneNumber);
        final Button Update = (Button)view.findViewById(R.id.alertDialog_Button_Update);

        FirstName.setText(userModel.getFirstName());
        LastName.setText(userModel.getLastName());
        PhoneNumber.setText(userModel.getPhoneNumber());

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final UserAccessor userAccessor = new UserAccessor();

              userAccessor.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), new UserCallbackListener() {
                    @Override
                    public void dataUpdate(UserModel um) {
                        um.setFirstName(FirstName.getText().toString());
                        um.setLastName(LastName.getText().toString());
                        um.setPhoneNumber(PhoneNumber.getText().toString());
                       userAccessor.updateUser(um);
                        ad.dismiss();
                    }
                });
            }
        });
        return ad;
    }

    private AlertDialog createAlertDialog(){
        final AlertDialog ad = new AlertDialog.Builder(ProfileActivity.this).create();
        LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
        ad.setTitle("Create a task");
        ad.setMessage("What can a BidButler do for you?");
        View view = inflater.inflate(R.layout.alertdialog_taskcreator, null);
        ad.setView(view);
        final EditText taskname = (EditText)view.findViewById(R.id.editText_taskname);
        final EditText taskdescr = (EditText)view.findViewById(R.id.editText_taskdescription);
        final EditText taskprice = (EditText)view.findViewById(R.id.editText_startprice);
        final EditText tasktags = (EditText)view.findViewById(R.id.editText_tasktags);
        dateLabel = (TextView)view.findViewById(R.id.label_taskEndTime);
        dateLabel.setText(new Date().toString());

        dateLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog timeSetDialog = createTimeSetDialog();
                timeSetDialog.show();
            }
        });

        ad.setButton(AlertDialog.BUTTON_NEUTRAL, "Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tskname = taskname.getText().toString();
                String tskdesc = taskdescr.getText().toString();
                String tskprice = taskprice.getText().toString();

                if(taskCreateValidation(tskname, tskdesc, tskprice)){
                    TaskAccessor ta = new TaskAccessor();
                    TaskModel tm = new TaskModel();
                    tm.setTitle(tskname);
                    tm.setDescription(tskdesc);
                    tm.setMaxPrice(Float.parseFloat(tskprice));
                    String tskId = ta.createTask(tm); // doesnt seem to be adding new task to firebase
                    Toast.makeText(ProfileActivity.this, "created " + tskId, Toast.LENGTH_LONG).show();
                }
            }
        });

        return ad;
    }

    private AlertDialog createTimeSetDialog() {
        final AlertDialog ad = new AlertDialog.Builder(this).create();
        final LayoutInflater inflater = this.getLayoutInflater();
        ad.setTitle("Choose a time");
        ad.setMessage("When should your task expire?");
        View view = inflater.inflate(R.layout.alertdialog_datesetter, null);
        ad.setView(view);
        final TimePicker tp = (TimePicker)view.findViewById(R.id.timePicker);
        final CalendarView cv = (CalendarView)view.findViewById(R.id.calendarView);

        ad.setButton(AlertDialog.BUTTON_NEUTRAL, "Choose", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Date d = new Date(cv.getDate());
                d.setHours(tp.getHour());
                d.setMinutes(tp.getMinute());
                dateLabel.setText(d.toString());
            }
        });

        return ad;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fabutton_createtask:
                final AlertDialog taskCreateDialog = createAlertDialog();
                taskCreateDialog.show();
                break;
            default:
                break;
        }
    }
}
