package com.example.shifty.viewmodel.fragment.Admin;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shifty.model.ScheduleRepository;

import java.util.concurrent.CompletableFuture;

public class ScheduleFragmentViewModel extends ViewModel {

    ScheduleRepository sr = ScheduleRepository.getInstance();

    MutableLiveData<String> errorMsg = new MutableLiveData<>();
    int[][] systemNeeds;


    MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ScheduleFragmentViewModel() {
    }

    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
    }

    public void validateAndUpdateSystemNeed(int day, int empPerHour)  {
        boolean isValid = true;
        try{
            inputCheck(day, empPerHour);
        }catch (Exception e){
            errorMsg.postValue(e.getMessage());
            isValid = false;
        }


        if(isValid) {
            updateSystemNeeds(day, empPerHour);
        }
    }



    private void updateSystemNeeds(int day, int empPerHour) {

        if(isLoading.getValue() == null || !isLoading.getValue()){
            for(int hour = 0; hour < 22; hour++){
                systemNeeds[day][hour] = empPerHour;
            }
            sr.saveSystemNeedsOnDayAndHour(systemNeeds);
        }

    }

    private void inputCheck(int day, int empPerHour) throws Exception{
        if(day < 0 || day > 6){
            throw new Exception("Day must be between 0 and 6");
        }
        if(empPerHour < 1 || empPerHour > 12){
            throw new Exception("Number of employees per hour must be between 1 and 10");
        }
    }

    public void loadSystemNeeds(){
        CompletableFuture<int[][]> data = sr.fetchSystemNeeds();
        isLoading.postValue(true);
        data.thenAccept(result -> {
            this.systemNeeds = result;
            isLoading.postValue(false);
        });
    }



    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

}



