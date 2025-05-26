package com.example.shifty.model.SchedulingAlgorithm;

import androidx.annotation.Nullable;

import com.example.shifty.model.Employee;
import com.example.shifty.model.EmployeeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Schedule {

    ArrayList<Employee>[][] schedule;


    public Schedule(){
        schedule = new ArrayList[7][22];
        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 22; j++){
                schedule[i][j] = new ArrayList<Employee>();
            }
        }
    }
    public void addShift(Employee e, int day, int startHour, int endHour){
        for(int i = startHour; i < endHour; i++){
            schedule[day][i].add(e);
        }
    }

    public void addEmployeeHour(Employee e, int day, int hour){
        schedule[day][hour].add(e);
    };

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    //from the scheulde initiallize shifts to Employee
    public void ScheduleToShifts(){

       /* HashMap<Employee, Integer> employeeMap = new HashMap<Employee, Integer>();
        List<Employee> allEmployee = EmployeeManager.getInstance().getEmployees();

        for(Employee e : allEmployee){
            employeeMap.put(e, -1);
        }*/

        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 22; j++){
                for(Employee e : schedule[i][j]){
                    e.addShift(i, j, j + 1);
                }

            }
        }
        EmployeeManager.getInstance().saveAllEmployees(); //save all employees to the database
    }
}
