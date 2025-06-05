package com.example.shifty.viewmodel.fragment.Employee;

import androidx.lifecycle.ViewModel;

import com.example.shifty.model.CurrentUserManager;
import com.example.shifty.model.User;

/**
 * ViewModel for displaying and managing user statistics (email, name, role, password)
 * for the current signed-in user.
 * <p>
 * Loads the user's data from the singleton {@link CurrentUserManager} and provides getters
 * for the UI to access this information.
 * </p>
 *
 * <b>Related:</b>
 * <ul>
 *     <li>{@link CurrentUserManager}</li>
 *     <li>{@link User}</li>
 * </ul>
 *
 * @author Eitan Navon
 */
public class StatsViewModel extends ViewModel {

    /** The user's email. */
    String email;
    /** The user's name. */
    String name;
    /** The user's role as a string. */
    String role;
    /** The user's password. */
    String password;

    /**
     * Loads the current user's data from the {@link CurrentUserManager}.
     * Sets the ViewModel fields for use in the UI.
     *
     * @throws NullPointerException if there is no signed-in user.
     */
    public void loadData() {
        User user = CurrentUserManager.getInstance().getUser();

        email = user.getEmail();
        name = user.getUsername();
        role = user.getRole().toString();
        password = user.getPassword();
    }

    /**
     * Gets the user's email.
     *
     * @return The user's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the user's name.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the user's role as a string.
     *
     * @return The user's role.
     */
    public String getRole() {
        return role;
    }

    /**
     * Gets the user's password.
     *
     * @return The user's password.
     */
    public String getPassword() {
        return password;
    }
}
