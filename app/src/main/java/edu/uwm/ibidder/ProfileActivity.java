package edu.uwm.ibidder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import edu.uwm.ibidder.Fragments.*;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.models.TaskModel;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    FloatingActionButton fabuttonTaskCreator;

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
    }

    private void initializeAllWidgets(){
        fabuttonTaskCreator = (FloatingActionButton) findViewById(R.id.fabutton_createtask);
        fabuttonTaskCreator.setOnClickListener(this);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       //Todo - What happens when they click on Setting
        /**
         * Todo- User must be able to Turn off services for
         * Bid Notifications
         * Should be able to Select their Radius For Bidding and Task Creater
         *
         */
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       switch(id){
           case R.id.action_settings:
               startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
               break;
           case R.id.action_logOut:
               // Log out current User and send it back to login Screen.
               String Provider = FirebaseAuth.getInstance().getCurrentUser().getProviderId();
               if(Provider.equals("facebook.com"));
                LoginManager.getInstance().logOut();

               FirebaseAuth.getInstance().signOut();
               startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
               break;
       }

        return super.onOptionsItemSelected(item);
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
