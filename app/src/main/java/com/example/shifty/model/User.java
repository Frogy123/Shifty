package com.example.shifty.model;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a user of the Shifty application.
 * Provides methods for loading and saving user data to a database, and basic user profile management.
 *
 * <p>
 * User data is stored and retrieved from a remote database using the {@link Database} helper class.
 * Authentication is managed using Firebase Authentication.
 * </p>
 *
 * Example usage:
 * <pre>
 *   User user = new User("uid", "username", "email", "password", Role.ADMIN);
 *   user.saveData();
 * </pre>
 *
 * @author Eitan Navon
 * @see Database
 * @see Role
 * @see FirebaseAuth
 */
public class User {

    /** The collection name used for storing users in the database. */
    private static final String COLLECTION_NAME = "Users";

    /** The maximum number of constraints for the user (not used in this class). */
    private static final int MAX_CONSTRAINTS = 2;

    /** Firebase authentication instance. */
    FirebaseAuth mAuth;

    /** Database helper instance. */
    Database db;

    /** The user's unique identifier (UID). */
    String uid;

    /** The user's email address. */
    String email;

    /** The user's display name. */
    String username;

    /** The user's password. */
    String password;

    /** The user's role (admin, employee, etc.). */
    Role role;

    /**
     * Default constructor. Initializes Firebase authentication and attempts to load the current user.
     *
     * @throws NullPointerException if there is no authenticated user
     */
    public User() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser userProfile = mAuth.getCurrentUser();
        uid = userProfile.getUid();
        db = new Database();
    }

    /**
     * Constructs a user with all relevant fields.
     *
     * @param uid      the unique identifier for the user
     * @param username the user's display name
     * @param email    the user's email address
     * @param password the user's password
     * @param role     the user's {@link Role}
     */
    public User(String uid, String username, String email, String password, Role role) {
        this.uid = uid;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        db = new Database();
    }

    /**
     * Constructs a user with only a UID. Used for loading or reference.
     *
     * @param uid the unique identifier for the user
     */
    public User(String uid) {
        this.uid = uid;
    }

    /**
     * Loads this user's data asynchronously from the database.
     *
     * @return a {@link CompletableFuture} which completes with this {@link User} instance
     *         after loading from the database
     * @throws RuntimeException if loading fails due to a database error
     * @see Database#get(String, String, Database.DataCallback)
     */
    public CompletableFuture<User> loadData() {
        CompletableFuture<User> thisUser = new CompletableFuture<>();
        db = new Database();
        db.get(COLLECTION_NAME, uid, data -> {
            this.email = (String) data.get("email");
            this.username = (String) data.get("username");
            this.password = (String) data.get("password");
            this.role = Role.valueOf((String) data.get("role"));
            thisUser.complete(this);
        });

        return thisUser;
    }

    /**
     * Saves the user's data to the database.
     *
     * @throws RuntimeException if saving fails due to a database error
     * @see Database#saveData(String, String, Map)
     */
    public void saveData() {
        db.saveData(COLLECTION_NAME, this.uid, this.toMap());
    }

    /**
     * Gets the unique identifier (UID) of the user.
     *
     * @return the user's UID
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the unique identifier (UID) of the user.
     *
     * @param uid the new UID to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Gets the user's role.
     *
     * @return the user's {@link Role}
     */
    public Role getRole() {
        return this.role;
    }

    /**
     * Gets the user's email address.
     *
     * @return the user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the user's username (display name).
     *
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the user's password.
     *
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Converts this user's data into a map for database storage.
     *
     * @return a {@link Map} representing this user's fields
     */
    private Map<String, Object> toMap() {
        return Map.of(
                "email", email,
                "username", username,
                "password", password,
                "role", role.toString(),
                "uid", uid
        );
    }
}
