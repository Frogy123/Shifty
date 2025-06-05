package com.example.shifty.viewmodel.fragment.Employee;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shifty.model.CurrentUserManager;
import com.example.shifty.model.Employee;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * ViewModel for managing the employee's shifts and shift constraints in the UI layer.
 * <p>
 * Handles business logic for adding constraints, validating constraints,
 * and exposing error messages to the view via LiveData.
 * </p>
 *
 * <p>Example usage in a Fragment:</p>
 * <pre>
 * ShiftsViewModel viewModel = new ViewModelProvider(this).get(ShiftsViewModel.class);
 * viewModel.getErrorMsg().observe(getViewLifecycleOwner(), errorMsg -> {
 *     // Show error message if needed
 * });
 * </pre>
 *
 * @author Eitan Navon
 * @see Employee
 * @see CurrentUserManager
 */
public class ShiftsViewModel extends ViewModel {

    /** The current employee for which shifts and constraints are managed. */
    Employee currEmp;

    /** LiveData for communicating error messages to the UI. */
    MutableLiveData<String> errorMsg = new MutableLiveData<>();

    /**
     * Initializes the view model and sets the current employee reference.
     */
    public ShiftsViewModel() {
        currEmp = CurrentUserManager.getInstance().getCurrentEmployee();
    }

    /**
     * Called when a constraint is added by the employee.
     * Updates the employee's constraints and persists the changes.
     *
     * @param day the day of the week (0-based index)
     * @param startHour the start hour for the constraint (24-hour format)
     * @param endHour the end hour for the constraint (24-hour format)
     * @throws NullPointerException if {@code currEmp} is {@code null}
     */
    public void onConstraintAdded(int day, int startHour, int endHour) {
        if (currEmp != null) {
            currEmp.addConstraint(day, startHour, endHour);
            currEmp.save();
        }
    }

    /**
     * Checks if the employee can add another constraint.
     * Throws an exception if the maximum allowed constraints (2) is exceeded.
     *
     * @param day the day of the week (not used in logic, for interface consistency)
     * @param startHour the start hour (not used in logic)
     * @param endHour the end hour (not used in logic)
     * @throws Exception if the employee already has 2 or more constraints
     */
    public void checkEmployeeConstraints(int day, int startHour, int endHour) throws Exception {
        if (currEmp.numberOfConstraints() >= 2) {
            throw new Exception("You can only have 2 constraints");
        }
    }

    /**
     * Returns the LiveData for error messages, to be observed by the UI.
     *
     * @return a {@link MutableLiveData} containing the current error message
     */
    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
    }
}
