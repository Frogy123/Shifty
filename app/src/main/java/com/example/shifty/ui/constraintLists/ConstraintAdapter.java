package com.example.shifty.ui.constraintLists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shifty.R;
import com.example.shifty.model.CurrentUserManager;
import com.example.shifty.model.Employee;
import com.example.shifty.model.SchedulingAlgorithm.Constraint;
import com.example.shifty.model.SchedulingAlgorithm.TimeUtil;
import com.example.shifty.ui.calendar.CalendarViewHolder;

import java.time.LocalDate;
import java.util.List;

public class ConstraintAdapter extends RecyclerView.Adapter<ConstraintViewHolder> {


    private final List<Constraint> constraints;
    Employee currEmp = CurrentUserManager.getInstance().getCurrentEmployee();


    public ConstraintAdapter(){

        constraints = currEmp.getConstraints();

    }

    @NonNull
    @Override
    public ConstraintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_constraint, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        return new ConstraintViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ConstraintViewHolder holder, int position) {
        Constraint constraint = constraints.get(position);

        String constraintText = TimeUtil.getDayOfWeek(constraint.getDay()) + " " + constraint.getStartHour() + " - " + constraint.getEndHour();
        holder.constraintText.setText(constraintText);//bind data to the view

        holder.deleteButton.setOnClickListener(v -> {
            currEmp.deleteConstraint(position);
        });

    }

    @Override
    public int getItemCount() {
        return constraints.size();
    }




}
