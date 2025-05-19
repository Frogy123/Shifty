package com.example.shifty.ui.constraintLists;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shifty.R;
import com.example.shifty.model.Employee;
import com.example.shifty.model.SchedulingAlgorithm.Constraint;
import com.example.shifty.model.EmployeeManager;
import com.example.shifty.model.SchedulingAlgorithm.TimeUtil;
import com.example.shifty.ui.fragment.AdminFragment.ScheduleFragment;

import java.util.ArrayList;
import java.util.List;

public class ConstraintAdminAdapter extends RecyclerView.Adapter<ConstraintViewHolder> {


    private final List<Pair<String, Constraint>> constraints;
    private final EmployeeManager employeeManager = EmployeeManager.getInstance();




    public ConstraintAdminAdapter(){
        constraints = new ArrayList<Pair<String, Constraint>>();
        loadConstraint();

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
        Pair<String, Constraint> constraintPair = constraints.get(position);

        Employee emp = employeeManager.getEmployee(constraintPair.first);

        String empName = emp.getName();
        Constraint constraint = constraintPair.second;

        String constraintText = TimeUtil.getDayOfWeek(constraint.getDay()) + " " + constraint.getStartHour() + " - " + constraint.getEndHour();
        holder.constraintText.setText(constraintText);//bind data to the view

        holder.nameTextView.setText(empName);

        holder.deleteButton.setOnClickListener(v -> {
            emp.deleteConstraint(position);
        });

    }

    @Override
    public int getItemCount() {
        return constraints.size();
    }

    public void loadConstraint(){
        List<Employee> employees = employeeManager.getEmployees();
        if(employeeManager.isInitialized()){
            for (Employee employee : employees) {
                for(Constraint constraint : employee.getConstraints()){
                    Pair<String, Constraint> constraintPair = new Pair<>(employee.getUid(), constraint);
                    if(constraint.getDay() == ScheduleFragment.selectedDate.getDayOfWeek().getValue()){
                        constraints.add(constraintPair);
                    }

                }
            }
        }


    }



}
