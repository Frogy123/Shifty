package com.example.shifty.viewmodel.dialogFragment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConstraintDialogViewModel extends ViewModel {




    MutableLiveData<String> ErrorMsg = new MutableLiveData<>();


    public ConstraintDialogViewModel() {
        // Constructor
    }
    public void addConstraint(int day, int startHour, int endHour) {

        try{


        }catch(Exception e){
            ErrorMsg.postValue(e.getMessage());
        }

    }




}
