package com.example.shifty.viewmodel.fragment.Admin;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shifty.model.EmployeeManager;
import com.example.shifty.model.ScheduleRepository;
import com.example.shifty.model.SchedulingAlgorithm.ModelSolver;
import com.example.shifty.model.SchedulingAlgorithm.Schedule;

import java.util.concurrent.CompletableFuture;

/**
 * ViewModel for managing scheduling logic and UI state for the admin's schedule fragment.
 * <p>
 * Handles system needs input, validation, schedule creation, and progress/error reporting
 * for the admin interface.
 * </p>
 *
 * @author Eitan Navon
 * @see ScheduleRepository
 * @see ModelSolver
 * @see Schedule
 */
public class ScheduleFragmentViewModel extends ViewModel {

    /** Repository for managing schedule and system needs persistence. */
    ScheduleRepository sr = ScheduleRepository.getInstance();

    /** LiveData holding the latest error or status message. */
    MutableLiveData<String> errorMsg = new MutableLiveData<>();

    /** 2D array representing system needs: [day][hour] = employees needed. */
    int[][] systemNeeds;

    /** LiveData indicating whether a long-running task is in progress. */
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    /**
     * Default constructor.
     */
    public ScheduleFragmentViewModel() {
    }

    /**
     * Returns the error message LiveData, to observe for status and error updates.
     *
     * @return LiveData containing error or status messages
     */
    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
    }

    /**
     * Validates and updates the system need for a specific day.
     * On invalid input, posts an error message to {@link #errorMsg}.
     * On valid input, updates the {@link #systemNeeds} array and persists it via {@link ScheduleRepository}.
     *
     * @param day        the day of the week (0=Sunday, 6=Saturday)
     * @param empPerHour the number of employees needed per hour (must be 1–12)
     */
    public void validateAndUpdateSystemNeed(int day, int empPerHour)  {
        boolean isValid = true;
        try {
            inputCheck(day, empPerHour);
        } catch (Exception e) {
            errorMsg.postValue(e.getMessage());
            isValid = false;
        }
        if (isValid) {
            updateSystemNeeds(day, empPerHour);
        }
    }

    /**
     * Updates the {@link #systemNeeds} array for the given day, setting all 22 hours to the specified value,
     * and persists the result using {@link ScheduleRepository#saveSystemNeedsOnDayAndHour(int[][])}.
     * Does nothing if a loading operation is in progress.
     *
     * @param day        the day of the week (0=Sunday, 6=Saturday)
     * @param empPerHour the number of employees needed per hour
     * @see #isLoading
     */
    private void updateSystemNeeds(int day, int empPerHour) {
        if (isLoading.getValue() == null || !isLoading.getValue()) {
            for (int hour = 0; hour < 22; hour++) {
                systemNeeds[day][hour] = empPerHour;
            }
            sr.saveSystemNeedsOnDayAndHour(systemNeeds);
        }
    }

    /**
     * Checks the validity of input for day and employees per hour.
     *
     * @param day        the day of the week (0–6)
     * @param empPerHour the number of employees per hour (1–12)
     * @throws Exception if the input values are out of allowed range
     */
    private void inputCheck(int day, int empPerHour) throws Exception {
        if (day < 0 || day > 6) {
            throw new Exception("Day must be between 0 and 6");
        }
        if (empPerHour < 1 || empPerHour > 12) {
            throw new Exception("Number of employees per hour must be between 1 and 12");
        }
    }

    /**
     * Loads the current system needs from the repository asynchronously.
     * Updates the {@link #systemNeeds} field and sets {@link #isLoading} appropriately.
     */
    public void loadSystemNeeds() {
        CompletableFuture<int[][]> data = sr.fetchSystemNeeds();
        isLoading.postValue(true);
        data.thenAccept(result -> {
            this.systemNeeds = result;
            isLoading.postValue(false);
        });
    }

    /**
     * Returns the LiveData tracking whether loading is in progress.
     *
     * @return LiveData indicating if an operation is currently loading
     */
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Creates a new schedule using the current employee list and system needs.
     * The process is run asynchronously and the resulting schedule is saved via {@link ScheduleRepository#saveSchedule(Schedule)}.
     * Posts status or error messages to {@link #errorMsg}, and updates {@link #isLoading}.
     */
    public void createSchedule() {
        isLoading.postValue(true); // Indicate loading
        CompletableFuture.runAsync(() -> {
            ModelSolver model = new ModelSolver(EmployeeManager.getInstance().getEmployees(), systemNeeds);
            try {
                Schedule schedule = model.Solve();
                sr.saveSchedule(schedule); // Save the schedule
                errorMsg.postValue("Schedule created successfully!");
            } catch (RuntimeException e) {
                errorMsg.postValue("Error: " + e.getMessage());
            } finally {
                isLoading.postValue(false); // Indicate loading complete
            }
        });
    }
}
