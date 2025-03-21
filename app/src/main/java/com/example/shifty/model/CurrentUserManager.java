package com.example.shifty.model;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class CurrentUserManager {

    static CurrentUserManager instance;
    User currentUser;

    private CurrentUserManager(){
        currentUser = new User();
    }

    public static CurrentUserManager getInstance(){
        if(instance == null){
            instance = new CurrentUserManager();
        }
        return instance;
    }


    public boolean isSignedIn(){
            return currentUser != null;
    }

    public void signIn(User user){
        this.currentUser = user;
    }

    public User getUser(){
        return currentUser;
    }
}
