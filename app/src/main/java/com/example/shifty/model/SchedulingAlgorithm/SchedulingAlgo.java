package com.example.shifty.model.SchedulingAlgorithm;

import com.example.shifty.model.Employee;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class SchedulingAlgo {

    private static final int MAX_HOURS = 8;
    private static final int MAX_DAYS = 7;
    private static final int END_HOUR = 22;

    private static final int  SPECIAL_EMPLOYEE_NEEDED = 3;

    private LinkedList<Employee> employees;

    private PriorityQueue<Employee> pq;
    private ArrayList<Employee> rest;

    private int[][] systemConstraints;
    private Schedule schedule;


    public SchedulingAlgo(LinkedList<Employee> _employees,int[][] systemConstraints) {
        this.employees = _employees;
        pq = new PriorityQueue<Employee>(employees);
        this.systemConstraints = systemConstraints;
    }

    public void init(List<Employee> employees,int[][] systemConstraints) {
        pq = new PriorityQueue<Employee>(employees);
        this.systemConstraints = systemConstraints;
    }

    public Schedule run(){

        this.schedule = new Schedule();

        LinkedList<Employee> special = getSpecialEmployees();
        PriorityQueue<Employee> specialPQ = new PriorityQueue<>(special);
        LinkedList<Employee> alreadyInShift = new LinkedList<>();


        //start with the special employees
        for(int day=0; day<MAX_DAYS;day++){
            for(int hour=0; hour<22;hour++){

                //how much special employees we need for the specific hour.
                int numOfEmployeesNeeded = Math.min(systemConstraints[day][hour], SPECIAL_EMPLOYEE_NEEDED );


                while(this.schedule.hourSchedule[day][hour].size() < numOfEmployeesNeeded){
                    Employee e = specialPQ.poll();
                    int endHour = computeEndHour(e, hour, day);

                    if(e.isAvailable(day,hour)){
                        addEmployee(e, day, hour, endHour);
                        alreadyInShift.add(e);
                    }


                }

            }

            for(Employee e:alreadyInShift) pq.add(e);
        }

        //continue with the regular employees.


        return this.schedule;
    }

    private LinkedList<Employee> getSpecialEmployees() {
        LinkedList<Employee> special = new LinkedList<>();
        for(Employee e: this.employees){
            if(e.haveSpecialProperties()) special.add(e);
        }
        return special;
    }

    private void addEmployee(Employee e, int day, int startHour, int endHourOfShift){

        this.schedule.addShift(e, day,startHour, endHourOfShift);
        calculateAddConstraint(e, day, startHour, endHourOfShift);

        Shift currShift = new Shift(day, startHour, endHourOfShift);
        e.addShift(currShift);

    }

    //dynamicly add constratints to the system.
    //add based on the shift the employer those
    private void calculateAddConstraint(Employee e, int day, int startHour, int endHourOfShift){
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
