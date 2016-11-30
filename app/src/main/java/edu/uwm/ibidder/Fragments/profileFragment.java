package edu.uwm.ibidder.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import edu.uwm.ibidder.R;
import edu.uwm.ibidder.dbaccess.ReviewAccessor;
import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.ReviewCallbackListener;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.ReviewModel;
import edu.uwm.ibidder.dbaccess.models.UserModel;

public class profileFragment extends Fragment {

    private TextView firstName;
    private TextView lastName;
    private TextView Email;
    private TextView PhoneNumber;
    private LinearLayout phoneNumberLayout;
    private LinearLayout userReviewLayout;

    public profileFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);

        String userId = getActivity().getIntent().getStringExtra("UserID");
        firstName = (TextView) view.findViewById(R.id.userProfileFirstName);
        lastName = (TextView) view.findViewById(R.id.userProfileLastName);
        Email = (TextView) view.findViewById(R.id.userProfileEmail);
        PhoneNumber = (TextView) view.findViewById(R.id.userProfilePhone);
        phoneNumberLayout = (LinearLayout) view.findViewById(R.id.phoneNumberLayout);
        if (userId != null) {
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

        phoneNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        3);

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + PhoneNumber.getText().toString()));
                startActivity(intent);
            }
        });

        return view;
    }
}
