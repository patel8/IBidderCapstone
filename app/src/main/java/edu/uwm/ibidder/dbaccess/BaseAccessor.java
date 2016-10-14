package edu.uwm.ibidder.dbaccess;

import com.firebase.geofire.GeoFire;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Stack;

/**
 * Base class for all accessors
 */
public abstract class BaseAccessor {

    final protected FirebaseDatabase database = FirebaseDatabase.getInstance();
    final protected FirebaseAuth auth = FirebaseAuth.getInstance();
    final protected GeoFire geoFire = new GeoFire(database.getReference("geofire"));

    protected Stack<DatabaseReference> storedDatabaseRefs;
    protected Stack<ValueEventListener> storedValueEventListeners;

    /**
     * Creates a BaseAccessor with its own pool of listeners
     */
    public BaseAccessor() {
        storedDatabaseRefs = new Stack<DatabaseReference>();
        storedValueEventListeners = new Stack<ValueEventListener>();
    }

    /**
     * Unhooks all persistent listeners.
     */
    public void stopListening() {
        while (!storedDatabaseRefs.empty() && !storedValueEventListeners.empty()) {
            storedDatabaseRefs.pop().removeEventListener(storedValueEventListeners.pop());
        }
    }

}
