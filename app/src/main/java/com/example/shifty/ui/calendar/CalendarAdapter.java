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
import com.example.shifty.ui.fragment.EmpFragment.ShiftsFragment;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * RecyclerView Adapter for displaying a calendar grid with selectable days.
 * Each cell represents a day, and the adapter manages the coloring and selection logic for each cell.
 *
 * <p>
 * The adapter uses {@link CalendarViewHolder} for cell representation and
 * requires an {@link OnItemListener} to handle click events on days.
 * </p>
 *
 * <b>Color logic:</b>
 * <ul>
 *     <li>Orange (#FCA311): currently selected date</li>
 *     <li>Green (#008000): if the current employee has a shift on the selected date</li>
 *     <li>Dark Blue (#14213D): default</li>
 * </ul>
 *
 * @author Eitan Navon
 * @see CalendarViewHolder
 * @see OnItemListener
 * @see ShiftsFragment
 * @see CurrentUserManager
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    /** List of {@link LocalDate} objects representing the days in the calendar view. */
    private final ArrayList<LocalDate> days;

    /** Listener for handling calendar cell click events. */
    private final OnItemListener onItemListener;

    /**
     * Constructs a new CalendarAdapter with the given days and listener.
     *
     * @param days           a list of {@link LocalDate} objects representing the days to display
     * @param onItemListener the listener for cell click events
     */
    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.days = days;
        this.onItemListener = onItemListener;
    }

    /**
     * Called when RecyclerView needs a new {@link CalendarViewHolder}.
     *
     * @param parent   the parent view group
     * @param viewType the type of the new view
     * @return a new instance of {@link CalendarViewHolder}
     */
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) parent.getHeight();
        return new CalendarViewHolder(view, onItemListener);
    }

    /**
     * Binds data to a {@link CalendarViewHolder} at the specified position.
     * Handles setting the day text and cell background color based on selection and shifts.
     *
     * @param holder   the view holder to bind
     * @param position the position in the data set
     * @throws NullPointerException if days or other referenced objects are unexpectedly null
     * @see ShiftsFragment#selectedDate
     * @see Employee#haveShift(LocalDate)
     */
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        final LocalDate date = days.get(position);
        if (date == null) {
            holder.dayOfMonth.setText("");
        } else {
            Employee currentEmployee = CurrentUserManager.getInstance().getCurrentEmployee();
            String dateMonth = Integer.toString(date.getDayOfMonth());
            holder.dayOfMonth.setText(dateMonth);

            if (date.equals(ShiftsFragment.selectedDate)) {
                holder.parentView.setBackgroundColor(Color.parseColor("#FCA311")); // Selected
            } else if (currentEmployee.haveShift(ShiftsFragment.selectedDate)) {
                holder.parentView.setBackgroundColor(Color.parseColor("#008000")); // Shift on selected day
            } else {
                holder.parentView.setBackgroundColor(Color.parseColor("#14213D")); // Default
            }
        }
    }

    /**
     * Returns the total number of items in the calendar.
     *
     * @return the number of days in the calendar view
     */
    @Override
    public int getItemCount() {
        return days.size();
    }
}
