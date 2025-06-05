package com.example.shifty.viewmodel.dialogFragment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel for managing the logic and UI state of a dialog fragment that adds scheduling constraints.
 * <p>
 * Provides a {@link MutableLiveData} for error messages and an entry point for
 * constraint addition logic (to be implemented).
 * </p>
 *
 * <b>Intended use:</b> Attach this ViewModel to dialog fragments like
 * {@link com.example.shifty.ui.dialogFragment.SystemNeedDialog} or others for constraint management.
 *
 * @author Eitan Navon
 * @see androidx.lifecycle.ViewModel
 */
public class ConstraintDialogViewModel extends ViewModel {

    /** LiveData for holding and observing error messages. */
    MutableLiveData<String> ErrorMsg = new MutableLiveData<>();

    /**
     * Default constructor.
     */
    public ConstraintDialogViewModel() {
        // Constructor
    }

    /**
     * Adds a constraint to the system for a given day and hour range.
     * On error, posts the error message to {@link #ErrorMsg}.
     *
     * @param day       the day of the week (0=Sunday, 6=Saturday)
     * @param startHour the starting hour of the constraint (24-hour format)
     * @param endHour   the ending hour of the constraint (24-hour format)
     * @throws IllegalArgumentException if invalid input is provided (not implemented here)
     */
    public void addConstraint(int day, int startHour, int endHour) {
        try {
            // TODO: Implement constraint addition logic here
        } catch (Exception e) {
            ErrorMsg.postValue(e.getMessage());
        }
    }
}
