package com.example.shifty.model.SchedulingAlgorithm;

import androidx.annotation.Nullable;
import com.example.shifty.model.Employee;
import com.example.shifty.model.EmployeeManager;
import java.util.ArrayList;

/**
 * Schedule manages the assignment of employees to shifts across a week.
 * It stores which employees are scheduled for each day and hour, and provides
 * methods to add shifts and convert the schedule into employee shift records.
 *
 * @author Eitan Navon
 */
public class Schedule {

    /**
     * 2D array representing the schedule for each day and hour.
     * Each cell contains a list of employees scheduled at that time.
     */
    ArrayList<Employee>[][] schedule;

    /**
     * Constructs an empty Schedule for 7 days and 22 hours per day.
     */
    public Schedule() {
        schedule = new ArrayList[7][22];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 22; j++) {
                schedule[i][j] = new ArrayList<Employee>();
            }
        }
    }

    /**
     * Adds an employee to a shift spanning from startHour to endHour on a given day.
     *
     * @param e the employee to add
     * @param day the day of the week (0 = Sunday, 6 = Saturday)
     * @param startHour the starting hour of the shift (inclusive)
     * @param endHour the ending hour of the shift (exclusive)
     */
    public void addShift(Employee e, int day, int startHour, int endHour) {
        for (int i = startHour; i < endHour; i++) {
            schedule[day][i].add(e);
        }
    }

    /**
     * Adds an employee to a specific hour on a given day.
     *
     * @param e the employee to add
     * @param day the day of the week (0 = Sunday, 6 = Saturday)
     * @param hour the hour to add the employee to
     */
    public void addEmployeeHour(Employee e, int day, int hour) {
        schedule[day][hour].add(e);
    }

    /**
     * Compares this Schedule to another object for equality.
     *
     * @param obj the object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    /**
     * Converts the schedule into shift records for each employee and saves all employees.
     * For each scheduled hour, adds a shift to the corresponding employee.
     */
    public void ScheduleToShifts() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 22; j++) {
                for (Employee e : schedule[i][j]) {
                    e.addShift(i, j, j + 1);
                }
            }
        }
        EmployeeManager.getInstance().saveAllEmployees(); // Save all employees to the database
    }
}