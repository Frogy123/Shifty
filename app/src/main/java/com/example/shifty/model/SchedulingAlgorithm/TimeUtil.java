package com.example.shifty.model.SchedulingAlgorithm;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Utility class for various time and date operations related to scheduling and shifts.
 * <p>
 * Provides helper functions for time calculations, formatting, and week navigation.
 * </p>
 *
 * <ul>
 *     <li>Add hours to a time and day, with day rollover handling</li>
 *     <li>Get the first Sunday for a given date</li>
 *     <li>Convert day indices to readable day names</li>
 *     <li>Format dates and times as strings</li>
 *     <li>Generate lists of days in a week</li>
 *     <li>Find the next occurrence of a given day of week</li>
 * </ul>
 *
 * <p>This class contains only static utility methods and is not meant to be instantiated.</p>
 *
 * @author Eitan Navon
 */
public class TimeUtil {

    /**
     * Adds a specified number of hours to a given start hour and day, with correct handling of day rollover.
     *
     * @param startHour The starting hour (0-23).
     * @param day The day of the week as an integer (0=Sunday, ..., 6=Saturday).
     * @param hours The number of hours to add.
     * @return An array of size 2: [new day (0-6), new hour (0-23)] after adding the hours.
     */
    public static int[] addHour(int startHour, int day, int hours) {
        int[] result = new int[2];
        int newHour = startHour + hours;
        if (newHour >= 24) {
            day = (day + 1) % 7;
            newHour = newHour - 22;
        }
        result[0] = day;
        result[1] = newHour;
        return result;
    }

    /**
     * Finds the most recent Sunday (or the given date itself if it is a Sunday) before or on the specified date.
     *
     * @param current The reference {@link LocalDate}.
     * @return The {@link LocalDate} of the latest Sunday before or on {@code current}.
     */
    public static LocalDate sundayForDate(LocalDate current) {
        LocalDate onWeekAgo = current.minusWeeks(1);
        while (current.isAfter(onWeekAgo)) {
            if (current.getDayOfWeek() == DayOfWeek.SUNDAY)
                return current;
            current = current.minusDays(1);
        }
        // Unreachable because there is always a Sunday in the previous week.
        return null;
    }

    /**
     * Returns the English name of the day of week for a given day index.
     *
     * @param day The day index (0=Sunday, 1=Monday, ..., 6=Saturday).
     * @return A string with the day's English name ("Sunday" to "Saturday").
     * @throws IllegalArgumentException If the day index is outside the range 0-6.
     */
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

    /**
     * Formats a {@link LocalDate} to a string with the pattern "MMMM yyyy" (e.g., "May 2025").
     *
     * @param date The date to format.
     * @return The formatted string with full month and year.
     */
    public static String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    /**
     * Returns a list of all {@link LocalDate} days in the week of the specified date,
     * starting from the most recent Sunday.
     *
     * @param selectedDate The reference date.
     * @return An {@link ArrayList} of {@link LocalDate} objects for each day in the week (Sunday to Saturday).
     */
    public static ArrayList<LocalDate> daysInWeek(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = TimeUtil.sundayForDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);

        while (current.isBefore(endDate)) {
            days.add(current);
            current = current.plusDays(1);
        }
        return days;
    }

    /**
     * Finds the next occurrence of the given day of week (0=Sunday, ..., 6=Saturday) from the current date.
     *
     * @param day The day index (0=Sunday, ..., 6=Saturday).
     * @return The next {@link LocalDate} that falls on the specified day.
     */
    public static LocalDate nextWeekDay(int day) {
        LocalDate current = LocalDate.now();
        LocalDate nextSunday = sundayForDate(current.plusWeeks(1));

        for (LocalDate d = nextSunday; d.getDayOfWeek().getValue() % 7 <= day; d = d.plusDays(1)) {
            if (d.getDayOfWeek().getValue() % 7 == day) {
                return d;
            }
        }
        return null;
    }

    /**
     * Formats a {@link Date} to a string with the pattern "dd/MM/yyyy".
     *
     * @param date The date to format.
     * @return The formatted string as "dd/MM/yyyy".
     */
    public static String formatDate(Date date) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy");
        String formatted = formatter.format(date);
        return formatted;
    }

    /**
     * Formats a {@link Date} to a string with the pattern "HH:mm" (24-hour time).
     *
     * @param date The date to format (time part is used).
     * @return The formatted string as "HH:mm".
     */
    public static String formatTime(Date date) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("HH:mm");
        String formatted = formatter.format(date);
        return formatted;
    }
}
