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

public class ScheduleFragment extends Fragment implements OnItemListener {


    ScheduleFragmentViewModel scheduleFragmentViewModel;

    private RecyclerView recyclerViewCalendar;
    private RecyclerView recyclerViewConstraints;

    private TextView monthYearText;
    public static LocalDate selectedDate;

    private TextView errorTextView;

    private Button previousWeek;
    private Button nextWeek;

    private Button addConstrains;

    public MutableLiveData<String> errorMsg;

    public ScheduleFragment() {
        scheduleFragmentViewModel = new ScheduleFragmentViewModel();
        errorMsg = scheduleFragmentViewModel.getErrorMsg();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedDate = LocalDate.now();
        scheduleFragmentViewModel = new ViewModelProvider(this).get(ScheduleFragmentViewModel.class);

        errorMsg.observe( this, errorMessage -> {
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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_schedule, container, false);
        initWidget(view);
        setWeekView();
        setConstraintView();
        addSystemNeedResultListener();
        scheduleFragmentViewModel.loadSystemNeeds();
        return view;
    }

    private void initWidget(View view) {
        recyclerViewCalendar = view.findViewById(R.id.calenderRecycler);
        recyclerViewConstraints = view.findViewById(R.id.constraintRecycler);
        monthYearText = view.findViewById(R.id.monthYearTV);
        previousWeek = view.findViewById(R.id.previousWeek);
        nextWeek = view.findViewById(R.id.nextWeek);
        addConstrains = view.findViewById(R.id.addConstraints);
        errorTextView = view.findViewById(R.id.Error);


        previousWeek.setOnClickListener(this::previousWeek);
        nextWeek.setOnClickListener(this::nextWeek);
        //addConstrains.setOnClickListener(this::addConstraintsOnClick);

    }

    private void setWeekView() {
        monthYearText.setText(TimeUtil.monthYearFromDate(selectedDate));
        ArrayList<LocalDate> daysInWeek = TimeUtil.daysInWeek(selectedDate);
        CalendarAdminAdapter calendarAdapter = new CalendarAdminAdapter(daysInWeek, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        recyclerViewCalendar.setLayoutManager(layoutManager);
        recyclerViewCalendar.setAdapter(calendarAdapter);
    }


    private void previousWeek(View view) {
        selectedDate = selectedDate.minusWeeks(1);
        setWeekView();
        setConstraintView();
    }

    private void nextWeek(View view) {
        selectedDate = selectedDate.plusWeeks(1);
        setWeekView();
        setConstraintView();
    }


    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.equals("")) {
            selectedDate = TimeUtil.daysInWeek(selectedDate).get(position);
            setWeekView();
            setConstraintView();
        }
    }

    @Override
    public void onItemLongClick(int position, String dayText) {
        if(!dayText.equals("")) {
            setWeekView();
            showSystemNeedsDialog(position);
        }
    }

    private void setConstraintView() {
        // Initialize the ConstraintAdapter
        ConstraintAdminAdapter constraintAdapter = new ConstraintAdminAdapter();

        // Set up the RecyclerView with a LinearLayoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewConstraints.setLayoutManager(layoutManager);
        recyclerViewConstraints.setAdapter(constraintAdapter);
    }

    public void addSystemNeedResultListener(){
        // Set up FragmentResultListener to get result from addConstraint
        getParentFragmentManager().setFragmentResultListener("SystemNeedRequest", this, (requestKey, result) -> {
            int day = result.getInt("day");
            int empPerHour = result.getInt("empPerHour");

            scheduleFragmentViewModel.validateAndUpdateSystemNeed(day, empPerHour);


        });

    }

    private void showSystemNeedsDialog(int position) {
        SystemNeedDialog dialog = new SystemNeedDialog(position);
        dialog.show(getParentFragmentManager(), "ConstraintDialog");
    }

    //input checker



    //getters and setters



}
