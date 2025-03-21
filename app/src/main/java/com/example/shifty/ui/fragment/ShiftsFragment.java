package com.example.shifty.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shifty.R;
import com.example.shifty.ui.calendar.CalendarAdapter;
import com.example.shifty.viewmodel.fragment.ShiftsViewModel;

import java.time.LocalDate;
import java.util.ArrayList;

public class ShiftsFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private RecyclerView recyclerView;
    private TextView monthYearText;
    public static LocalDate selectedDate;
    private ShiftsViewModel shiftsViewModel;

    private Button previousWeek;
    private Button nextWeek;

    private Button addConstrains;

    public ShiftsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shiftsViewModel = new ViewModelProvider(this).get(ShiftsViewModel.class);
        selectedDate = LocalDate.now();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shifts, container, false);
        initWidget(view);
        setWeekView();
        return view;
    }

    private void initWidget(View view) {
        recyclerView = view.findViewById(R.id.calenderRecycler);
        monthYearText = view.findViewById(R.id.monthYearTV);
        previousWeek = view.findViewById(R.id.previousWeek);
        nextWeek = view.findViewById(R.id.nextWeek);
        addConstrains = view.findViewById(R.id.addConstraints);

        previousWeek.setOnClickListener(this::previousWeek);
        nextWeek.setOnClickListener(this::nextWeek);
        addConstrains.setOnClickListener(this::addConstraints);

    }

    private void setWeekView() {
        monthYearText.setText(shiftsViewModel.monthYearFromDate(selectedDate));
        ArrayList<LocalDate> daysInWeek = shiftsViewModel.daysInWeek(selectedDate);
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInWeek, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(calendarAdapter);
    }

    private void previousWeek(View view) {
        selectedDate = selectedDate.minusWeeks(1);
        setWeekView();
    }

    private void nextWeek(View view) {
        selectedDate = selectedDate.plusWeeks(1);
        setWeekView();
    }

    private void addConstraints(View view) {
        Toast.makeText(getContext(), "Add Constraints", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.equals("")) {
            selectedDate = shiftsViewModel.daysInWeek(selectedDate).get(position);
            setWeekView();

        }
    }
}