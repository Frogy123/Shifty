package com.example.shifty.ui.fragment.AdminFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shifty.R;
import com.example.shifty.model.SchedulingAlgorithm.TimeUtil;
import com.example.shifty.ui.calendar.CalendarAdminAdapter;
import com.example.shifty.ui.calendar.OnItemListener;
import com.example.shifty.ui.constraintLists.ConstraintAdminAdapter;
import com.example.shifty.ui.dialogFragment.SystemNeedDialog;
import com.example.shifty.viewmodel.fragment.Admin.ScheduleFragmentViewModel;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Fragment for admin schedule management.
 * <p>
 * Displays a week-view calendar, allows admins to navigate weeks,
 * view/add constraints, and manage system needs and shift creation.
 * </p>
 *
 * <p>Implements {@link OnItemListener} for handling calendar cell clicks and long-press events.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * // Use in navigation or fragment transaction:
 * getSupportFragmentManager()
 *     .beginTransaction()
 *     .replace(R.id.fragment_container, new ScheduleFragment())
 *     .commit();
 * </pre>
 *
 * @author Eitan Navon
 * @see CalendarAdminAdapter
 * @see ConstraintAdminAdapter
 * @see ScheduleFragmentViewModel
 * @see OnItemListener
 * @see SystemNeedDialog
 */
public class ScheduleFragment extends Fragment implements OnItemListener {

    /** The view model backing this fragment. */
    ScheduleFragmentViewModel scheduleFragmentViewModel;

    /** RecyclerView for the calendar week display. */
    private RecyclerView recyclerViewCalendar;
    /** RecyclerView for the admin constraint list. */
    private RecyclerView recyclerViewConstraints;

    /** Displays the current month and year. */
    private TextView monthYearText;
    /** Static reference to the currently selected date. */
    public static LocalDate selectedDate;

    /** Displays error and status messages. */
    private TextView errorTextView;

    /** Button to navigate to the previous week. */
    private Button previousWeek;
    /** Button to navigate to the next week. */
    private Button nextWeek;
    /** Button to create a new schedule. */
    private Button createSchedule;

    /** LiveData for error messages. */
    public MutableLiveData<String> errorMsg;

    /**
     * Default constructor. Initializes the view model and error message LiveData.
     */
    public ScheduleFragment() {
        scheduleFragmentViewModel = new ScheduleFragmentViewModel();
        errorMsg = scheduleFragmentViewModel.getErrorMsg();
    }

    /**
     * Called when the fragment is created.
     * Initializes the selected date and observes LiveData for error and loading messages.
     *
     * @param savedInstanceState previously saved instance state or {@code null}
     * @throws NullPointerException if context is {@code null}
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedDate = LocalDate.now();
        scheduleFragmentViewModel = new ViewModelProvider(this).get(ScheduleFragmentViewModel.class);

        errorMsg.observe(this, errorMessage -> {
            if (errorMessage != null) {
                errorTextView.setText(errorMessage);
            }
        });

        scheduleFragmentViewModel.getIsLoading().observe(this, value -> {
            if (value) {
                errorTextView.setText("Loading...");
            } else {
                errorTextView.setText("");
            }
        });
    }

    /**
     * Inflates the fragment layout and initializes its widgets.
     *
     * @param inflater the LayoutInflater used to inflate views
     * @param container the parent view group
     * @param savedInstanceState saved instance state
     * @return the root view of the fragment
     * @throws NullPointerException if inflater is {@code null}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_schedule, container, false);
        initWidget(view);
        setWeekView();
        setConstraintView();
        addSystemNeedResultListener();
        scheduleFragmentViewModel.loadSystemNeeds();
        return view;
    }

    /**
     * Initializes widgets and sets up click listeners for navigation and schedule creation.
     *
     * @param view the fragment's root view
     */
    private void initWidget(View view) {
        recyclerViewCalendar = view.findViewById(R.id.calenderRecycler);
        recyclerViewConstraints = view.findViewById(R.id.constraintRecycler);
        monthYearText = view.findViewById(R.id.monthYearTV);
        previousWeek = view.findViewById(R.id.previousWeek);
        nextWeek = view.findViewById(R.id.nextWeek);
        createSchedule = view.findViewById(R.id.createSchedule);
        errorTextView = view.findViewById(R.id.Error);

        previousWeek.setOnClickListener(this::previousWeek);
        nextWeek.setOnClickListener(this::nextWeek);
        createSchedule.setOnClickListener(this::createScheduleOnClick);
    }

    /**
     * Handler for the create schedule button click.
     *
     * @param view the clicked button view
     */
    private void createScheduleOnClick(View view) {
        scheduleFragmentViewModel.createSchedule();
    }

    /**
     * Sets up the calendar week view, including updating the displayed month and year,
     * and populating the RecyclerView with calendar cells.
     */
    private void setWeekView() {
        monthYearText.setText(TimeUtil.monthYearFromDate(selectedDate));
        ArrayList<LocalDate> daysInWeek = TimeUtil.daysInWeek(selectedDate);
        CalendarAdminAdapter calendarAdapter = new CalendarAdminAdapter(daysInWeek, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        recyclerViewCalendar.setLayoutManager(layoutManager);
        recyclerViewCalendar.setAdapter(calendarAdapter);
    }

    /**
     * Handler for previous week navigation button click.
     *
     * @param view the clicked button view
     */
    private void previousWeek(View view) {
        selectedDate = selectedDate.minusWeeks(1);
        setWeekView();
        setConstraintView();
    }

    /**
     * Handler for next week navigation button click.
     *
     * @param view the clicked button view
     */
    private void nextWeek(View view) {
        selectedDate = selectedDate.plusWeeks(1);
        setWeekView();
        setConstraintView();
    }

    /**
     * Called when a calendar day cell is clicked.
     * Updates the selected date and refreshes the week and constraint views.
     *
     * @param position the position of the clicked item
     * @param dayText the string representation of the day
     */
    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.equals("")) {
            selectedDate = TimeUtil.daysInWeek(selectedDate).get(position);
            setWeekView();
            setConstraintView();
        }
    }

    /**
     * Called when a calendar day cell is long-clicked.
     * Opens the system needs dialog for the selected day.
     *
     * @param position the position of the long-pressed item
     * @param dayText the string representation of the day
     */
    @Override
    public void onItemLongClick(int position, String dayText) {
        if(!dayText.equals("")) {
            setWeekView();
            showSystemNeedsDialog(position);
        }
    }

    /**
     * Sets up the constraints RecyclerView with the admin constraint adapter.
     */
    private void setConstraintView() {
        ConstraintAdminAdapter constraintAdapter = new ConstraintAdminAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewConstraints.setLayoutManager(layoutManager);
        recyclerViewConstraints.setAdapter(constraintAdapter);
    }

    /**
     * Registers a FragmentResultListener to receive results from the {@link SystemNeedDialog}.
     * Updates the system needs in the view model upon result.
     */
    public void addSystemNeedResultListener(){
        getParentFragmentManager().setFragmentResultListener("SystemNeedRequest", this, (requestKey, result) -> {
            int day = result.getInt("day");
            int empPerHour = result.getInt("empPerHour");
            scheduleFragmentViewModel.validateAndUpdateSystemNeed(day, empPerHour);
        });
    }

    /**
     * Opens the {@link SystemNeedDialog} for the selected day.
     *
     * @param position the day index for which to show the dialog
     */
    private void showSystemNeedsDialog(int position) {
        SystemNeedDialog dialog = new SystemNeedDialog(position);
        dialog.show(getParentFragmentManager(), "ConstraintDialog");
    }

    // Additional input checking and getters/setters can be added here as needed.
}
