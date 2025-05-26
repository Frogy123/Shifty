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

public class ShiftsFragment extends Fragment implements OnItemListener, ConstraintDialog.Communicator {

    private RecyclerView recyclerViewCalendar;
    private RecyclerView recyclerViewConstraints;

    private TextView monthYearText;
    public static LocalDate selectedDate;
    private ShiftsViewModel shiftsViewModel;

    private TextView errorTextView;

    private Button previousWeek;
    private Button nextWeek;

    private Button addConstrains;

    public MutableLiveData<String> errorMsg;

    public ShiftsFragment() {
        shiftsViewModel = new ShiftsViewModel();
        errorMsg = shiftsViewModel.getErrorMsg();
    }

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

    public void addConstraintResultListener(){
        // Set up FragmentResultListener to get result from addConstraint
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shifts, container, false);
        initWidget(view);
        setWeekView();
        setConstraintView();
        return view;
    }

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

    private void setWeekView() {
        monthYearText.setText(TimeUtil.monthYearFromDate(selectedDate));
        ArrayList<LocalDate> daysInWeek = TimeUtil.daysInWeek(selectedDate);
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInWeek, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        recyclerViewCalendar.setLayoutManager(layoutManager);
        recyclerViewCalendar.setAdapter(calendarAdapter);
    }

    private void setConstraintView() {
        // Initialize the ConstraintAdapter
        ConstraintAdapter constraintAdapter = new ConstraintAdapter();

        // Set up the RecyclerView with a LinearLayoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewConstraints.setLayoutManager(layoutManager);
        recyclerViewConstraints.setAdapter(constraintAdapter);
    }


    private void previousWeek(View view) {
        selectedDate = selectedDate.minusWeeks(1);
        setWeekView();
    }

    private void nextWeek(View view) {
        selectedDate = selectedDate.plusWeeks(1);
        setWeekView();
    }


    public void addConstraintsOnClick(View view) {
        ConstraintDialog dialog = new ConstraintDialog();
        dialog.show(getParentFragmentManager(), "ConstraintDialog");
    }


    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.equals("")) {
            selectedDate = TimeUtil.daysInWeek(selectedDate).get(position);
            setWeekView();

        }
    }




    @Override
    public void onConstraintAdded(int day, int startHour, int endHour) {

        try{
            checkInput(startHour, endHour);
            shiftsViewModel.checkEmployeeConstraints(day, startHour, endHour);

            shiftsViewModel.onConstraintAdded(day, startHour, endHour);


        }catch (Exception e){
            errorMsg.postValue(e.getMessage());
        }


    }

    //input checker
    private void checkInput(int startHour, int endHour) throws Exception {
        if (startHour >= endHour) throw new Exception("Start hour must be less than end hour");
        if (startHour < 0 || startHour > 23) throw new Exception("Start hour must be between 0 and 23");
        if (endHour < 0 || endHour > 23) throw new Exception("End hour must be between 0 and 23");
    }



    //getters and setters

    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
    }

    //not using
    @Override
    public void onItemLongClick(int position, String dayText) {

    }

}