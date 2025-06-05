package com.example.shifty.model;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Manages {@link Update} objects in the system.
 * <p>
 * Handles fetching, monitoring, adding, removing, and saving of updates stored in Firebase.
 * Implements the Singleton pattern.
 * </p>
 *
 * <p>Supports live data observation for UI updates, sorted insertion, and asynchronous operations with {@link CompletableFuture}.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * UpdateManager manager = UpdateManager.getInstance();
 * manager.addNewUpdate(new Update("New Update"));
 * }
 * </pre>
 *
 * @author Eitan Navon
 * @see Update
 * @see ListUtil
 * @see FirebaseDatabase
 * @see CompletableFuture
 */
public class UpdateManager {

    private static final String SERVER_URL = "https://shifty-1c786-default-rtdb.europe-west1.firebasedatabase.app";
    private static final String COLLECTION_NAME = "updates";
    private static UpdateManager instance;
    private List<Update> updates = new ArrayList<>();
    private boolean initialized = false;
    private MutableLiveData<Boolean> needRefresh = new MutableLiveData<>();

    /**
     * Returns the singleton instance of {@code UpdateManager}.
     *
     * @return the singleton instance
     */
    public static UpdateManager getInstance() {
        if(instance == null) instance = new UpdateManager();
        return instance;
    }

    /**
     * Private constructor. Initializes updates and sets up live monitoring.
     */
    private UpdateManager() {
        initializeUpdate();
        monitorUIDs();
    }

    /**
     * Initializes updates by fetching all update IDs from the database
     * and adding them to the local list.
     * Uses asynchronous operations and handles errors.
     */
    private void initializeUpdate() {
        CompletableFuture<List<String>> future = getAllUpdatesIDs();
        future.thenAccept(ids -> {
            for (String id : ids) {
                addUpdate(id);
                initialized = true;
            }
        }).exceptionally(e -> {
            System.err.println("Error fetching UIDs: " + e.getMessage());
            return null;
        });
    }

    /**
     * Fetches all update IDs from the Firebase database asynchronously.
     *
     * @return a {@link CompletableFuture} that resolves to a list of update IDs
     * @throws RuntimeException if there is a database connectivity error
     * @see DatabaseReference#addListenerForSingleValueEvent(ValueEventListener)
     */
    private CompletableFuture<List<String>> getAllUpdatesIDs() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(SERVER_URL);
        DatabaseReference employeesRef = database.getReference(COLLECTION_NAME);
        CompletableFuture<List<String>> toReturn = new CompletableFuture<>();
        employeesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> uids = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String uid = childSnapshot.getKey();
                    if (uid != null) {
                        uids.add(uid);
                    }
                }
                toReturn.complete(uids);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error fetching UIDs: " + databaseError.getMessage());
            }
        });
        return toReturn;
    }

    /**
     * Starts listening to updates in the updates collection (additions/removals).
     * Notifies observers when the update list changes.
     */
    private void monitorUIDs() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference employeesRef = database.getReference(COLLECTION_NAME);

        employeesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String id = dataSnapshot.getKey();
                addUpdate(id);
                needRefresh.postValue(true); // Notify observers
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeUpdate(dataSnapshot.getKey());
                needRefresh.postValue(true);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error monitoring UIDs: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Returns a reference to the list of updates.
     *
     * @return a list of {@link Update} objects
     */
    public List<Update> getUpdates() {
        return updates;
    }

    /**
     * Gets an update by its unique ID.
     *
     * @param uid the unique ID of the update
     * @return the {@link Update} with the given ID, or {@code null} if not found
     */
    public Update getUpdate(String uid) {
        for(Update update : updates) {
            if(update.getId().equals(uid)) {
                return update;
            }
        }
        return null;
    }

    /**
     * Returns whether the manager has completed its initialization.
     *
     * @return {@code true} if initialized, {@code false} otherwise
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Sets the initialization state of the manager.
     *
     * @param initialized {@code true} to mark as initialized, {@code false} otherwise
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Saves all updates in the current list to the database.
     * Calls {@link Update#saveUpdate()} on each update.
     */
    public void saveAllUpdates() {
        for (Update update : updates) {
            update.saveUpdate();
        }
    }

    /**
     * Removes an update from the list and the database by its ID.
     *
     * @param id the unique ID of the update to remove
     */
    public void removeUpdate(String id) {
        updates.removeIf(update -> update.getId().equals(id));
    }

    /**
     * Adds an update by ID, loading its content asynchronously from the database.
     * If loading is successful, the update is inserted in sorted order.
     *
     * @param id the unique ID of the update to add
     * @throws NullPointerException if id is {@code null}
     */
    public void addUpdate(String id) {
        Update update = new Update(id);
        CompletableFuture<Boolean> future = update.loadUpdate();
        future.thenAccept((result) -> {
            if(Boolean.TRUE.equals(result)){
                ListUtil.insertSorted(updates, update);
                needRefresh.postValue(true);
            }
        });
    }

    /**
     * Adds a new update, assigns it a unique ID, saves it to the database,
     * and inserts it in sorted order.
     *
     * @param update the {@link Update} to add
     * @return a {@link CompletableFuture} that resolves to {@code true} if the update was saved successfully
     * @throws NullPointerException if update is {@code null}
     */
    public CompletableFuture<Boolean> addNewUpdate(Update update) {
        update.setId(createUniqueId());
        ListUtil.insertSorted(updates, update);
        needRefresh.postValue(true);
        return update.saveUpdate();
    }

    /**
     * Returns the number of updates in the list.
     *
     * @return the number of updates
     */
    public int getUpdatesCount() {
        return updates.size();
    }

    /**
     * Generates a new unique ID for an update.
     *
     * @return a unique string identifier (UUID)
     * @see UUID
     */
    private String createUniqueId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Returns the {@link MutableLiveData} object used to notify observers of list changes.
     *
     * @return the live data object for refresh notifications
     * @see MutableLiveData
     */
    public MutableLiveData<Boolean> getNeedRefresh() {
        return needRefresh;
    }
}
