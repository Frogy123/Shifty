package com.example.shifty.viewmodel.fragment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shifty.model.CurrentUserManager;
import com.example.shifty.model.Employee;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ShiftsViewModel extends ViewModel {


    Employee currEmp;
    MutableLiveData<String> errorMsg = new MutableLiveData<>();



    public ShiftsViewModel() {
        currEmp  = CurrentUserManager.getInstance().getCurrentEmployee();
    }

    public String monthYearFromDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public ArrayList<LocalDate> daysInWeek(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = sundayForDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);

        while(current.isBefore(endDate)){
            days.add(current);
            current = current.plusDays(1);
        }
        

        return days;
    }

    private static LocalDate sundayForDate(LocalDate current) {
        LocalDate onWeekAgo =  current.minusWeeks(1);

        while(current.isAfter(onWeekAgo)){
            if(current.getDayOfWeek() == DayOfWeek.SUNDAY)
                return current;

            current = current.minusDays(1);
        }

        //unreachable because there will allways be a sunday in a week
        return null;
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
