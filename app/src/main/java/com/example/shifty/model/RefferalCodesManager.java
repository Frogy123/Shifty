package com.example.shifty.model;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RefferalCodesManager {


    private static final String COLLECTION_NAME = "refferalCodes";
    private static final String DOCUMENT_NAME = "MAP";


    //create an empty map if it doesn't exist
    private static void createIfNeeded(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_NAME).document(DOCUMENT_NAME).get().addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                db.collection(COLLECTION_NAME).document(DOCUMENT_NAME).set(new HashMap<String, String>());
            }
        });
    }

    public static void addRefferalCode(String uid, String code){
        createIfNeeded();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> data = new HashMap<>();
        data.put(uid, code);

        db.collection(COLLECTION_NAME).document(DOCUMENT_NAME).update(data);

    }

    private static void getRefferalCode(String uid, final RefferalCodeCallback callback){
        createIfNeeded();
        FirebaseFirestore db = FirebaseFirestore.getInstance();



        db.collection(COLLECTION_NAME).document(DOCUMENT_NAME).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String resultCode = task.getResult().getString(uid);
                callback.onCallback(resultCode);
            }
        });

    }

    public static void checkReffearalCode(String uid, String code) throws IllegalArgumentException{
        getRefferalCode(uid, resultCode -> {
            if(!resultCode.equals(code)){
                throw new IllegalArgumentException("Invalid refferal code");
            }
        });
    }

    //@PRE - checkRefferalCode() == true
    //@POST - the code releated to the uid is deleted
    public static void useRefferalCode(String uid, String code){
        getRefferalCode(uid, resultCode -> {
                deleteRefferalCode(uid);
        });
    }

    public static void deleteRefferalCode(String uid){
        createIfNeeded();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_NAME).document(DOCUMENT_NAME).update(uid, FieldValue.delete());
    }
    interface RefferalCodeCallback{
        //void onCallback(String code, String resultCode);
        void onCallback(String code);
    }
}
