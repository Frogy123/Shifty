package com.example.shifty.model.SchedulingAlgorithm;

public class Constraint extends TimeStamp{

        int day; //number from 1 - 7
        int startHour; //number from 0 - 21 4am - 1am;
        int endHour; //number from 1 - 22 5am - 2am

        public Constraint(int day, int startHour, int endHour){
            super(day, startHour, endHour);
        }


}
