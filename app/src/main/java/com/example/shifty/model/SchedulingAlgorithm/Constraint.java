package com.example.shifty.model.SchedulingAlgorithm;

import java.util.Map;

public class Constraint extends TimeStamp{



        public Constraint(int day, int startHour, int endHour){
            super(day, startHour, endHour);
        }
        public Constraint(){
            super();
        }
        public Constraint(Map<String, Object> map){
            super(map);
        }




}
