package com.example.shifty.ui.calendar;

/**
 * Listener interface for handling item click and long-click events in the calendar view.
 * <p>
 * Implement this interface to define custom behaviors when a calendar cell is clicked or long-pressed.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * calendarAdapter = new CalendarAdminAdapter(days, new OnItemListener() {
 *     &#64;Override
 *     public void onItemClick(int position, String dayText) {
 *         // Handle click event
 *     }
 *
 *     &#64;Override
 *     public void onItemLongClick(int position, String dayText) {
 *         // Handle long-click event
 *     }
 * });
 * }
 * </pre>
 *
 * @author Eitan Navon
 * @see CalendarAdminAdapter
 */
public interface OnItemListener {
    /**
     * Called when a calendar item is clicked.
     *
     * @param position the adapter position of the clicked item
     * @param dayText the string representation of the day (e.g., "15" for the 15th of the month)
     */
    void onItemClick(int position, String dayText);

    /**
     * Called when a calendar item is long-pressed.
     *
     * @param position the adapter position of the long-pressed item
     * @param dayText the string representation of the day (e.g., "15" for the 15th of the month)
     */
    void onItemLongClick(int position, String dayText);
}
