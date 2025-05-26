package com.example.shifty.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class Update implements Comparable<Update> {

    private static final String SERVER_URL = "https://shifty-1c786-default-rtdb.europe-west1.firebasedatabase.app";
    private static final String COLLECTION_NAME = "updates";


    String id; // Unique identifier for the update, if needed
    String name;
    Date date;

    String description;

    boolean isCritical;

    public Update(String name, String description, boolean isCritical, Date date) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.isCritical = isCritical;
    }

    public Update(){
        // Default constructor required for calls to DataSnapshot.getValue(Update.class)
    }

    public Update(String id){
        // Constructor for creating an Update object with an ID
        this.id = id;
    }

    public CompletableFuture<Boolean> loadUpdate() {
        FirebaseDatabase fb = FirebaseDatabase.getInstance(SERVER_URL);
        DatabaseReference updateRef = fb.getReference(COLLECTION_NAME).child(String.valueOf(id));

        CompletableFuture <Boolean> future = new CompletableFuture<>();

        updateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    id = dataSnapshot.getKey();
                    name = dataSnapshot.child("name").getValue(String.class);
                    description = dataSnapshot.child("description").getValue(String.class);
                    Long dateLong = dataSnapshot.child("date").getValue(Long.class);

                    if (dateLong != null) {
                        date = new Date(dateLong);
                    } else {
                        date = null; // or handle as needed
                    }


                    Boolean critical = dataSnapshot.child("isCritical").getValue(Boolean.class);
                    isCritical = critical != null && critical;
                    future.complete(true);
                    Log.d("Update", "Update data loaded successfully.");
                } else {
                    future.complete(false);
                    Log.d("Update", "No data found for this update.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Update", "Failed to load update data.", error.toException());
            }
        });
        return future;
    }

    public CompletableFuture<Boolean> saveUpdate() {
        FirebaseDatabase fb = FirebaseDatabase.getInstance(SERVER_URL);
        DatabaseReference updateRef = fb.getReference(COLLECTION_NAME).child(String.valueOf(id));

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        updateRef.child("name").setValue(name);
        updateRef.child("description").setValue(description);
        updateRef.child("date").setValue(date.getTime());
        updateRef.child("isCritical").setValue(isCritical).addOnCompleteListener((result) -> {
            future.complete(true);
        });
        return future;
    }





    //getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public void setCritical(boolean critical) {
        isCritical = critical;
    }


    @Override
    public int compareTo(Update otherUpdate) {
        if(otherUpdate.isCritical ^ this.isCritical){
            return otherUpdate.isCritical? 1 : -1; // Prioritize critical updates
        }
        return date.compareTo(otherUpdate.getDate());
    }
}
