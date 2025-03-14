package com.example.shifty.model;

import com.example.shifty.model.Role;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {



    FirebaseAuth mAuth;
    Database db;
    String uid;
    String email;
    String name;
    Role role;

    public User(){
        FirebaseUser userProfile = mAuth.getCurrentUser();
        uid = userProfile.getUid();
        db = new Database();
    }

    public User(String uid, String email, String name, Role role){
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.role = role;
        db = new Database();
    }

    public void loadData(){
        db.getUser(this.uid, user -> {

            if (user != null) {
                this.email = user.email;
                this.name = user.name;
                this.role = user.role;
                //todo update the information and add more stuff
            }

        });

    }

    public void saveData(){
        db.saveUser(this);
    }

    public String getUid() {
        return uid;
    }
}
