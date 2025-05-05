package com.example.shifty.model.SchedulingAlgorithm;

import androidx.annotation.Nullable;

import com.example.shifty.model.Employee;

import java.util.ArrayList;

public class Schedule {

    ArrayList<Employee>[][] hourSchedule;


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
}
