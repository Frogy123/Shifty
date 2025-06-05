package com.example.shifty.model.SchedulingAlgorithm;

import android.util.Log;
import com.example.shifty.model.Employee;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.variables.InputOrder;
import org.chocosolver.solver.variables.BoolVar;
import java.util.List;

/**
 * ModelSolver is responsible for building and solving the employee scheduling problem
 * using constraint programming (Choco Solver). It encodes employee availability,
 * system needs, and work hour constraints, and produces a feasible schedule if possible.
 *
 * The class initializes the constraint model, applies all relevant constraints,
 * and attempts to solve the scheduling problem, returning a Schedule object on success.
 *
 * @author Eitan Navon
 */
public class ModelSolver {

    private static final String TAG = "ModelSolver";

    Model model;

    // Constants for scheduling
    final int MAX_DAYS = 7;
    final int MAX_HOURS = 22;
    final int SHIFT_LENGTH = 8; // Maximum shift length in hours

    int EmployeeCount;
    Employee[] employeeMap; // Maps index to Employee object
    boolean[][][] availability; // Employee availability [employee][day][hour]
    int[][] systemNeeds; // Number of employees needed for each day and hour
    BoolVar[][][] schedule; // Choco Solver variables for scheduling

    /**
     * Constructs a ModelSolver with the given list of employees and system needs.
     *
     * @param _employees the list of employees to schedule
     * @param _systemNeeds a 2D array representing the number of employees needed for each day and hour
     */
    public ModelSolver(List<Employee> _employees, int[][] _systemNeeds) {
        Log.d(TAG, "Initializing ModelSolver...");
        model = new Model("Scheduling Algorithm");

        // Initialize employee mapping
        EmployeeCount = _employees.size();
        employeeMap = new Employee[EmployeeCount];
        for (int i = 0; i < _employees.size(); i++) {
            employeeMap[i] = _employees.get(i);
        }
        Log.d(TAG, "Employee count: " + EmployeeCount);

        // Initialize arrays
        availability = new boolean[EmployeeCount][MAX_DAYS][MAX_HOURS];
        systemNeeds = _systemNeeds;
        schedule = new BoolVar[EmployeeCount][MAX_DAYS][MAX_HOURS];

        // Initialize model variables and constraints
        initializeAvailability();
        initializeSchedule();
        initializeConstraints();
        Log.d(TAG, "ModelSolver initialized successfully.");
    }

    /**
     * Initializes all constraints for the scheduling model.
     */
    private void initializeConstraints() {
        Log.d(TAG, "Initializing constraints...");
        initializeConstraintAvailability();
        initializeConstraintsSystemNeeds();
        initializeConstraintWorkHours();
        Log.d(TAG, "Constraints initialized.");
    }

    /**
     * Applies constraints to ensure employees are only scheduled when available.
     */
    private void initializeConstraintAvailability() {
        Log.d(TAG, "Applying availability constraints...");
        for (int i = 0; i < EmployeeCount; i++) {
            for (int j = 0; j < MAX_DAYS; j++) {
                for (int k = 0; k < MAX_HOURS; k++) {
                    if (!availability[i][j][k]) {
                        // If not available, cannot be scheduled
                        model.arithm(schedule[i][j][k], "=", 0).post();
                    }
                }
            }
        }
        Log.d(TAG, "Availability constraints applied.");
    }

    /**
     * Applies constraints to ensure system needs (number of employees per hour) are met.
     */
    private void initializeConstraintsSystemNeeds() {
        Log.d(TAG, "Applying system needs constraints...");
        for (int j = 0; j < MAX_DAYS; j++) {
            for (int k = 0; k < MAX_HOURS; k++) {
                BoolVar[] employeesScheduled = new BoolVar[EmployeeCount];
                for (int i = 0; i < EmployeeCount; i++) {
                    employeesScheduled[i] = schedule[i][j][k];
                }
                // The sum of scheduled employees must match the system need
                model.sum(employeesScheduled, "=", systemNeeds[j][k]).post();
            }
        }
        Log.d(TAG, "System needs constraints applied.");
    }

    /**
     * Applies constraints to limit work hours and ensure valid shift patterns.
     */
    private void initializeConstraintWorkHours() {
        Log.d(TAG, "Applying work hours constraints...");
        for (int i = 0; i < EmployeeCount; i++) {
            for (int j = 0; j < MAX_DAYS; j++) {
                BoolVar[] dailyHours = new BoolVar[MAX_HOURS];
                for (int k = 0; k < MAX_HOURS; k++) {
                    // Prevent more than 8 consecutive hours
                    if (k >= 8) {
                        for (int h = k - 8; h < k; h++) {
                            model.ifThen(
                                    model.arithm(schedule[i][j][h], "=", 1),
                                    model.arithm(schedule[i][j][k], "=", 0)
                            );
                        }
                    }
                    dailyHours[k] = schedule[i][j][k];

                    // Enforce that if two hours are scheduled with a gap, the hour in between must also be scheduled
                    if (k < MAX_HOURS - 2) {
                        model.ifThen(
                                model.and(
                                        model.arithm(schedule[i][j][k], "=", 1),
                                        model.arithm(schedule[i][j][k + 2], "=", 1)
                                ),
                                model.arithm(schedule[i][j][k + 1], "=", 1)
                        );
                    }
                }
                // Limit total hours per day
                model.sum(dailyHours, "<=", SHIFT_LENGTH).post();

                // Prevent overnight shifts (no consecutive shifts across days)
                int lastDay = ((j - 1) + MAX_DAYS) % MAX_DAYS;
                for (int l = 0; l < 7; l++) {
                    model.ifThen(
                            model.arithm(schedule[i][lastDay][15 + l], "=", 1),
                            model.arithm(schedule[i][j][l], "=", 0)
                    );
                }
            }
        }
        Log.d(TAG, "Work hours constraints applied.");
    }

    /**
     * Initializes the schedule variables for the model.
     */
    private void initializeSchedule() {
        Log.d(TAG, "Initializing schedule variables...");
        for (int i = 0; i < EmployeeCount; i++) {
            for (int j = 0; j < MAX_DAYS; j++) {
                for (int k = 0; k < MAX_HOURS; k++) {
                    // Each variable represents if employee i is scheduled on day j at hour k
                    schedule[i][j][k] = model.boolVar("schedule_" + i + "_" + j + "_" + k);
                }
            }
        }
        Log.d(TAG, "Schedule variables initialized.");
    }

    /**
     * Initializes the availability array for all employees.
     */
    private void initializeAvailability() {
        Log.d(TAG, "Initializing availability...");
        for (int i = 0; i < EmployeeCount; i++) {
            for (int j = 0; j < MAX_DAYS; j++) {
                for (int k = 0; k < MAX_HOURS; k++) {
                    // Query each employee's availability
                    availability[i][j][k] = employeeMap[i].isAvailable(j, k);
                }
            }
        }
        Log.d(TAG, "Availability initialized.");
    }

    /**
     * Attempts to solve the scheduling problem and returns a Schedule if successful.
     *
     * @return a Schedule object representing the solution
     * @throws RuntimeException if no solution is found
     */
    public Schedule Solve() throws RuntimeException {
        Log.d(TAG, "Starting to solve the model...");
        setSearchStrategy();

        if (this.model.getSolver().solve()) {
            Log.d(TAG, "Solution found.");
            Schedule s = new Schedule();
            // Collect the solution into a Schedule object
            for (int i = 0; i < EmployeeCount; i++) {
                for (int j = 0; j < MAX_DAYS; j++) {
                    for (int k = 0; k < MAX_HOURS; k++) {
                        if (schedule[i][j][k].getValue() == 1) {
                            s.addEmployeeHour(employeeMap[i], j, k);
                            Log.d(TAG, "Employee " + employeeMap[i].getName() + " scheduled on day " + j + " at hour " + k);
                        }
                    }
                }
            }
            debug();
            return s;
        } else {
            Log.d(TAG, "No solution found.");
            debug();
            throw new RuntimeException("No solution found");
        }
    }

    /**
     * Prints debugging information about the solver's statistics and solutions.
     */
    public void debug() {
        Log.d(TAG, "Debugging solver statistics...");
        model.getSolver().showShortStatistics();
        model.getSolver().showStatistics();
        model.getSolver().showSolutions();
    }

    /**
     * Sets the search strategy for the constraint solver.
     */
    public void setSearchStrategy() {
        Log.d(TAG, "Setting search strategy...");
        model.getSolver().setSearch(
                Search.intVarSearch(
                        new InputOrder<>(model), // Variable selection: input order
                        new IntDomainMax(),      // Value selection: maximum value
                        flattenSchedule()        // Flattened array of BoolVar
                )
        );
    }

    /**
     * Flattens the 3D schedule array into a 1D array for use in search strategies.
     *
     * @return a flattened array of BoolVar representing the schedule variables
     */
    private BoolVar[] flattenSchedule() {
        BoolVar[] flatSchedule = new BoolVar[EmployeeCount * MAX_DAYS * MAX_HOURS];
        int index = 0;
        for (int i = 0; i < EmployeeCount; i++) {
            for (int j = 0; j < MAX_DAYS; j++) {
                for (int k = 0; k < MAX_HOURS; k++) {
                    flatSchedule[index++] = schedule[i][j][k];
                }
            }
        }
        return flatSchedule;
    }
}