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

/**
 * RecyclerView.Adapter for displaying calendar days for admins.
 * <p>
 * This adapter is responsible for binding {@link LocalDate} objects to calendar cell views,
 * handling click events, and highlighting the selected date.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * CalendarAdminAdapter adapter = new CalendarAdminAdapter(days, listener);
 * recyclerView.setAdapter(adapter);
 * }
 * </pre>
 *
 * @author Eitan Navon
 * @see CalendarViewHolder
 * @see RecyclerView.Adapter
 * @see LocalDate
 * @see ScheduleFragment
 */
public class CalendarAdminAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    /** List of dates to be displayed in the calendar. */
    private final ArrayList<LocalDate> days;
    /** Listener for item click events. */
    private final OnItemListener onItemListener;

    /**
     * Constructs a new CalendarAdminAdapter.
     *
     * @param days           list of {@link LocalDate} objects representing each day cell
     * @param onItemListener listener for item click events
     * @throws NullPointerException if days or onItemListener is null
     */
    public CalendarAdminAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.days = days;
        this.onItemListener = onItemListener;
    }

    /**
     * Inflates the calendar cell layout and creates a new {@link CalendarViewHolder}.
     *
     * @param parent   the parent view group
     * @param viewType the view type of the new view
     * @return a new {@link CalendarViewHolder} instance
     * @throws NullPointerException if parent is null
     * @link CalendarViewHolder
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
     * Binds the {@link LocalDate} at the given position to the calendar cell view.
     * If the date is null, the cell is empty. If the date matches the selected date in
     * {@link ScheduleFragment}, the cell is highlighted.
     *
     * @param holder   the {@link CalendarViewHolder} to update
     * @param position the position of the item within the adapter's data set
     * @throws NullPointerException if holder is null
     */
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        final LocalDate date = days.get(position);
        if(date == null){
            holder.dayOfMonth.setText("");
        } else {
            String dateMonth = Integer.toString(date.getDayOfMonth());
            holder.dayOfMonth.setText(dateMonth);
            if(date.equals(ScheduleFragment.selectedDate))
                holder.parentView.setBackgroundColor(Color.parseColor("#FCA311"));
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return the number of calendar cells
     */
    @Override
    public int getItemCount() {
        return days.size();
    }

}
