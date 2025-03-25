package com.example.shifty.model;

import com.example.shifty.model.SchedulingAlgorithm.Constraint;
import com.example.shifty.model.SchedulingAlgorithm.Shift;

import java.util.ArrayList;

public class Employee extends User implements Comparable{


    public static int T_REST = 8;
    public static int W_REST = 2;
    public static int W_AGE = 2;


    ArrayList<Constraint> constraints;
    ArrayList<String> specialProperties;

    int age;
    int maxShiftsPerWeek;

    ArrayList<Shift> shifts;


    @Override
    public int compareTo(Object o) {
        if(o instanceof Employee){
            return this.getPriorityScore() - ((Employee) o).getPriorityScore();
        }
        return 0;
    }

    public int getPriorityScore(){
        int priorityScore  = 0;

        //TODO implement priority score calculation

        return priorityScore;
    }

    public boolean isAvailable(int day, int hour) {
        for(Constraint c : constraints){
            if(c.getDay() == day){
                if(c.getStartHour() <= hour && c.getEndHour() >= hour){
                    return false;
                }
            }
        }

        return shifts.size() < maxShiftsPerWeek;

    }

    public void addConstraint(int day, int startHour, int endHour) {
        Constraint c = new Constraint(day, startHour, endHour);
        constraints.add(c);
    }

    //@postCondition: the function will return true if the employee have special property.
    public boolean haveSpecialProperties(){
        return specialProperties.size() > 0;
    }

    //clears unneeded constraints
    private void clearConstraints(int day, int hour) {
        constraints.clear();
    }

    public void addShift(Shift shift) {
        shifts.add(shift);
    }
}
