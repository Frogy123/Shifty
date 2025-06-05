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

/**
 * ViewModel responsible for handling user sign-up logic and state management.
 * <p>
 * Handles asynchronous sign-up, referral code validation, password policy enforcement,
 * and exposes sign-in status and error messages to the UI via LiveData.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * SignupViewModel vm = new ViewModelProvider(this).get(SignupViewModel.class);
 * vm.signUp(username, email, password, referalCode);
 * vm.getSignInStatus().observe(getViewLifecycleOwner(), status -> {
 *     // Navigate if status is true
 * });
 * vm.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
 *     // Show error
 * });
 * </pre>
 *
 * @author Eitan Navon
 * @see RefferalCodesManager
 * @see Database
 * @see FirebaseAuth
 * @see MutableLiveData
 */
public class SignupViewModel extends ViewModel {

    /** Firebase authentication instance used for creating new users. */
    FirebaseAuth auth = FirebaseAuth.getInstance();

    /** LiveData for propagating error messages to the UI. */
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    /** LiveData tracking whether the user has signed in successfully. */
    private final MutableLiveData<Boolean> signInStatus = ShiftyApplication.signInStatus;

    /** Reference to the database instance. */
    private final Database db = Database.getInstance();

    /**
     * Handles the sign-up flow, including referral code validation and password policy enforcement.
     * On successful sign-up, updates sign-in status; on failure, updates error message.
     *
     * @param username    the username chosen by the user (currently not used for account creation)
     * @param email       the user's email address (used as account identifier)
     * @param password    the password for the account (must satisfy password policy)
     * @param referalCode the referral code to validate (must be valid)
     * @throws IllegalArgumentException if the password is invalid
     * @see RefferalCodesManager#checkCode(String, String)
     * @see FirebaseAuth#createUserWithEmailAndPassword(String, String)
     * @return void
     */
    public void signUp(String username, String email, String password, String referalCode) {
        RefferalCodesManager.checkCode(referalCode, email).thenAccept(isValid -> {
            if (isValid) {
                try {
                    checkPassword(password);
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                signInStatus.setValue(true);
                            });
                } catch (IllegalArgumentException e) {
                    errorMessage.setValue(e.getMessage());
                }
            } else {
                errorMessage.setValue("Invalid referral code");
            }
        });
    }

    /**
     * Validates the password against minimum and maximum length constraints.
     *
     * @param password the password to validate
     * @throws IllegalArgumentException if the password is too short or too long
     */
    private void checkPassword(String password) throws IllegalArgumentException {
        if (password.length() < 8 || password.length() > 20) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and at most 20 characters long");
        }
    }

    /**
     * Returns LiveData for error messages generated during sign-up.
     *
     * @return a {@link MutableLiveData} containing the current error message
     */
    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns LiveData for observing the current sign-in status.
     *
     * @return a {@link MutableLiveData} indicating whether sign-in was successful
     */
    public MutableLiveData<Boolean> getSignInStatus() {
        return signInStatus;
    }
}
