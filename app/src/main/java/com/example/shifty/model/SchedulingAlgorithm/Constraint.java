package com.example.shifty.model.SchedulingAlgorithm;

public class Constraint{

        int day; //number from 1 - 7
        int startHour; //number from 0 - 21 4am - 1am;
        int endHour; //number from 1 - 22 5am - 2am

        public Constraint(int day, int startHour, int endHour){
            this.day = day;
            this.startHour = startHour;
            this.endHour = endHour;
        }

        public int getDay(){
            return day;
        }

        public int getStartHour(){
            return startHour;
        }

        public int getEndHour(){
            return endHour;
        }


}
