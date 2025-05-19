package com.example.shifty.ui.calendar;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shifty.R;

public class CalendarViewHolder extends RecyclerView.ViewHolder
        implements  View.OnClickListener, View.OnLongClickListener {

    public final TextView dayOfMonth;
    public final View parentView;
    private final OnItemListener onItemListener;
    public CalendarViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
        super(itemView);
        this.dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.parentView = itemView.findViewById(R.id.parentView);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }


    @Override
    public void onClick(View view) {
        onItemListener.onItemClick(getAdapterPosition(), (String) dayOfMonth.getText());
    }

    @Override
    public boolean onLongClick(View view) {
        onItemListener.onItemLongClick(getAdapterPosition(), (String) dayOfMonth.getText());
        return true ; // return true to indicate that the long click was handled
    }
}
