package com.example.shifty.viewmodel.fragment.Employee;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shifty.model.Update;
import com.example.shifty.model.UpdateManager;

import java.util.Date;
import java.util.Observable;
import java.util.concurrent.CompletableFuture;

public class UpdatesViewModel extends ViewModel {

    MutableLiveData<String> errorMsg;

    public UpdatesViewModel() {
        errorMsg = new MutableLiveData<>();
    }

    public void addUpadate(String title, String description, boolean isPinned, long date) {

        Update newUpdate = new Update(title, description, isPinned, new Date(date));
        CompletableFuture<Boolean> isDone =  UpdateManager.getInstance().addNewUpdate(newUpdate);

        isDone.thenAccept((result) -> {
            if(result) errorMsg.postValue("Update added successfully");
            else errorMsg.postValue("Failed to add update");
        });


    }

    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
    }



}
