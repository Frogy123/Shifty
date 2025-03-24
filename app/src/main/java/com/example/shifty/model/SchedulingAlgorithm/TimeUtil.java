package com.example.shifty.model.SchedulingAlgorithm;

public class TimeUtil {

    //@params startHour: the starting hour of the shift
    //@params day: the day of the week
    //@params hours: the number of hours to add to the startHour
    //@return an array of size 2 containing the [day, hour] after adding the hours
    public static int[] addHour(int startHour, int day, int hours){
        int[] result = new int[2];
        int newHour = startHour + hours;
        if(newHour >= 24){
            day = (day + 1) % 7;
            newHour = newHour - 22;
        }
        result[0] = day;
        result[1] = newHour;
        return result;

    }
}
