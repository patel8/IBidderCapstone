package edu.uwm.ibidder.Fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import edu.uwm.ibidder.R;
import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.UserModel;

public class profileFragment extends Fragment {

    private TextView firstName;
    private TextView lastName;
    private TextView Email;
    private TextView PhoneNumber;


    public profileFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_settings, container, false);

        String userId = getActivity().getIntent().getStringExtra("UserID");
        firstName = (TextView) view.findViewById(R.id.userProfileFirstName);
        lastName = (TextView) view.findViewById(R.id.userProfileLastName);
        Email = (TextView) view.findViewById(R.id.userProfileEmail);
        PhoneNumber = (TextView) view.findViewById(R.id.userProfilePhone);

        if(userId!=null)
        {
            UserAccessor userAccessor = new UserAccessor();
            userAccessor.getUser(userId, new UserCallbackListener() {
                @Override
                public void dataUpdate(UserModel um) {
                    firstName.setText(um.getFirstName());
                    lastName.setText(um.getLastName());
                    Email.setText(um.getEmail());
                    PhoneNumber.setText(um.getPhoneNumber());
                }
            });
        }



        return view;
    }
}
