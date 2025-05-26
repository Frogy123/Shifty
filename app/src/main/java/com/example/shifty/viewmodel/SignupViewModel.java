package com.example.shifty.viewmodel;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shifty.ShiftyApplication;
import com.example.shifty.model.Employee;
import com.example.shifty.model.Role;

import com.example.shifty.model.CurrentUserManager;
import com.example.shifty.model.Database;
import com.example.shifty.model.RefferalCodesManager;
import com.example.shifty.model.User;
import com.google.firebase.auth.FirebaseAuth;

public class SignupViewModel extends ViewModel{

    FirebaseAuth auth = FirebaseAuth.getInstance();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> signInStatus = ShiftyApplication.signInStatus;
    private final Database db = Database.getInstance();



    public void signUp(String username, String email, String password, String referalCode) {
        RefferalCodesManager.checkCode(referalCode, email).thenAccept(isValid -> {
            if (isValid) {
                try {
                    checkPassword(password);
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                signInStatus.setValue(true);

                            });
                } catch (IllegalArgumentException e)     {
                    errorMessage.setValue(e.getMessage());
                }
            } else {
                errorMessage.setValue("Invalid referral code");
            }
        });
    }






    private void checkPassword(String password) throws IllegalArgumentException {
        if (password.length() < 8 || password.length() > 20) {
            throw new IllegalArgumentException("Password must be at least 6 characters long and at most 20 characters long");
        }
    }

    public MutableLiveData<String> getErrorMessage(){
        return errorMessage;
    }

    public MutableLiveData<Boolean> getSignInStatus() {
        return signInStatus;
    }
}
