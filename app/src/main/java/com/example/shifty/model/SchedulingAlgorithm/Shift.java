package com.example.shifty.model.SchedulingAlgorithm;
import java.util.Map;
public class Shift extends TimeStamp{


    public Shift(int day, int startHour, int endHour){
        super(day, startHour, endHour);
    }
    // Constructor for creating a Shift object from a map
    public Shift(Map<String, Object> map){
        super(map);
    }

}
