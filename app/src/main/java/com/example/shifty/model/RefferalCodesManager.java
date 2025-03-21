package com.example.shifty.model;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class RefferalCodesManager {


    private static final String COLLECTION_NAME = "RefferalCodes";
    private static final String DOCUMENT_NAME = "Map";

    public static CompletableFuture<Boolean> checkCode(String code, String email) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        Database db = new Database();
        db.getField(COLLECTION_NAME, DOCUMENT_NAME, email, data ->{
            String actualCode = (String) data;
            if (actualCode.equals(code)) {
                result.complete(true);
            } else {
                result.complete(false);
            }
        });

        return result;
    }

    //method is used just after checking the code
    public static void useCode(String email) {
        Database db = new Database();
        db.deleteField(COLLECTION_NAME, DOCUMENT_NAME, email);
    }

}
