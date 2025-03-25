package com.example.shifty.model.SchedulingAlgorithm;

import java.util.HashMap;
import java.util.Map;

public class Shift {

    int day;
    int startHour;
    int endHour;


    public Shift(int _day, int _startHour, int _endHour){
        this.day = _day;
        this.startHour = _startHour;
        this.endHour = _endHour;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("day", day);
        data.put("startHour", startHour);
        data.put("endHour", endHour);


        return data;

    }
}
