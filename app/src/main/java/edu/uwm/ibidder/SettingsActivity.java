package edu.uwm.ibidder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.UserModel;

public class SettingsActivity extends AppCompatActivity {

    TextView labelname;
    TextView labelloc;
    TextView labelphone;
    TextView labelreview;
    TextView labelaward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initializeAllWidgets();

        final UserAccessor userAccessor = new UserAccessor();

        userAccessor.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), new UserCallbackListener() {
            @Override
            public void dataUpdate(UserModel um) {
                String fname = um.getFirstName();
                String lname = um.getLastName();
                String label = labelname.getText().toString();
                labelname.setText(label + " " +fname + " " + lname);
                String phone = um.getPhoneNumber();
                label = labelphone.getText().toString();
                labelphone.setText(label + " " + phone);
            }
        });
    }

    private void initializeAllWidgets(){
        labelname = (TextView)findViewById(R.id.settings_textview_name);
        labelloc = (TextView)findViewById(R.id.settings_textview_location);
        labelphone = (TextView)findViewById(R.id.settings_textview_phone);
        labelreview = (TextView)findViewById(R.id.settings_textview_review);
        labelaward = (TextView)findViewById(R.id.settings_textview_awards);
    }
}
