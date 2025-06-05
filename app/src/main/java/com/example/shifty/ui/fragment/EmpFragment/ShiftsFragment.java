package com.example.shifty.ui.fragment.EmpFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shifty.R;
import com.example.shifty.model.SchedulingAlgorithm.TimeUtil;
import com.example.shifty.ui.calendar.CalendarAdapter;
import com.example.shifty.ui.calendar.OnItemListener;
import com.example.shifty.ui.constraintLists.ConstraintAdapter;
import com.example.shifty.ui.dialogFragment.ConstraintDialog;
import com.example.shifty.viewmodel.fragment.Employee.ShiftsViewModel;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Fragment for employees to view and manage their shifts and constraints.
 * Displays a weekly calendar and a list of shift constraints, and allows
 * the user to add new constraints using a dialog.
 *
 * <p>
 * Handles week navigation, error messages, and integrates with the
 * {@link ShiftsViewModel}. Communicates with the {@link ConstraintDialog}
 * via the {@link ConstraintDialog.Communicator} interface and the fragment
 * result API.
 * </p>
 *
 * <b>Related:</b>
 * <ul>
 *     <li>{@link CalendarAdapter}</li>
 *     <li>{@link ConstraintAdapter}</li>
 *     <li>{@link ConstraintDialog}</li>
 *     <li>{@link ShiftsViewModel}</li>
 *     <li>{@link TimeUtil}</li>
 * </ul>
 *
 * @author Eitan Navon
 */
public class ShiftsFragment extends Fragment implements OnItemListener, ConstraintDialog.Communicator {

    /** RecyclerView for the calendar display. */
    private RecyclerView recyclerViewCalendar;
    /** RecyclerView for the constraints display. */
    private RecyclerView recyclerViewConstraints;
    /** TextView for displaying the month and year. */
    private TextView monthYearText;
    /** The currently selected date in the calendar. */
    public static LocalDate selectedDate;
    /** ViewModel for shift and constraint logic. */
    private ShiftsViewModel shiftsViewModel;
    /** TextView for showing error messages. */
    private TextView errorTextView;
    /** Button to move to the previous week. */
    private Button previousWeek;
    /** Button to move to the next week. */
    private Button nextWeek;
    /** Button to add new constraints. */
    private Button addConstrains;
    /** LiveData for error messages. */
    public MutableLiveData<String> errorMsg;

    /**
     * Default constructor for {@code ShiftsFragment}.
     * Initializes the ViewModel and error message LiveData.
     */
    public ShiftsFragment() {
        shiftsViewModel = new ShiftsViewModel();
        errorMsg = shiftsViewModel.getErrorMsg();
    }

    /**
     * Called when the fragment is created.
     * Initializes the ViewModel, sets the selected date, sets up the
     * result listener for the add constraint dialog, and observes error messages.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     * @see #addConstraintResultListener()
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shiftsViewModel = new ViewModelProvider(this).get(ShiftsViewModel.class);
        selectedDate = LocalDate.now();

        // Set up FragmentResultListener to get result from addConstraint
        addConstraintResultListener();

        // Observe error messages
        errorMsg.observe(this, errorMessage -> {
            if (errorMessage != null) {
                errorTextView.setText(errorMessage);
            }
        });
    }

    /**
     * Sets up a {@link androidx.fragment.app.FragmentResultListener} to receive constraint input
     * from the {@link ConstraintDialog} and handle it by updating the view model.
     */
    public void addConstraintResultListener() {
        getParentFragmentManager().setFragmentResultListener("constraintRequest", this, (requestKey, result) -> {
            int day = result.getInt("day");
            int startHour = result.getInt("startHour");
            int endHour = result.getInt("endHour");

            try {
                checkInput(startHour, endHour);
                shiftsViewModel.checkEmployeeConstraints(day, startHour, endHour);
                shiftsViewModel.onConstraintAdded(day, startHour, endHour);
            } catch (Exception e) {
                errorMsg.postValue(e.getMessage());
            }
        });
    }

    /**
     * Inflates the fragment's view, initializes UI widgets, and sets up
     * the week and constraint views.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The root view of the fragment.
     * @see #initWidget(View)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shifts, container, false);
        initWidget(view);
        setWeekView();
        setConstraintView();
        return view;
    }

    /**
     * Initializes all UI widgets in the fragment's view and sets up click listeners.
     *
     * @param view The root view for the fragment's layout.
     */
    private void initWidget(View view) {
        recyclerViewCalendar = view.findViewById(R.id.calenderRecycler);
        recyclerViewConstraints = view.findViewById(R.id.constraintRecycler);
        monthYearText = view.findViewById(R.id.monthYearTV);
        previousWeek = view.findViewById(R.id.previousWeek);
        nextWeek = view.findViewById(R.id.nextWeek);
        addConstrains = view.findViewById(R.id.addConstraints);
        errorTextView = view.findViewById(R.id.constraintError);

        previousWeek.setOnClickListener(this::previousWeek);
        nextWeek.setOnClickListener(this::nextWeek);
        addConstrains.setOnClickListener(this::addConstraintsOnClick);
    }

    /**
     * Updates the calendar RecyclerView for the current week and updates the month/year label.
     */
    private void setWeekView() {
        monthYearText.setText(TimeUtil.monthYearFromDate(selectedDate));
        ArrayList<LocalDate> daysInWeek = TimeUtil.daysInWeek(selectedDate);
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInWeek, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        recyclerViewCalendar.setLayoutManager(layoutManager);
        recyclerViewCalendar.setAdapter(calendarAdapter);
    }

    /**
     * Sets up the constraints RecyclerView with a {@link ConstraintAdapter}.
     */
    private void setConstraintView() {
        ConstraintAdapter constraintAdapter = new ConstraintAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewConstraints.setLayoutManager(layoutManager);
        recyclerViewConstraints.setAdapter(constraintAdapter);
    }

    /**
     * Navigates to the previous week and updates the calendar view.
     *
     * @param view The button that was clicked.
     */
    private void previousWeek(View view) {
        selectedDate = selectedDate.minusWeeks(1);
        setWeekView();
    }

    /**
     * Navigates to the next week and updates the calendar view.
     *
     * @param view The button that was clicked.
     */
    private void nextWeek(View view) {
        selectedDate = selectedDate.plusWeeks(1);
        setWeekView();
    }

    /**
     * Opens the {@link ConstraintDialog} for adding a new constraint.
     *
     * @param view The button that was clicked.
     */
    public void addConstraintsOnClick(View view) {
        ConstraintDialog dialog = new ConstraintDialog();
        dialog.show(getParentFragmentManager(), "ConstraintDialog");
    }

    /**
     * Handles clicks on calendar items. Updates the selected date and refreshes the week view.
     *
     * @param position The position of the clicked item.
     * @param dayText The text representing the clicked day.
     */
    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.equals("")) {
            selectedDate = TimeUtil.daysInWeek(selectedDate).get(position);
            setWeekView();
        }
    }

    /**
     * Handles the addition of a new constraint via the {@link ConstraintDialog.Communicator} interface.
     * Validates input, checks for existing constraints, and updates the ViewModel.
     *
     * @param day The selected day (0=Sunday, 1=Monday, etc.)
     * @param startHour The start hour of the constraint
     * @param endHour The end hour of the constraint
     */
    @Override
    public void onConstraintAdded(int day, int startHour, int endHour) {
        try {
            checkInput(startHour, endHour);
            shiftsViewModel.checkEmployeeConstraints(day, startHour, endHour);
            shiftsViewModel.onConstraintAdded(day, startHour, endHour);
        } catch (Exception e) {
            errorMsg.postValue(e.getMessage());
        }
    }

    /**
     * Validates the start and end hours for a constraint.
     *
     * @param startHour The start hour to check
     * @param endHour The end hour to check
     * @throws Exception if the hours are invalid or not in a valid range
     */
    private void checkInput(int startHour, int endHour) throws Exception {
        if (startHour >= endHour) throw new Exception("Start hour must be less than end hour");
        if (startHour < 0 || startHour > 23) throw new Exception("Start hour must be between 0 and 23");
        if (endHour < 0 || endHour > 23) throw new Exception("End hour must be between 0 and 23");
    }

    /**
     * Gets the error message LiveData.
     *
     * @return The MutableLiveData used for error messages.
     */
    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
    }

    /**
     * Not used. Called when a calendar item is long-clicked.
     *
     * @param position The position of the item
     * @param dayText The text of the day
     */
    @Override
    public void onItemLongClick(int position, String dayText) {
        // Not implemented
    }
}
