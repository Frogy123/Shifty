package com.example.shifty.model.SchedulingAlgorithm;

import com.example.shifty.model.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class SchedulingAlgo {

    // Constants for maximum hours per shift, days in a week, and the end hour of the day
    private static final int MAX_HOURS = 8;
    private static final int MAX_DAYS = 7;
    private static final int END_HOUR = 22;

    // Priority queue to manage employees based on their priority
    private PriorityQueue<Employee> pq;

    // List to store employees who are not assigned yet
    private ArrayList<Employee> rest;

    // System constraints for scheduling (e.g., required workers per hour)
    private int[][] systemConstraints;

    // Schedule object to store the final schedule
    private Schedule schedule;

    // Constructor to initialize the scheduling algorithm with employees and constraints
    public SchedulingAlgo(List<Employee> employees, int[][] systemConstraints) {
        pq = new PriorityQueue<Employee>(employees); // Initialize priority queue with employees
        this.systemConstraints = systemConstraints; // Set system constraints
    }

    // Method to reinitialize the algorithm with new employees and constraints
    public void init(List<Employee> employees, int[][] systemConstraints) {
        pq = new PriorityQueue<Employee>(employees); // Reinitialize priority queue
        this.systemConstraints = systemConstraints; // Update system constraints
    }

    // Main method to run the scheduling algorithm
    public Schedule run() {
        this.schedule = new Schedule(); // Initialize a new schedule
        ArrayList<Employee> rest = new ArrayList<Employee>(); // List to store unassigned employees

        // Iterate through each day and hour
        for (int day = 0; day < MAX_DAYS; day++) {
            for (int hour = 0; hour < 22; hour++) {
                // While there are unfulfilled constraints for the current hour
                while (systemConstraints[day][hour] > 0) {
                    Employee e = pq.poll(); // Get the next employee from the priority queue
                    if (e.isAvailable(day, hour)) { // Check if the employee is available
                        addEmployee(e, day, hour); // Assign the employee to the shift
                    }
                }
            }
        }

        return this.schedule; // Return the final schedule
    }

    // Helper method to add an employee to a shift
    private void addEmployee(Employee e, int day, int startHour) {
        int endHourOfShift = computeEndHour(e, startHour, day); // Calculate the end hour of the shift
        this.schedule.addShift(e, day, startHour, endHourOfShift); // Add the shift to the schedule

        // Calculate the next available time for the employee after their rest period
        int[] constraint = TimeUtil.addHour(endHourOfShift, day, 8); // 8 hours of rest

        // If the rest period extends to the next day
        if (constraint[0] != day) {
            e.addConstraint(day, startHour, END_HOUR); // Add constraint for the current day
            e.addConstraint(constraint[0], 0, constraint[1]); // Add constraint for the next day
        } else {
            e.addConstraint(day, startHour, END_HOUR); // Add constraint for the current day
        }
    }

    // Helper method to compute the end hour of a shift
    private int computeEndHour(Employee e, int startHour, int day) {
        int index = startHour;
        // Iterate through the hours of the shift
        for (int i = startHour; i < startHour + MAX_HOURS - 1; i++) {
            if (!e.isAvailable(day, i)) { // If the employee is not available
                return i; // Return the current hour as the end hour
            }
        }
        return startHour + MAX_HOURS - 1; // Return the maximum shift length
    }
}