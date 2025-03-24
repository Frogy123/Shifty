package com.example.shifty.model;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class User {

    private static final String COLLECTION_NAME = "Users";
    private static final int MAX_CONSTRAINTS = 2;

    FirebaseAuth mAuth;
    Database db;
    String uid;
    String email;
    String username;
    String password;




    Role role;

    public User(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser userProfile = mAuth.getCurrentUser();
        uid = userProfile.getUid();
        
        db = new Database();
    }

    public User(String uid, String username, String email, String password, Role role){
        this.uid = uid;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        db = new Database();
    }

    public User(String uid){
        this.uid = uid;
    }

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



    public void saveData(){
       db.saveData(COLLECTION_NAME, this.uid, this.toMap());
    }

    public String getUid() {
        return uid;
    }

    public Role getRole() {
        return this.role;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private Map<String, Object> toMap(){
        return Map.of("email", email,
                "username", username,
                "password", password,
                "role", role.toString(),
                "uid", uid);
    }

}
