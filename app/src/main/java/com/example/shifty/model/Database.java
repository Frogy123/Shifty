package com.example.shifty.model;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

public class Database {

    private FirebaseFirestore db;

    public Database() {
        db = FirebaseFirestore.getInstance();
    }

    public void saveUser(User user) {
        DocumentReference userRef = db.collection("users").document(user.getUid());
        userRef.set(user)
                .addOnSuccessListener(aVoid -> {
                    // Data saved successfully
                })
                .addOnFailureListener(e -> {
                    // Failed to save data
                });
    }

    public void getUser(String uid, final UserCallback callback) {
        DocumentReference userRef = db.collection("users").document(uid);
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onCallback(user);
                    } else {
                        callback.onCallback(null);
                    }
                })
                .addOnFailureListener(e -> {
                    // Failed to retrieve data
                    callback.onCallback(null);
                });
    }

    public interface UserCallback {
        void onCallback(User user);
    }
}