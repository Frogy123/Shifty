package com.example.shifty.model.SchedulingAlgorithm;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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

    public static LocalDate sundayForDate(LocalDate current) {
        LocalDate onWeekAgo =  current.minusWeeks(1);

        while(current.isAfter(onWeekAgo)){
            if(current.getDayOfWeek() == DayOfWeek.SUNDAY)
                return current;

            current = current.minusDays(1);
        }

        //unreachable because there will allways be a sunday in a week
        return null;
    }

    public static String getDayOfWeek(int day) {
        switch (day) {
            case 0:
                return "Sunday";
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            default:
                throw new IllegalArgumentException("Invalid day: " + day);
        }
    }

    public static String monthYearFromDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public static ArrayList<LocalDate> daysInWeek(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = TimeUtil.sundayForDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);

        while(current.isBefore(endDate)){
            days.add(current);
            current = current.plusDays(1);
        }



        return days;
    }
}
