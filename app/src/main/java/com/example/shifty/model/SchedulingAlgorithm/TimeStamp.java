package com.example.shifty.model.SchedulingAlgorithm;

import java.util.Map;

public class TimeStamp {


    int day; //number from 1 - 7
    int startHour; //number from 0 - 21 4am - 1am;
    int endHour; //number from 1 - 22 5am - 2am

    public TimeStamp(int day, int startHour, int endHour){
        this.day = day;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public TimeStamp(Map<String, Object> map){
        try{
            this.day = (int) map.get("day");
            this.startHour = (int) map.get("startHour");
            this.endHour = (int) map.get("endHour");
        }catch(Exception e){
            e.printStackTrace();
            //TODO: handle error
        }

    }

    public TimeStamp() {
        // No-argument constructor required for Firebase
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

    public void setDay(int day){
        this.day = day;
    }

    public void setStartHour(int startHour){
        this.startHour = startHour;
    }

    public void setEndHour(int endHour){
        this.endHour = endHour;
    }


    public Map<String, Object> toMap(){
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("day", day);
        result.put("startHour", startHour);
        result.put("endHour", endHour);
        return result;
    }



}
