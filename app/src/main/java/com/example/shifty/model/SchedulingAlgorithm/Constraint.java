package com.example.shifty.model.SchedulingAlgorithm;

import java.util.Date;
import java.util.Map;

public class Constraint extends TimeStamp{



        public Constraint(int day, int startHour, int endHour, long date){
            super(day, startHour, endHour, date);
        }
        public Constraint(){
            super();
        }
        public Constraint(Map<String, Object> map){
            super(map);
        }




}

