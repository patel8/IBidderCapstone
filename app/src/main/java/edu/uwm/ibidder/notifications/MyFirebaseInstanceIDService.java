package edu.uwm.ibidder.notifications;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import edu.uwm.ibidder.dbaccess.UserAccessor;
import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.UserModel;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Manages unique ID's for the notification service
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static String token;

    @Override
    public void onTokenRefresh() {
        updateTokenOnServer();
    }

    public static void updateTokenOnServer() {
        token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);
        if (token != null) {
            try {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final UserAccessor ua = new UserAccessor();

                ua.getUser(userId, new UserCallbackListener() {
                    @Override
                    public void dataUpdate(UserModel um) {
                        um.setMessengerId(token);
                        ua.updateUser(um);
                    }
                });

            } catch (Exception e) {
                Log.d(TAG, "Not yet logged in, token set delayed");
            }
        }
    }

}
