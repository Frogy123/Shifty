package com.example.shifty.model.SchedulingAlgorithm;

import java.util.Date;
import java.util.Map;

/**
 * Represents a scheduling constraint for an employee, such as unavailable days or hours.
 * Inherits from {@link TimeStamp} to include time-related properties.
 *
 * Used in the scheduling algorithm to restrict employee shift assignments.
 *
 * @author Eitan Navon
 */
public class Constraint extends TimeStamp {

    /**
     * Constructs a Constraint with the specified day, start hour, end hour, and date.
     *
     * @param day the day of the week (0 = Sunday, 6 = Saturday)
     * @param startHour the starting hour of the constraint
     * @param endHour the ending hour of the constraint
     * @param date the epoch day this constraint applies to
     */
    public Constraint(int day, int startHour, int endHour, long date){
        super(day, startHour, endHour, date);
    }

    /**
     * Default constructor for Constraint.
     */
    public Constraint(){
        super();
    }

    /**
     * Constructs a Constraint from a map of properties.
     *
     * @param map a map containing constraint properties
     */
    public Constraint(Map<String, Object> map){
        super(map);
    }
}

