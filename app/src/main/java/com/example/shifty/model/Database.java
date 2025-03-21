package com.example.shifty.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class Database {

    private static final String TAG = "Database";
    private FirebaseFirestore db;

    public Database() {
        db = FirebaseFirestore.getInstance();
    }

    public static Database getInstance(){
        return new Database();
    }


    public void get(String COLLECTION_NAME, String DOC_NAME, Callback callback){
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(DOC_NAME);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        callback.onCallback(document.getData());
                        Log.d(TAG, "DocumentSnapshot loaded successfully ");
                    } else {

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void getField(String collectionName, String documentName, String fieldName, FieldCallback callback) {
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> data = document.getData();
                    Object fieldValue = data.get(fieldName);
                    callback.onCallback(fieldValue);
                    Log.d(TAG, "Field loaded successfully: " + fieldValue);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

            public void saveData(String COLLECTION_NAME, String DOC_NAME, Map<String, Object> data){
        db.collection(COLLECTION_NAME).document(DOC_NAME).set(data).addOnCompleteListener(task ->{
            if(task.isSuccessful()){
                Log.d(TAG, "DocumentSnapshot added with ID: " + DOC_NAME);
            }else{
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }

    public boolean delete(final String collctionName, final String documentName){
        DocumentReference doc = db.collection(collctionName).document(documentName);
        if(doc.get().isSuccessful()){
            doc.delete();
            return true;
        }else{
            return false;
        }
    }

    public boolean deleteField(final String collctionName, final String documentName, final String fieldName){
        DocumentReference doc = db.collection(collctionName).document(documentName);
        if(doc.get().isSuccessful()){
            doc.update(fieldName, FieldValue.delete());
            return true;
        }else{
            return false;
        }
    }


    public interface Callback {
        void onCallback(Map<String, Object> data);

    }

    public interface FieldCallback {
        void onCallback(Object data);

    }
}