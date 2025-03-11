package com.example.shifty.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shifty.model.CurrentUserManager;
import com.google.firebase.auth.FirebaseAuth;

public class LoginViewModel extends ViewModel {

    private FirebaseAuth mAuth;
    private MutableLiveData<Boolean> signInStatus = new MutableLiveData<>();


    public LoginViewModel() {
        mAuth = FirebaseAuth.getInstance();
    }


    public void signIn(String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CurrentUserManager.getInstance().signIn();
                        signInStatus.setValue(true);
                    } else {
                        signInStatus.setValue(false);
                    }
                });
    }

    public LiveData<Boolean> getSignInStatus() {
        return signInStatus;
    }
}