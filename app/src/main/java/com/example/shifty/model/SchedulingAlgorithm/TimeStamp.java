package com.example.shifty.model.SchedulingAlgorithm;

import com.google.type.DateTime;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

public class TimeStamp {


    int day; //number from 1 - 7
    int startHour; //number from 0 - 21 4am - 1am;
    int endHour; //number from 1 - 22 5am - 2am

    long date;

    public TimeStamp(int day, int startHour, int endHour, long date){
        this.day = day;
        this.startHour = startHour;
        this.endHour = endHour;
        this.date = date;
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
    public long getDate(){
        return date;
    }

    public void setDate(long date){
        this.date = date;
    }

    public LocalDate getDateObject(){
        return LocalDate.ofEpochDay(date);
    }

    public boolean isConsectuive(TimeStamp t){
        return this.getDay() == t.getDay() && this.getEndHour() == t.getStartHour();
    }


    public Map<String, Object> toMap(){
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("day", day);
        result.put("startHour", startHour);
        result.put("endHour", endHour);
        return result;
    }



}
