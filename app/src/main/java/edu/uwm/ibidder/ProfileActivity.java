package edu.uwm.ibidder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import edu.uwm.ibidder.Fragments.*;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
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
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        if(fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
