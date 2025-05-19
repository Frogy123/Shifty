package com.example.shifty.ui.calendar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shifty.R;
import com.example.shifty.model.CurrentUserManager;
import com.example.shifty.model.Employee;
import com.example.shifty.ui.fragment.AdminFragment.ScheduleFragment;
import com.example.shifty.ui.fragment.EmpFragment.ShiftsFragment;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdminAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;

    public CalendarAdminAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.days = days;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) parent.getHeight();

        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        final LocalDate date = days.get(position);
        if(date==null){
            holder.dayOfMonth.setText("");
        }else{
            String dateMonth = Integer.toString(date.getDayOfMonth());
            holder.dayOfMonth.setText(dateMonth);
            if(date.equals((ScheduleFragment.selectedDate)))
                holder.parentView.setBackgroundColor(Color.parseColor("#FCA311"));



        }

    }

    @Override
    public int getItemCount() {
        return days.size();
    }


}
