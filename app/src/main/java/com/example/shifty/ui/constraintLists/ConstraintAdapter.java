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

/**
 * Adapter class for displaying a list of {@link Constraint} objects for the current employee
 * within a {@link RecyclerView}. Each item shows the day of the week and the start/end hour,
 * with an option to delete the constraint.
 *
 * <p>
 * This adapter retrieves constraints from the current employee instance managed by
 * {@link CurrentUserManager}. When the delete button is clicked, the constraint is removed
 * from the employee's list via {@link Employee#deleteConstraint(int)}.
 * </p>
 *
 * <p>
 * Related resources:
 * <ul>
 *     <li>{@link ConstraintViewHolder}</li>
 *     <li>{@link Constraint}</li>
 *     <li>{@link Employee}</li>
 *     <li>{@link CurrentUserManager}</li>
 * </ul>
 * </p>
 *
 * @author Eitan Navon
 */
public class ConstraintAdapter extends RecyclerView.Adapter<ConstraintViewHolder> {

    /**
     * List of constraints for the current employee.
     */
    private final List<Constraint> constraints;

    /**
     * Reference to the current employee, obtained from {@link CurrentUserManager}.
     */
    Employee currEmp = CurrentUserManager.getInstance().getCurrentEmployee();

    /**
     * Constructs a new {@code ConstraintAdapter}, retrieving the list of constraints
     * from the current employee.
     *
     * @throws NullPointerException if there is no current employee or their constraints are null
     */
    public ConstraintAdapter() {
        constraints = currEmp.getConstraints();
    }

    /**
     * Called when RecyclerView needs a new {@link ConstraintViewHolder} of the given type.
     *
     * @param parent The parent {@link ViewGroup} into which the new view will be added
     * @param viewType The view type of the new view
     * @return a new instance of {@link ConstraintViewHolder}
     * @throws NullPointerException if the parent context or layout inflater fails
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
     * Called by RecyclerView to display the data at the specified position. Binds the
     * constraint data to the {@link ConstraintViewHolder} and sets up a listener to
     * handle deletion.
     *
     * @param holder The {@link ConstraintViewHolder} which should be updated
     * @param position The position of the item within the adapter's data set
     * @throws IndexOutOfBoundsException if the position is out of range
     * @see Constraint
     */
    @Override
    public void onBindViewHolder(@NonNull ConstraintViewHolder holder, int position) {
        Constraint constraint = constraints.get(position);

        // Format: "Monday 9 - 13"
        String constraintText = TimeUtil.getDayOfWeek(constraint.getDay()) + " " +
                constraint.getStartHour() + " - " + constraint.getEndHour();
        holder.constraintText.setText(constraintText); // Bind data to the view

        holder.deleteButton.setOnClickListener(v -> {
            currEmp.deleteConstraint(position);
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The number of constraints to display
     */
    @Override
    public int getItemCount() {
        return constraints.size();
    }
}
