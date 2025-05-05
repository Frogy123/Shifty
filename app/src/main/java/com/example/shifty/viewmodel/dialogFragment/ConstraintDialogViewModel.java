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

    private void checkInput(int startHour, int endHour) throws Exception{
        if(startHour >= endHour) throw new Exception("Start hour must be less than end hour");
        if(startHour < 0 || startHour > 23) throw new Exception("Start hour must be between 0 and 23");
        if(endHour < 0 || endHour > 23) throw new Exception("End hour must be between 0 and 23");
    }




}
