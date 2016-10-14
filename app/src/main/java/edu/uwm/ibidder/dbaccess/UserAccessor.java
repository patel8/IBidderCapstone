package edu.uwm.ibidder.dbaccess;

import com.google.firebase.database.DatabaseReference;

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
        } catch (NullPointerException npe){
            return false;
        }

        return true;
    }

}
