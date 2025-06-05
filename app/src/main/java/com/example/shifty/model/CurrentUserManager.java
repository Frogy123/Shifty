package com.example.shifty.model;

import androidx.lifecycle.MutableLiveData;

/**
 * Singleton manager for handling the current signed-in user and employee data across the application.
 * <p>
 * This class is responsible for:
 * <ul>
 *     <li>Tracking the currently authenticated user.</li>
 *     <li>Managing the associated {@link Employee} object (if the user is an employee).</li>
 *     <li>Providing live refresh status for UI updates.</li>
 *     <li>Serving as a central point for sign-in and user-related session data.</li>
 * </ul>
 * <p>
 * Usage: Call {@link #getInstance()} to obtain the singleton instance.
 * </p>
 *
 * @author Eitan Navon
 */
public class CurrentUserManager {

    /**
     * Singleton instance of the manager.
     */
    static CurrentUserManager instance;

    /**
     * The currently signed-in {@link User}, or {@code null} if not signed in.
     */
    User currentUser;

    /**
     * The currently signed-in {@link Employee}, or {@code null} if the user is not an employee.
     */
    Employee currentEmployee;

    /**
     * LiveData to indicate when a refresh of user/employee information is needed (for reactive UI).
     */
    MutableLiveData<Boolean> needRefresh = new MutableLiveData<>(false);

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the manager with no current user or employee.
     */
    private CurrentUserManager() {
        currentUser = null;
        currentEmployee = null;
    }

    /**
     * Returns the singleton instance of {@code CurrentUserManager}.
     * Creates it if it does not already exist.
     *
     * @return The singleton {@code CurrentUserManager} instance.
     */
    public static CurrentUserManager getInstance() {
        if (instance == null) {
            instance = new CurrentUserManager();
        }
        return instance;
    }

    /**
     * Checks if there is a user currently signed in.
     *
     * @return {@code true} if a user is signed in; {@code false} otherwise.
     */
    public boolean isSignedIn() {
        return currentUser != null;
    }

    /**
     * Sets the currently signed-in user.
     * If the user is an employee, also loads the corresponding {@link Employee} object.
     *
     * @param user The {@link User} object to sign in as the current user.
     */
    public void signIn(User user) {
        this.currentUser = user;
        if (currentUser.role == Role.EMPLOYEE) {
            currentEmployee = new Employee(user.getUid());
            currentEmployee.loadEmp();
        }
    }

    /**
     * Returns the current signed-in user.
     *
     * @return The {@link User} object for the current user, or {@code null} if none.
     */
    public User getUser() {
        return currentUser;
    }

    /**
     * Returns the current signed-in employee, if the user is an employee.
     *
     * @return The {@link Employee} object, or {@code null} if not applicable.
     */
    public Employee getCurrentEmployee() {
        return currentEmployee;
    }
}
