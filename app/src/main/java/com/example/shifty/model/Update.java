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

/**
 * Represents an update or announcement within the system. Supports
 * loading and saving from/to Firebase Realtime Database and allows
 * prioritization of critical updates.
 *
 * <p>
 * The class implements {@link Comparable} for sorting by criticality and date.
 * </p>
 *
 * @author Eitan Navon
 * @see FirebaseDatabase
 */
public class Update implements Comparable<Update> {

    /** Firebase Realtime Database URL. */
    private static final String SERVER_URL = "https://shifty-1c786-default-rtdb.europe-west1.firebasedatabase.app";

    /** Name of the updates collection/table in Firebase. */
    private static final String COLLECTION_NAME = "updates";

    /** Unique identifier for the update. */
    String id;

    /** Name/title of the update. */
    String name;

    /** Date/time when the update was created or issued. */
    Date date;

    /** Description or body text of the update. */
    String description;

    /** Whether this update is critical and should be prioritized. */
    boolean isCritical;

    /**
     * Constructs an Update with the specified details.
     *
     * @param name        the name/title of the update
     * @param description the detailed description of the update
     * @param isCritical  whether the update is critical
     * @param date        the date of the update
     */
    public Update(String name, String description, boolean isCritical, Date date) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.isCritical = isCritical;
    }

    /**
     * Default constructor.
     * <p>
     * Required for Firebase deserialization via {@link DataSnapshot#getValue(Class)}.
     * </p>
     */
    public Update() {
        // Required for Firebase
    }

    /**
     * Constructs an Update object with a given ID.
     *
     * @param id the unique identifier of the update
     */
    public Update(String id) {
        this.id = id;
    }

    /**
     * Loads the update details from Firebase using its ID.
     * Asynchronous operation; returns a {@link CompletableFuture} that
     * completes with {@code true} if the update exists and was loaded successfully,
     * or {@code false} if no such update exists.
     *
     * @return a {@link CompletableFuture} with the load result
     * @throws NullPointerException if {@link #id} is {@code null}
     * @see DatabaseReference#addValueEventListener(ValueEventListener)
     */
    public CompletableFuture<Boolean> loadUpdate() {
        FirebaseDatabase fb = FirebaseDatabase.getInstance(SERVER_URL);
        DatabaseReference updateRef = fb.getReference(COLLECTION_NAME).child(String.valueOf(id));

        CompletableFuture<Boolean> future = new CompletableFuture<>();

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
                        date = null;
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
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    /**
     * Saves the update details to Firebase under its ID.
     * Overwrites the current values.
     *
     * @return a {@link CompletableFuture} that completes when the save is done
     * @throws NullPointerException if {@link #id}, {@link #name}, {@link #date}, or {@link #description} is {@code null}
     * @see DatabaseReference#setValue(Object)
     */
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

    // -------------------------
    // Getters and Setters
    // -------------------------

    /**
     * Gets the update's unique identifier.
     *
     * @return the ID of the update
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the update's unique identifier.
     *
     * @param id the ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the name or title of the update.
     *
     * @return the name/title of the update
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name or title of the update.
     *
     * @param name the new name/title to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the date of the update.
     *
     * @return the {@link Date} of the update
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date of the update.
     *
     * @param date the new {@link Date} to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the description of the update.
     *
     * @return the description/body of the update
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the update.
     *
     * @param description the new description/body to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Checks whether this update is marked as critical.
     *
     * @return {@code true} if critical; {@code false} otherwise
     */
    public boolean isCritical() {
        return isCritical;
    }

    /**
     * Sets whether this update is critical.
     *
     * @param critical {@code true} to mark as critical; {@code false} otherwise
     */
    public void setCritical(boolean critical) {
        isCritical = critical;
    }

    /**
     * Compares this update to another update for sorting.
     * Critical updates are prioritized before non-critical.
     * If criticality is equal, compares by date.
     *
     * @param otherUpdate the other update to compare to
     * @return a negative integer, zero, or a positive integer as this update
     *         is less than, equal to, or greater than the specified update
     */
    @Override
    public int compareTo(Update otherUpdate) {
        if (otherUpdate.isCritical ^ this.isCritical) {
            return otherUpdate.isCritical ? 1 : -1;
        }
        return date.compareTo(otherUpdate.getDate());
    }
}
