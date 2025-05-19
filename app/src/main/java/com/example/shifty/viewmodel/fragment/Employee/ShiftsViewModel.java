package com.example.shifty.viewmodel.fragment.Employee;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shifty.model.CurrentUserManager;
import com.example.shifty.model.Employee;
import com.example.shifty.model.SchedulingAlgorithm.TimeUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ShiftsViewModel extends ViewModel {


    Employee currEmp;
    MutableLiveData<String> errorMsg = new MutableLiveData<>();



    public ShiftsViewModel() {
        currEmp  = CurrentUserManager.getInstance().getCurrentEmployee();
    }



    public void onConstraintAdded(int day, int startHour, int endHour) {
        // Handle the constraint added event here
        // For example, you can update the schedule or notify the user
        if(currEmp != null){
            currEmp.addConstraint(day, startHour, endHour);
        }
        currEmp.save();

    }

    public void checkEmployeeConstraints(int day, int startHour, int endHour)throws Exception {

        if(currEmp.numberOfConstraints() >= 2){
            throw new Exception("You can only have 2 constraints");
        }


    }

    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
    }
}
