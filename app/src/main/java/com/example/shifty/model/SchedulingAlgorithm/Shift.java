package com.example.shifty.model.SchedulingAlgorithm;

import java.util.Date;
import java.util.Map;

/**
 * Represents a single work shift, which is a specific block of time within a day,
 * assigned to an employee as part of the scheduling algorithm.
 *
 * This class extends {@link TimeStamp}, inheriting day, start hour, end hour, and date information.
 * It is used as the primary data structure for shift allocation within the scheduling model.
 *
 *
 *
 * Shifts can be constructed directly via primitive parameters or from a {@code Map}
 * (e.g., when deserializing from a database or Firebase).
 *
 *
 * @author Eitan Navon
 */
public class Shift extends TimeStamp {

    /**
     * Constructs a new {@code Shift} with the specified day, start hour, end hour, and date.
     *
     * @param day The day of the week (e.g., 0 = Sunday, 1 = Monday, etc.).
     * @param startHour The hour the shift starts (0-23).
     * @param endHour The hour the shift ends (0-23, should be greater than {@code startHour}).
     * @param date The date of the shift, represented as milliseconds since epoch (Unix time).
     */
    public Shift(int day, int startHour, int endHour, long date) {
        super(day, startHour, endHour, date);
    }

    /**
     * Constructs a new {@code Shift} from a {@code Map} representation, typically loaded from
     * a remote database or used for deserialization.
     *
     * @param map A {@code Map} containing the shift's attributes. Must contain keys
     *            for day, startHour, endHour, and date, matching the {@link TimeStamp} fields.
     * @throws ClassCastException if the values in the map cannot be cast to the expected types.
     * @throws NullPointerException if required keys are missing from the map.
     */
    public Shift(Map<String, Object> map) {
        super(map);
    }

}
