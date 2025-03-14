package com.example.shifty.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shifty.model.Role;

import com.example.shifty.model.CurrentUserManager;
import com.example.shifty.model.Database;
import com.example.shifty.model.RefferalCodesManager;
import com.example.shifty.model.User;
import com.google.firebase.auth.FirebaseAuth;

public class SignupViewModel extends ViewModel{

    FirebaseAuth auth = FirebaseAuth.getInstance();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> signInStatus = new MutableLiveData<>();
    private final Database db = Database.getInstance();



    public void signUp(String uid, String email, String password, String referalCode){
        boolean canSignUp = true;
        try{
            RefferalCodesManager.checkReffearalCode(uid, referalCode);
            checkPassword(password);
        }catch (IllegalArgumentException e){
            errorMessage.setValue(e.getMessage());
            canSignUp = false;
        }

        if(canSignUp){
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            User user = new User(uid, email, password, Role.EMPLOYEE);
                            db.saveUser(user);

                            CurrentUserManager.getInstance().signIn();

                            signInStatus.setValue(true);

                            RefferalCodesManager.useRefferalCode(uid, referalCode);
                        } else {
                            errorMessage.setValue("Failed to sign up");
                            signInStatus.setValue(false);
                        }
                    });
        }
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
