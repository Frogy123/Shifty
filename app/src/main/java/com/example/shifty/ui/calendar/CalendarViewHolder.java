package com.example.shifty.ui.calendar;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shifty.R;

/**
 * ViewHolder for individual calendar cells in the {@link CalendarAdapter}.
 * Handles click and long-click interactions for each day cell.
 *
 * <p>
 * Each holder displays a day of the month, and notifies the {@link OnItemListener}
 * when the user clicks or long-clicks a day.
 * </p>
 *
 * @author Eitan Navon
 * @see CalendarAdapter
 * @see OnItemListener
 */
public class CalendarViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener {

    /** TextView displaying the day of the month. */
    public final TextView dayOfMonth;

    /** The parent view for the cell, used for background color changes, etc. */
    public final View parentView;

    /** The listener for click and long click events on this cell. */
    private final OnItemListener onItemListener;

    /**
     * Constructs a CalendarViewHolder for a calendar cell.
     *
     * @param itemView        the view for the calendar cell
     * @param onItemListener  listener for handling click and long click events
     */
    public CalendarViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
        super(itemView);
        this.dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.parentView = itemView.findViewById(R.id.parentView);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    /**
     * Handles click events for the calendar cell.
     * Notifies the {@link OnItemListener#onItemClick(int, String)} with the cell position and day text.
     *
     * @param view the clicked view
     */
    @Override
    public void onClick(View view) {
        onItemListener.onItemClick(getAdapterPosition(), (String) dayOfMonth.getText());
    }

    /**
     * Handles long click events for the calendar cell.
     * Notifies the {@link OnItemListener#onItemLongClick(int, String)} with the cell position and day text.
     *
     * @param view the long-clicked view
     * @return {@code true} to indicate the long click event was handled
     */
    @Override
    public boolean onLongClick(View view) {
        onItemListener.onItemLongClick(getAdapterPosition(), (String) dayOfMonth.getText());
        return true;
    }
}
