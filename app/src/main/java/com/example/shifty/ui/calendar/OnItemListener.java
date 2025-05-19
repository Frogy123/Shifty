package com.example.shifty.ui.calendar;

public interface OnItemListener {
    void onItemClick(int position, String dayText);
    void onItemLongClick(int position, String dayText);

}
