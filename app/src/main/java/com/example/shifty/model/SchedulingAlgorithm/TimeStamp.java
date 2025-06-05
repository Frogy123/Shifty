package com.example.shifty.model.SchedulingAlgorithm;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

/**
 * Represents a time slot within a week, including the day, start hour, end hour, and date.
 * <p>
 * Used as a base class for shift scheduling objects (e.g., {@link Shift}).
 * Provides both a direct (primitive values) and a map-based constructor for easy serialization/deserialization,
 * as well as convenience methods for time logic and conversion.
 * </p>
 *
 * <p>
 * The convention is:
 * <ul>
 *   <li>day: number from 1 (Sunday) to 7 (Saturday)</li>
 *   <li>startHour: number from 0 (4:00 AM) to 21 (1:00 AM next day)</li>
 *   <li>endHour: number from 1 (5:00 AM) to 22 (2:00 AM next day)</li>
 *   <li>date: long (milliseconds since epoch, or epoch days)</li>
 * </ul>
 * </p>
 *
 * @author Eitan Navon
 */
public class TimeStamp {

    /**
     * The day of the week (1=Sunday, ..., 7=Saturday).
     */
    int day;

    /**
     * The hour at which the time slot starts (0-21; 4 AM - 1 AM).
     */
    int startHour;

    /**
     * The hour at which the time slot ends (1-22; 5 AM - 2 AM).
     */
    int endHour;

    /**
     * The date of the time slot, represented as milliseconds since epoch (or as epoch day if used with LocalDate).
     */
    long date;

    /**
     * Constructs a new {@code TimeStamp} with the specified day, start hour, end hour, and date.
     *
     * @param day       The day of the week (1 = Sunday, ..., 7 = Saturday).
     * @param startHour The start hour (0-21; 4 AM - 1 AM).
     * @param endHour   The end hour (1-22; 5 AM - 2 AM).
     * @param date      The date as a long (milliseconds since epoch, or as epoch day for {@link #getDateObject()}).
     */
    public TimeStamp(int day, int startHour, int endHour, long date) {
        this.day = day;
        this.startHour = startHour;
        this.endHour = endHour;
        this.date = date;
    }

    /**
     * Constructs a {@code TimeStamp} from a map, typically for use with deserialization (e.g. from Firebase).
     *
     * @param map A map with keys "day", "startHour", "endHour". Values must be castable to int.
     * @throws ClassCastException   If map values are not integers.
     * @throws NullPointerException If any key is missing.
     */
    public TimeStamp(Map<String, Object> map) {
        try {
            this.day = (int) map.get("day");
            this.startHour = (int) map.get("startHour");
            this.endHour = (int) map.get("endHour");
        } catch (Exception e) {
            e.printStackTrace();
            //TODO: handle error appropriately
        }
    }

    /**
     * No-argument constructor required for serialization frameworks (e.g. Firebase).
     */
    public TimeStamp() {
        // No-argument constructor required for Firebase
    }

    /**
     * Returns the day of the week (1 = Sunday, ..., 7 = Saturday).
     *
     * @return the day of the week.
     */
    public int getDay() {
        return day;
    }

    /**
     * Returns the start hour of the time slot.
     *
     * @return the start hour (0-21).
     */
    public int getStartHour() {
        return startHour;
    }

    /**
     * Returns the end hour of the time slot.
     *
     * @return the end hour (1-22).
     */
    public int getEndHour() {
        return endHour;
    }

    /**
     * Sets the day of the week.
     *
     * @param day the day to set (1=Sunday, ..., 7=Saturday)
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * Sets the start hour.
     *
     * @param startHour the hour the time slot starts (0-21)
     */
    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    /**
     * Sets the end hour.
     *
     * @param endHour the hour the time slot ends (1-22)
     */
    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    /**
     * Returns the date of the time slot.
     *
     * @return the date as a long (milliseconds since epoch, or as epoch day for use with {@link #getDateObject()}).
     */
    public long getDate() {
        return date;
    }

    /**
     * Sets the date for the time slot.
     *
     * @param date the date to set (milliseconds since epoch, or epoch day)
     */
    public void setDate(long date) {
        this.date = date;
    }

    /**
     * Returns the date as a {@link LocalDate} object.
     *
     * @return a {@code LocalDate} representing the time slot's date.
     */
    public LocalDate getDateObject() {
        return LocalDate.ofEpochDay(date);
    }

    /**
     * Determines whether this {@code TimeStamp} is consecutive to the given {@code TimeStamp}
     * (i.e., both are on the same day, and this ends exactly when the other starts).
     *
     * @param t the {@code TimeStamp} to compare to.
     * @return true if both are on the same day and {@code this.endHour == t.startHour}; false otherwise.
     */
    public boolean isConsectuive(TimeStamp t) {
        return this.getDay() == t.getDay() && this.getEndHour() == t.getStartHour();
    }

    /**
     * Serializes the object to a map (for saving to a database or sending to a remote API).
     *
     * @return a map with the fields "day", "startHour", and "endHour".
     */
    public Map<String, Object> toMap() {
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("day", day);
        result.put("startHour", startHour);
        result.put("endHour", endHour);
        return result;
    }
}
