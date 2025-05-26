package com.example.shifty.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shifty.ShiftyApplication;
import com.example.shifty.model.CurrentUserManager;
import com.example.shifty.model.User;
import com.google.firebase.auth.FirebaseAuth;

public class LoginViewModel extends ViewModel {

    private FirebaseAuth mAuth;
    private final MutableLiveData<Boolean> signInStatus = ShiftyApplication.signInStatus;

    private MutableLiveData<String> errorMessage = new MutableLiveData<>();


    public LoginViewModel() {
            mAuth = FirebaseAuth.getInstance();
    }


    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(CurrentUserManager.getInstance().getUser() != null) {
                            // If the user is already signed in, we can just update the current user
                            CurrentUserManager.getInstance().getUser().setUid(mAuth.getCurrentUser().getUid());
                            CurrentUserManager.getInstance().getUser().saveData();
                            signInStatus.setValue(true);
                            return;
                        }
                        User currUser = new User(mAuth.getCurrentUser().getUid());
                        currUser.loadData().thenAccept(user -> {
                            CurrentUserManager.getInstance().signIn(user);
                            signInStatus.setValue(true);
                        });

                    } else {
                        errorMessage.setValue("Failed to sign in");
                        signInStatus.setValue(false);
                    }
                });


    }

    public LiveData<Boolean> getSignInStatus() {
        return signInStatus;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

}