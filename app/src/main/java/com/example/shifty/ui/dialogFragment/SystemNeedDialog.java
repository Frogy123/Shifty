package com.example.shifty.ui.dialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.shifty.R;
import com.example.shifty.viewmodel.dialogFragment.ConstraintDialogViewModel;

/**
 * Dialog fragment for adding or editing a system need constraint, such as specifying
 * the number of employees required per hour for a selected day.
 * <p>
 * Contains input widgets for selecting the day and specifying the employee count,
 * and communicates results using {@link androidx.fragment.app.FragmentResultListener} and a
 * {@link Communicator} interface.
 * </p>
 *
 * <b>UI widgets:</b>
 * <ul>
 *     <li>{@link Spinner} for day selection</li>
 *     <li>{@link EditText} for employee per hour input</li>
 *     <li>{@link Button} for Add and Cancel actions</li>
 * </ul>
 *
 * @author Eitan Navon
 * @see ConstraintDialogViewModel
 */
public class SystemNeedDialog extends DialogFragment implements View.OnClickListener {

    /** ViewModel for managing dialog state and logic. */
    ConstraintDialogViewModel constraintDialogViewModel;

    /** Button for confirming the addition of a system need. */
    Button add;
    /** Button for cancelling the dialog. */
    Button cancel;
    /** Spinner for selecting the day of the week. */
    Spinner daySpinner;
    /** Index of the day to select by default. */
    int startingDay;
    /** EditText for inputting employees required per hour. */
    EditText EmpPerHourEditText;

    /** Optional communicator for callback to parent. */
    ConstraintDialog.Communicator comm;

    /**
     * Constructs a new dialog with the specified starting day selected.
     *
     * @param day the index of the day to select in the spinner (0=Sunday)
     */
    public SystemNeedDialog(int day) {
        startingDay = day;
    }

    /**
     * Inflates the dialog layout and initializes all UI components and ViewModel.
     *
     * @param inflater           the LayoutInflater object
     * @param container          the parent ViewGroup
     * @param savedInstanceState saved instance state Bundle
     * @return the created view for the dialog
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system_need_dialog, container, false);

        // Initialize ViewModel and widgets
        constraintDialogViewModel = new ViewModelProvider(this).get(ConstraintDialogViewModel.class);
        initWidget(view);

        setCancelable(false);

        return view;
    }

    /**
     * Initializes UI widgets and their listeners for this dialog.
     *
     * @param view the root view containing all dialog UI components
     */
    private void initWidget(View view) {
        // Buttons
        add = (Button) view.findViewById(R.id.add);
        cancel = (Button) view.findViewById(R.id.cancel);

        add.setOnClickListener(this);
        cancel.setOnClickListener(this);

        // Spinner (day select)
        daySpinner = view.findViewById(R.id.daySpinner);

        // Set up the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.days_of_week,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);

        daySpinner.setSelection(startingDay);

        // EditText
        EmpPerHourEditText = view.findViewById(R.id.EmpPerHourInput);
    }

    /**
     * Handles the Cancel button click. Dismisses the dialog.
     *
     * @param view the button view that was clicked
     */
    public void onCancelClick(View view) {
        dismiss();
    }

    /**
     * Handles the Add button click. Collects the user input and communicates the result
     * back to the parent fragment or activity via FragmentResult API.
     * <p>
     * Puts {@code day} and {@code empPerHour} in the result {@link Bundle}.
     * </p>
     *
     * @param view the button view that was clicked
     * @throws NumberFormatException if employee per hour input is not a valid integer
     */
    public void onAddSystemNeedClick(View view) {
        int selectedDay = daySpinner.getSelectedItemPosition();
        String empPerHourString = EmpPerHourEditText.getText().toString();

        Bundle result = new Bundle();
        result.putInt("day", selectedDay);
        result.putInt("empPerHour", Integer.parseInt(empPerHourString));

        getParentFragmentManager().setFragmentResult("SystemNeedRequest", result);
        dismiss();
    }

    /**
     * Handles click events for both Add and Cancel buttons.
     *
     * @param view the clicked button
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add) {
            onAddSystemNeedClick(view);
        } else if (view.getId() == R.id.cancel) {
            onCancelClick(view);
        }
    }

    /**
     * Communicator interface for callback when a system need is added.
     * Implement this in the parent fragment or activity to handle the dialog result directly.
     */
    public interface Communicator {
        /**
         * Called when a system need has been added by the user.
         *
         * @param day       the day index selected (0=Sunday)
         * @param startHour the starting hour for the system need
         * @param endHour   the ending hour for the system need
         */
        void onSystemNeedAdded(int day, int startHour, int endHour);
    }
}
