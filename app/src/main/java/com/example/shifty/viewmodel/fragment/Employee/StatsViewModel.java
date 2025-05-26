package com.example.shifty.viewmodel.fragment.Employee;

import androidx.lifecycle.ViewModel;

import com.example.shifty.model.CurrentUserManager;
import com.example.shifty.model.User;

public class StatsViewModel extends ViewModel {

    String email;
    String name;
    String role;
    String password;


    public void loadData() {
        User user = CurrentUserManager.getInstance().getUser();

        email = user.getEmail();
        name = user.getUsername();
        role = user.getRole().toString();
        password = user.getPassword();


    }

    //getters
    public String getEmail() {
        return email;
    }
    public String getName() {
        return name;
    }
    public String getRole() {
        return role;
    }
    public String getPassword() {
        return password;
    }
}
