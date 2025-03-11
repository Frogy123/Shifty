package com.example.shifty.model;

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

    public void signIn() {
        currentUser = new User();
        currentUser.loadData();
    }
}
