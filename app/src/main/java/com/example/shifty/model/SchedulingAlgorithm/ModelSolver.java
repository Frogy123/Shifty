package com.example.shifty.model.SchedulingAlgorithm;

import android.util.Log;
import com.example.shifty.model.Employee;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.search.loop.monitors.ISearchMonitor;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.selectors.variables.InputOrder;
import org.chocosolver.solver.variables.BoolVar;
import java.util.List;

public class ModelSolver {

    private static final String TAG = "ModelSolver";

    Model model;

    // Constants
    final int MAX_DAYS = 7;
    final int MAX_HOURS = 22;
    final int SHIFT_LENGTH = 8; // hours

    int EmployeeCount;
    Employee[] employeeMap; // Mapping index to employee object
    boolean[][][] availability;
    int[][] systemNeeds; // Number of employees needed for each hour
    BoolVar[][][] schedule;

    public ModelSolver(List<Employee> _employees, int[][] _systemNeeds) {
        Log.d(TAG, "Initializing ModelSolver...");
        model = new Model("Scheduling Algorithm");

        // Initialize the model
        EmployeeCount = _employees.size();
        employeeMap = new Employee[EmployeeCount];
        for (int i = 0; i < _employees.size(); i++) {
            employeeMap[i] = _employees.get(i);
        }
        Log.d(TAG, "Employee count: " + EmployeeCount);

        availability = new boolean[EmployeeCount][MAX_DAYS][MAX_HOURS];
        systemNeeds = _systemNeeds;
        schedule = new BoolVar[EmployeeCount][MAX_DAYS][MAX_HOURS];

        initializeAvailability();
        initializeSchedule();
        initializeConstraints();
        Log.d(TAG, "ModelSolver initialized successfully.");
    }

    private void initializeConstraints() {
        Log.d(TAG, "Initializing constraints...");
        initializeConstraintAvailability();
        initializeConstraintsSystemNeeds();
        initializeConstraintWorkHours();
        Log.d(TAG, "Constraints initialized.");
    }

    private void initializeConstraintAvailability() {
        Log.d(TAG, "Applying availability constraints...");
        for (int i = 0; i < EmployeeCount; i++) {
            for (int j = 0; j < MAX_DAYS; j++) {
                for (int k = 0; k < MAX_HOURS; k++) {
                    if (!availability[i][j][k]) {
                        model.arithm(schedule[i][j][k], "=", 0).post();
                    }
                }
            }
        }
        Log.d(TAG, "Availability constraints applied.");
    }

    private void initializeConstraintsSystemNeeds() {
        Log.d(TAG, "Applying system needs constraints...");
        for (int j = 0; j < MAX_DAYS; j++) {
            for (int k = 0; k < MAX_HOURS; k++) {
                BoolVar[] employeesScheduled = new BoolVar[EmployeeCount];
                for (int i = 0; i < EmployeeCount; i++) {
                    employeesScheduled[i] = schedule[i][j][k];
                }
                model.sum(employeesScheduled, "=", systemNeeds[j][k]).post();
            }
        }
        Log.d(TAG, "System needs constraints applied.");
    }

    private void initializeConstraintWorkHours() {
        Log.d(TAG, "Applying work hours constraints...");
        for (int i = 0; i < EmployeeCount; i++) {
            for (int j = 0; j < MAX_DAYS; j++) {
                BoolVar[] dailyHours = new BoolVar[MAX_HOURS];
                for (int k = 0; k < MAX_HOURS; k++) {
                    if (k >= 8) {
                        for (int h = k - 8; h < k; h++) {
                            model.ifThen(
                                    model.arithm(schedule[i][j][h], "=", 1),
                                    model.arithm(schedule[i][j][k], "=", 0)
                            );
                        }
                    }
                    dailyHours[k] = schedule[i][j][k];

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
                model.sum(dailyHours, "<=", SHIFT_LENGTH).post();
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

    private void initializeSchedule() {
        Log.d(TAG, "Initializing schedule variables...");
        for (int i = 0; i < EmployeeCount; i++) {
            for (int j = 0; j < MAX_DAYS; j++) {
                for (int k = 0; k < MAX_HOURS; k++) {
                    schedule[i][j][k] = model.boolVar("schedule_" + i + "_" + j + "_" + k);
                }
            }
        }
        Log.d(TAG, "Schedule variables initialized.");
    }

    private void initializeAvailability() {
        Log.d(TAG, "Initializing availability...");
        for (int i = 0; i < EmployeeCount; i++) {
            for (int j = 0; j < MAX_DAYS; j++) {
                for (int k = 0; k < MAX_HOURS; k++) {
                    availability[i][j][k] = employeeMap[i].isAvailable(j, k);
                }
            }
        }
        Log.d(TAG, "Availability initialized.");
    }

    public Schedule Solve() throws RuntimeException {
        Log.d(TAG, "Starting to solve the model...");
        setSearchStrategy();



        if (this.model.getSolver().solve()) {
            Log.d(TAG, "Solution found.");
            Schedule s = new Schedule();
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


    public void debug() {
        Log.d(TAG, "Debugging solver statistics...");
        model.getSolver().showShortStatistics();
        model.getSolver().showStatistics();
        model.getSolver().showSolutions();
    }

    public void setSearchStrategy() {
        Log.d(TAG, "Setting search strategy...");
        model.getSolver().setSearch(
                Search.intVarSearch(
                        new InputOrder<>(model), // Variable selection: FirstFail
                        new IntDomainMax(),   // Value selection: Minimum value
                        flattenSchedule()     // Flattened array of BoolVar
                )
        );
    }

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