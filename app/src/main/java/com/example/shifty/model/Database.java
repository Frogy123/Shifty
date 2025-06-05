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
import java.util.concurrent.CompletableFuture;

/**
 * Database abstraction layer for Firestore, providing simplified CRUD operations
 * for collections and documents, as well as field and element counting.
 * <p>
 * All methods are asynchronous, with completion notified via callbacks or {@link CompletableFuture}.
 * This class should be used to manage Firestore reads, writes, updates, and deletes from
 * within the Shifty application.
 * </p>
 *
 * <p>
 * <strong>Usage:</strong> Call {@link #getInstance()} to get a new Database instance for operations.
 * </p>
 *
 * @author Eitan Navon
 */
public class Database {

    /**
     * Logging tag for debugging database actions.
     */
    private static final String TAG = "Database";

    /**
     * Reference to the application's Firestore database.
     */
    private FirebaseFirestore db;

    /**
     * Creates a new Database instance with a reference to Firestore.
     */
    public Database() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Returns a new Database instance.
     * <p>
     * Not a singleton—creates a new instance every call.
     * </p>
     *
     * @return a new {@code Database} object.
     */
    public static Database getInstance() {
        return new Database();
    }

    /**
     * Fetches a document from a collection and delivers the data to the given callback.
     *
     * @param COLLECTION_NAME The name of the Firestore collection.
     * @param DOC_NAME        The name (ID) of the document to fetch.
     * @param callback        The callback invoked on successful document fetch, with document data as a {@code Map}.
     */
    public void get(String COLLECTION_NAME, String DOC_NAME, Callback callback) {
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
                        // No document exists; nothing is returned
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * Fetches a document from a collection and delivers the data or error result to the provided callbacks.
     *
     * @param COLLECTION_NAME The name of the Firestore collection.
     * @param DOC_NAME        The document name (ID).
     * @param callback        The callback invoked on success (with document data as a map).
     * @param errorCallback   The callback invoked on failure (with {@code null} as parameter).
     */
    public void get(String COLLECTION_NAME, String DOC_NAME, Callback callback, Callback errorCallback) {
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
                        // Document does not exist; nothing returned
                    }
                } else {
                    errorCallback.onCallback(null);
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * Fetches a single field from a document.
     *
     * @param collectionName The collection to query.
     * @param documentName   The document's name (ID).
     * @param fieldName      The specific field to retrieve.
     * @param callback       The callback invoked with the field's value (or {@code null} if not found).
     */
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

    /**
     * Saves data to a document, replacing any existing fields.
     *
     * @param COLLECTION_NAME The name of the collection.
     * @param DOC_NAME        The document's name (ID).
     * @param data            The data to save as a map of field names to values.
     */
    public void saveData(String COLLECTION_NAME, String DOC_NAME, Map<String, Object> data) {
        db.collection(COLLECTION_NAME).document(DOC_NAME).set(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + DOC_NAME);
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    /**
     * Returns the number of elements (documents) in a Firestore collection, asynchronously.
     *
     * @param collectionName The collection to count.
     * @return A {@link CompletableFuture} that completes with the number of documents in the collection.
     */
    public CompletableFuture<Integer> getCountOfElements(String collectionName) {
        CompletableFuture<Integer> count = new CompletableFuture<>();

        db.collection(collectionName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                count.complete(task.getResult().size());
                Log.d(TAG, "Count of elements in collection: " + count);
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        return count;
    }

    /**
     * Deletes a document from a collection if it exists.
     *
     * @param collctionName The collection name.
     * @param documentName  The document's name (ID).
     * @return {@code true} if the document existed and deletion was requested; {@code false} otherwise.
     */
    public boolean delete(final String collctionName, final String documentName) {
        DocumentReference doc = db.collection(collctionName).document(documentName);
        if (doc.get().isSuccessful()) {
            doc.delete();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Deletes a specific field from a document in a collection.
     *
     * @param collctionName The collection name.
     * @param documentName  The document name (ID).
     * @param fieldName     The field to delete from the document.
     * @return {@code true} if the field was requested for deletion; {@code false} otherwise.
     */
    public boolean deleteField(final String collctionName, final String documentName, final String fieldName) {
        DocumentReference doc = db.collection(collctionName).document(documentName);
        if (doc.get().isSuccessful()) {
            doc.update(fieldName, FieldValue.delete());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Callback interface for returning document data from asynchronous Firestore operations.
     */
    public interface Callback {
        /**
         * Called with the resulting document data (as a {@link Map}) or {@code null} on error.
         *
         * @param data The data retrieved, or {@code null} if not found or on failure.
         */
        void onCallback(Map<String, Object> data);
    }

    /**
     * Callback interface for returning a single field value from a Firestore document.
     */
    public interface FieldCallback {
        /**
         * Called with the field value (could be {@code null}) after retrieval.
         *
         * @param data The field value, or {@code null} if missing or error.
         */
        void onCallback(Object data);
    }
}
