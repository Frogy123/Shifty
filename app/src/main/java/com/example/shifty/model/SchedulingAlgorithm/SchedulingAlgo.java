package com.example.shifty.model.SchedulingAlgorithm;

import com.example.shifty.model.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class SchedulingAlgo {

    private static final int MAX_HOURS = 8;
    private static final int MAX_DAYS = 7;
    private static final int END_HOUR = 22;

    private PriorityQueue<Employee> pq;
    private ArrayList<Employee> rest;

    private int[][] systemConstraints;

    private Schedule schedule;

    public SchedulingAlgo(List<Employee> employees,int[][] systemConstraints) {
        pq = new PriorityQueue<Employee>(employees);
        this.systemConstraints = systemConstraints;
    }

    public void init(List<Employee> employees,int[][] systemConstraints) {
        pq = new PriorityQueue<Employee>(employees);
        this.systemConstraints = systemConstraints;
    }

    public Schedule run(){

        this.schedule = new Schedule();

        ArrayList<Employee> rest = new ArrayList<Employee>();


        for(int day=0; day<MAX_DAYS;day++){
            for(int hour=0; hour<22;hour++){
                while(systemConstraints[day][hour] > 0){


                    Employee e = pq.poll();
                    if(e.isAvailable(day,hour)){
                        addEmployee(e,day,hour);
                    }


                }
            }
        }

        return this.schedule;
    }

    private void addEmployee(Employee e, int day, int startHour){

        int endHourOfShift = computeEndHour(e,startHour,day);
        this.schedule.addShift(e, day,startHour, endHourOfShift);
        int[] constraint = TimeUtil.addHour(endHourOfShift, day,8);//connstraint[0] is the day, constraint[1] is the hour

        if(constraint[0] != day){
            e.addConstraint(day,startHour, END_HOUR);
            e.addConstraint(constraint[0], 0, constraint[1]);
        }
        else{
            e.addConstraint(day,startHour,END_HOUR);
        }


    }

    private int computeEndHour(Employee e, int startHour, int day){
       int index = startHour;
        for(int i=startHour; i<startHour + MAX_HOURS - 1; i++){
            if(!e.isAvailable(day,i)){
                return i;
            }
        }
        return startHour + MAX_HOURS -1;
    }





}
