package edu.uwm.ibidder.dbaccess;

import com.google.firebase.database.DatabaseReference;

import edu.uwm.ibidder.dbaccess.listeners.UserCallbackListener;
import edu.uwm.ibidder.dbaccess.models.UserModel;

/**
 * Handles interactions with user objects in the database
 */
public class UserAccessor extends BaseAccessor {

    /**
     * Default constructor, initializes with no listeners
     */
    public UserAccessor() {
        super();
    }

    /**
     * Creates a user object in firebase with the provided UserModel and FireBaseAuth's UID.
     *
     * @param userToCreate The user to put in the database
     * @return true if it worked
     */
    public boolean createUser(UserModel userToCreate) {
        try {
            DatabaseReference ref = database.getReference("users/" + auth.getCurrentUser().getUid());
            ref.setValue(userToCreate);
        } catch (NullPointerException npe) {
            return false;
        }

        return true;
    }

    /**
     * Gets a User in its current form and sends it to the passed-in UserCallbackListener.  This does not update constantly.
     *
     * @param userId               The id of the user (aka the uid)
     * @param userCallbackListener The UserCallbackListener that will get the UserModel
     */
    public void getUserOnce(String userId, UserCallbackListener userCallbackListener) {
        DatabaseReference ref = database.getReference("users/" + userId);
        ref.addListenerForSingleValueEvent(userCallbackListener);
    }

    /**
     * Gets a User in its current form and sends it to the passed-in UserCallbackListener.  This remains up-to-date.
     *
     * @param userId               The id of the user (aka the uid)
     * @param userCallbackListener The UserCallbackListener that will get the UserModel
     */
    public void getUser(String userId, UserCallbackListener userCallbackListener) {
        DatabaseReference ref = database.getReference("users/" + userId);
        storedValueEventListeners.push(ref.addValueEventListener(userCallbackListener));
        storedDatabaseRefs.push(ref);
    }

    /**
     * Updates a user's info in firebase.
     *
     * @param userId      the id aka uid of the user to update
     * @param newUserInfo the new user info
     */
    public void updateUser(String userId, UserModel newUserInfo) {
        DatabaseReference ref = database.getReference("users/" + userId);
        ref.setValue(newUserInfo);
    }

    /**
     * Deletes a user's info in firebase.
     *
     * @param userId the id aka uid of the user to delete
     */
    public void removeUser(String userId) {
        DatabaseReference ref = database.getReference("users/" + userId);
        ref.removeValue();
    }

}
