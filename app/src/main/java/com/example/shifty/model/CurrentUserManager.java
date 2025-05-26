package com.example.shifty.model;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class CurrentUserManager {

    static CurrentUserManager instance;
    User currentUser;
    Employee currentEmployee;

    MutableLiveData<Boolean> needRefresh = new MutableLiveData<>(false);

    private CurrentUserManager(){
        currentUser = null;
        currentEmployee = null;
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

        if(currentUser.role == Role.EMPLOYEE){
            currentEmployee = new Employee(user.getUid());
            currentEmployee.loadEmp();
        }


    }

    public User getUser(){
        return currentUser;
    }

    public Employee getCurrentEmployee() {
        return currentEmployee;
    }
}
