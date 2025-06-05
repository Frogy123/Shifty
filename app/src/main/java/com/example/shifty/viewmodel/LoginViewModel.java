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

/**
 * ViewModel for handling user login logic and state.
 * <p>
 * Handles Firebase authentication, user profile loading, and communicates
 * sign-in status and errors via {@link LiveData} observables.
 * </p>
 *
 * <b>Usage:</b> Attach this ViewModel to a login screen and observe
 * {@link #getSignInStatus()} and {@link #getErrorMessage()} for UI updates.
 *
 * @author Eitan Navon
 * @see FirebaseAuth
 * @see User
 * @see CurrentUserManager
 */
public class LoginViewModel extends ViewModel {

    /** Firebase authentication instance. */
    private FirebaseAuth mAuth;

    /** LiveData tracking the user's sign-in status (true if signed in, false otherwise). */
    private final MutableLiveData<Boolean> signInStatus = ShiftyApplication.signInStatus;

    /** LiveData holding the latest error message, if any. */
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    /**
     * Default constructor. Initializes Firebase authentication.
     */
    public LoginViewModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Attempts to sign in a user using Firebase Authentication with the provided credentials.
     * <ul>
     *     <li>If sign-in is successful, loads the user profile and updates {@link #signInStatus} to true.</li>
     *     <li>If sign-in fails, sets an error message and {@link #signInStatus} to false.</li>
     * </ul>
     *
     * @param email    the email address of the user
     * @param password the user's password
     * @see FirebaseAuth#signInWithEmailAndPassword(String, String)
     * @see CurrentUserManager
     * @see User#loadData()
     */
    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (CurrentUserManager.getInstance().getUser() != null) {
                            // If the user is already signed in, update the current user UID and data.
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

    /**
     * Returns a LiveData object representing the user's sign-in status.
     * Observe this to be notified of login success or failure.
     *
     * @return LiveData<Boolean> true if signed in, false otherwise
     */
    public LiveData<Boolean> getSignInStatus() {
        return signInStatus;
    }

    /**
     * Returns a LiveData object holding the latest login error message.
     * Observe this to display error messages to the user.
     *
     * @return LiveData<String> containing the error message, or null if no error
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
