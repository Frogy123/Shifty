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

public class UpdateManager {

    private static final String SERVER_URL = "https://shifty-1c786-default-rtdb.europe-west1.firebasedatabase.app";
    private static final String COLLECTION_NAME = "updates";
    private static UpdateManager instance;
    private List<Update> updates = new ArrayList<>();
    private boolean initialized = false;
    private MutableLiveData<Boolean> needRefresh = new MutableLiveData<>(); //just when something is added




    public static UpdateManager getInstance() {
        if(instance == null) instance = new UpdateManager();
        return instance;
    }


    private UpdateManager() {
        initializeUpdate();
        monitorUIDs();
    }


    private void initializeUpdate(){
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
                // Print or use the list of UIDs
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error fetching UIDs: " + databaseError.getMessage());
            }
        });
        return toReturn;
    }

    private void monitorUIDs() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference employeesRef = database.getReference(COLLECTION_NAME);

        employeesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // This method is triggered when a new child is added
                String id = dataSnapshot.getKey();
                addUpdate(id);
                needRefresh.postValue(true); // Notify observers that an update was added

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeUpdate(dataSnapshot.getKey());
                needRefresh.postValue(true);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Triggered when a child node is moved
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error monitoring UIDs: " + databaseError.getMessage());
            }
        });
    }

    public List<Update> getUpdates(){
        return updates;
    }


    public Update getUpdate(String uid) {
        Update updateToReturn = null;

        for(Update update: updates) {
            if(update.getId().equals(uid)) {
                update = update;
                break;
            }
        }

        return updateToReturn;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public void saveAllUpdates() {
        for (Update update : updates) {
            update.saveUpdate();
        }
    }

    public void removeUpdate(String id) {
        for(Update update : updates) {
            if(update.getId().equals(id)) {
                updates.remove(update);
                break;
            }
        }
    }

    public void addUpdate(String id) {
        Update update = new Update(id);
        CompletableFuture<Boolean> future = update.loadUpdate();

        future.thenAccept((result) -> {
            if(result == true){
                ListUtil.insertSorted(updates, update);
                needRefresh.postValue(true); // Notify observers that an update was added
            }
        });
    }

    public CompletableFuture<Boolean> addNewUpdate(Update update){

        CompletableFuture<Boolean> future;
        update.setId(createUniqueId());
        ListUtil.insertSorted(updates, update);
        needRefresh.postValue(true);
        future = update.saveUpdate();
        return future;

    }

    public int getUpdatesCount() {
        return updates.size();
    }

    private String createUniqueId() {
        return UUID.randomUUID().toString();
    }



    public MutableLiveData<Boolean> getNeedRefresh() {
        return needRefresh;
    }


}
