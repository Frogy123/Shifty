package com.example.shifty.model.SchedulingAlgorithm;
import java.util.Date;
import java.util.Map;
public class Shift extends TimeStamp{


    public Shift(int day, int startHour, int endHour, long date){
        super(day, startHour, endHour, date);
    }
    // Constructor for creating a Shift object from a map
    public Shift(Map<String, Object> map){
        super(map);
    }

}
