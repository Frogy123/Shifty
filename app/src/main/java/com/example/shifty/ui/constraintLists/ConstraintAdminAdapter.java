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

/**
 * Adapter for displaying constraints for all employees (admin view) in a {@link RecyclerView}.
 * Each list item shows the employee's name, the day, and the constraint hours.
 * Admins can also delete constraints from this view.
 * <p>
 * This adapter relies on {@link EmployeeManager} to retrieve and manage employee data.
 * Each constraint is paired with the employee's UID for reference.
 * </p>
 *
 * <p>
 * <b>Related:</b>
 * <ul>
 *     <li>{@link ConstraintViewHolder}</li>
 *     <li>{@link Employee}</li>
 *     <li>{@link EmployeeManager}</li>
 *     <li>{@link Constraint}</li>
 *     <li>{@link TimeUtil}</li>
 *     <li>{@link ScheduleFragment}</li>
 * </ul>
 * </p>
 *
 * @author Eitan Navon
 */
public class ConstraintAdminAdapter extends RecyclerView.Adapter<ConstraintViewHolder> {

    /**
     * List of pairs, where each pair consists of an employee UID and their constraint.
     */
    private final List<Pair<String, Constraint>> constraints;

    /**
     * Reference to the {@link EmployeeManager} singleton for accessing employees.
     */
    private final EmployeeManager employeeManager = EmployeeManager.getInstance();

    /**
     * Constructs a new {@code ConstraintAdminAdapter}. Loads all constraints
     * for the currently selected day in {@link ScheduleFragment}.
     *
     * @throws NullPointerException if {@link EmployeeManager#getInstance()} returns null.
     */
    public ConstraintAdminAdapter() {
        constraints = new ArrayList<>();
        loadConstraint();
    }

    /**
     * Called when RecyclerView needs a new {@link ConstraintViewHolder}.
     *
     * @param parent   The parent {@link ViewGroup} into which the new view will be added
     * @param viewType The view type of the new view
     * @return A new {@link ConstraintViewHolder} for an item_constraint layout.
     * @throws NullPointerException if the context or inflater is not available.
     */
    @NonNull
    @Override
    public ConstraintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_constraint, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        return new ConstraintViewHolder(view);
    }

    /**
     * Binds the constraint data for an employee to a {@link ConstraintViewHolder}.
     * Shows the employee's name and their constraint.
     * The delete button removes the constraint from the employee's list.
     *
     * @param holder   The {@link ConstraintViewHolder} which should be updated
     * @param position The position of the item within the adapter's data set
     * @throws IndexOutOfBoundsException if position is invalid
     * @see Constraint
     */
    @Override
    public void onBindViewHolder(@NonNull ConstraintViewHolder holder, int position) {
        Pair<String, Constraint> constraintPair = constraints.get(position);

        Employee emp = employeeManager.getEmployee(constraintPair.first);
        if (emp == null) {
            // Defensive: should not happen unless data is corrupted
            holder.nameTextView.setText("Unknown");
            holder.constraintText.setText("Invalid constraint");
            return;
        }

        String empName = emp.getName();
        Constraint constraint = constraintPair.second;

        String constraintText = TimeUtil.getDayOfWeek(constraint.getDay()) + " " +
                constraint.getStartHour() + " - " + constraint.getEndHour();
        holder.constraintText.setText(constraintText); // Bind data to the view

        holder.nameTextView.setText(empName);

        holder.deleteButton.setOnClickListener(v -> {
            emp.deleteConstraint(position); // May throw IndexOutOfBoundsException if invalid
        });
    }

    /**
     * Returns the total number of items in the adapter.
     *
     * @return The size of the constraint list.
     */
    @Override
    public int getItemCount() {
        return constraints.size();
    }

    /**
     * Loads constraints for all employees that match the currently selected day
     * from {@link ScheduleFragment#selectedDate}.
     * Only constraints whose day matches the selected date are added.
     */
    public void loadConstraint() {
        List<Employee> employees = employeeManager.getEmployees();
        if (employeeManager.isInitialized()) {
            for (Employee employee : employees) {
                for (Constraint constraint : employee.getConstraints()) {
                    Pair<String, Constraint> constraintPair = new Pair<>(employee.getUid(), constraint);
                    if (constraint.getDay() == ScheduleFragment.selectedDate.getDayOfWeek().getValue()) {
                        constraints.add(constraintPair);
                    }
                }
            }
        }
    }

}
