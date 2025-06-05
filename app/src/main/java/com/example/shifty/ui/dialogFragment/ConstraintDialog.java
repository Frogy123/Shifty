package com.example.shifty.ui.dialogFragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.shifty.R;
import com.example.shifty.viewmodel.LoginViewModel;
import com.example.shifty.viewmodel.dialogFragment.ConstraintDialogViewModel;

/**
 * Dialog fragment for adding a scheduling constraint. Allows users to select a day and specify a start and end hour.
 * Communicates results to the parent fragment using {@link androidx.fragment.app.FragmentResultListener}.
 *
 * <p>
 * The dialog uses a {@link ConstraintDialogViewModel} for data handling and passes the constraint back via {@link #onAddConstraintClick(View)}.
 * </p>
 *
 * <b>Related:</b>
 * <ul>
 *     <li>{@link ConstraintDialogViewModel}</li>
 *     <li>{@link androidx.fragment.app.FragmentResultListener}</li>
 *     <li>{@link Communicator}</li>
 * </ul>
 *
 * @author Eitan Navon
 */
public class ConstraintDialog extends DialogFragment implements View.OnClickListener {

    /**
     * ViewModel used to handle constraint logic and data.
     */
    ConstraintDialogViewModel constraintDialogViewModel;

    /**
     * Button for adding a constraint.
     */
    Button add;

    /**
     * Button for canceling the dialog.
     */
    Button cancel;

    /**
     * Spinner for selecting the day of the week.
     */
    Spinner daySpinner;

    /**
     * Input for the starting hour.
     */
    EditText startHourEditText;

    /**
     * Input for the ending hour.
     */
    EditText endHourEditText;

    /**
     * Communicator interface for callbacks (not always used; see {@link Communicator}).
     */
    Communicator comm;

    /**
     * Inflates the dialog view, initializes the ViewModel, and sets up UI widgets.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return The View for the fragment's UI
     * @see #initWidget(View)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_constraint_dialog, container, false);

        //init view model
        constraintDialogViewModel = new ViewModelProvider(requireActivity()).get(ConstraintDialogViewModel.class);
        initWidget(view);

        setCancelable(false);
        return  view;
    }

    /**
     * Initializes and configures all widgets in the dialog view.
     *
     * @param view The root view of the dialog layout
     */
    private void initWidget(View view){
        //buttons
        add = view.findViewById(R.id.add);
        cancel = view.findViewById(R.id.cancel);

        add.setOnClickListener(this);
        cancel.setOnClickListener(this);

        //spinner (day select)
        daySpinner = view.findViewById(R.id.daySpinner);

        // Set up the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.days_of_week,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);

        //edit text
        startHourEditText = view.findViewById(R.id.fromInput);
        endHourEditText = view.findViewById(R.id.toInput);
    }

    /**
     * Called when the cancel button is clicked. Dismisses the dialog.
     *
     * @param view The cancel button that was clicked
     * @see #onClick(View)
     */
    public void onCancelClick(View view){
        dismiss();
    }

    /**
     * Called when the add button is clicked. Retrieves the selected day and input hours, packs them into a {@link Bundle},
     * and communicates them to the parent fragment via a fragment result. Dismisses the dialog.
     *
     * @param view The add button that was clicked
     * @throws NumberFormatException if the start or end hour fields are not valid integers
     * @see #onClick(View)
     */
    public void onAddConstraintClick(View view){
        int selectedDay = daySpinner.getSelectedItemPosition();
        int startHour = Integer.parseInt(startHourEditText.getText().toString());
        int endHour = Integer.parseInt(endHourEditText.getText().toString());

        Bundle result = new Bundle();
        result.putInt("day", selectedDay);
        result.putInt("startHour", startHour);
        result.putInt("endHour", endHour);

        getParentFragmentManager().setFragmentResult("constraintRequest", result);
        dismiss();
    }

    /**
     * Handles click events for both the add and cancel buttons.
     *
     * @param view The button that was clicked
     */
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.add){
            onAddConstraintClick(view);
        }else if ((view.getId() == R.id.cancel)) {
            onCancelClick(view);
        }
    }

    /**
     * Interface for communicating constraint data from the dialog to the parent.
     * Not used in all implementations (alternative: {@link androidx.fragment.app.FragmentResultListener}).
     */
    public interface Communicator{
        /**
         * Callback for when a constraint is added.
         * @param day The selected day (0=Sunday, 1=Monday, etc.)
         * @param startHour The start hour of the constraint
         * @param endHour The end hour of the constraint
         */
        void onConstraintAdded(int day, int startHour, int endHour);
    }
}
