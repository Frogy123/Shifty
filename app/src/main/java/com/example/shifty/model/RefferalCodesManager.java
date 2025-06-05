package com.example.shifty.model;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * Manages the operations related to referral codes in the application.
 * Handles verification and usage of referral codes for user registration
 * or promotions using Firebase Firestore.
 *
 * <p>
 * <b>Collection:</b> {@link #COLLECTION_NAME} <br>
 * <b>Document:</b> {@link #DOCUMENT_NAME}
 * </p>
 *
 * Usage example:
 * <pre>
 *   RefferalCodesManager.checkCode("myCode", "user@email.com")
 *       .thenAccept(isValid -> { ... });
 *   RefferalCodesManager.useCode("user@email.com");
 * </pre>
 *
 * @author Eitan Navon
 * @see Database
 */
public class RefferalCodesManager {

    /** The Firestore collection where referral codes are stored. */
    private static final String COLLECTION_NAME = "RefferalCodes";
    /** The Firestore document containing the referral code mapping. */
    private static final String DOCUMENT_NAME = "Map";

    /**
     * Asynchronously checks whether the given code matches the referral code stored for the specified email.
     *
     * @param code  the referral code to check
     * @param email the email associated with the referral code
     * @return a {@link CompletableFuture} that will complete with {@code true} if the code matches,
     *         {@code false} otherwise; the future may complete exceptionally if a database error occurs
     * @throws NullPointerException if the data returned from the database is {@code null}
     * @see Database#getField(String, String, String, Database.DataCallback)
     * @see <a href="https://firebase.google.com/docs/firestore">Firebase Firestore Docs</a>
     */
    public static CompletableFuture<Boolean> checkCode(String code, String email) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        Database db = new Database();
        db.getField(COLLECTION_NAME, DOCUMENT_NAME, email, data -> {
            String actualCode = (String) data;
            if (actualCode.equals(code)) {
                result.complete(true);
            } else {
                result.complete(false);
            }
        });
        return result;
    }

    /**
     * Marks a referral code as used by deleting the field associated with the given email.
     * This should be called only after a code has been checked and validated.
     *
     * @param email the email whose referral code entry should be deleted
     * @see Database#deleteField(String, String, String)
     * @see #checkCode(String, String)
     * @throws RuntimeException if the deletion fails due to a database error
     */
    public static void useCode(String email) {
        Database db = new Database();
        db.deleteField(COLLECTION_NAME, DOCUMENT_NAME, email);
    }
}
