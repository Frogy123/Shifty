package com.example.shifty;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.FirebaseApp;

public class ShiftyApplication extends Application {

    private static final String TAG = "ShiftyApplication";

    public static final MutableLiveData<Boolean> signInStatus = new MutableLiveData<>();

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);


    }
}
