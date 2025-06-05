package com.example.shifty;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.FirebaseApp;

/**
 * Custom {@link Application} class for initializing global state and third-party libraries
 * for the Shifty app.
 * <p>
 * Initializes Firebase when the app starts and provides a global {@link MutableLiveData}
 * for tracking user sign-in status across the app.
 * </p>
 *
 * <b>Related:</b>
 * <ul>
 *     <li>{@link FirebaseApp}</li>
 *     <li>{@link MutableLiveData}</li>
 * </ul>
 *
 * @author Eitan Navon
 */
public class ShiftyApplication extends Application {

    /** Tag for logging and debugging. */
    private static final String TAG = "ShiftyApplication";

    /**
     * Global LiveData for observing user sign-in status throughout the app.
     * Value is {@code true} if sign-in was successful, {@code false} otherwise.
     */
    public static final MutableLiveData<Boolean> signInStatus = new MutableLiveData<>();

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects have been created.
     * <p>
     * Initializes Firebase for use throughout the app.
     * </p>
     *
     * @see FirebaseApp#initializeApp(android.content.Context)
     */
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
