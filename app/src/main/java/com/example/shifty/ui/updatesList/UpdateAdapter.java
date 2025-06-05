package com.example.shifty.ui.updatesList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shifty.R;
import com.example.shifty.model.SchedulingAlgorithm.TimeUtil;
import com.example.shifty.model.Update;
import com.example.shifty.model.UpdateManager;

import java.util.List;

/**
 * Adapter for displaying a list of {@link Update} objects in a {@link RecyclerView}.
 * Each item shows the update's title, date, time, description, and a critical indicator if relevant.
 * <p>
 * The adapter retrieves updates from the singleton {@link UpdateManager}.
 * </p>
 *
 * <b>Related:</b>
 * <ul>
 *     <li>{@link UpdateViewHolder}</li>
 *     <li>{@link Update}</li>
 *     <li>{@link UpdateManager}</li>
 *     <li>{@link TimeUtil}</li>
 * </ul>
 *
 * @author Eitan Navon
 */
public class UpdateAdapter extends RecyclerView.Adapter<UpdateViewHolder> {

    /** Tag for logging and debugging. */
    private final String TAG = "UpdateAdapter";
    /** List of updates to display. */
    private List<Update> updates;
    /** Singleton manager for retrieving updates. */
    private final UpdateManager updateManager = UpdateManager.getInstance();

    /**
     * Constructs a new {@code UpdateAdapter} and initializes the updates list
     * from the {@link UpdateManager}.
     *
     * @throws NullPointerException if {@link UpdateManager#getInstance()} or its data is null.
     */
    public UpdateAdapter() {
        updates = updateManager.getUpdates();
    }

    /**
     * Creates a new {@link UpdateViewHolder} for the update item view.
     *
     * @param parent The parent {@link ViewGroup} into which the new view will be added.
     * @param viewType The view type of the new view.
     * @return A new instance of {@link UpdateViewHolder}.
     * @throws NullPointerException if the parent context or inflater fails.
     */
    @NonNull
    @Override
    public UpdateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_update, parent, false);
        return new UpdateViewHolder(view);
    }

    /**
     * Binds an {@link Update} item to the given {@link UpdateViewHolder}.
     * Sets the title, date, time, description, and critical indicator.
     * Handles long-click events on the item view.
     *
     * @param holder The {@link UpdateViewHolder} to bind data to.
     * @param position The position of the item within the updates list.
     * @throws IndexOutOfBoundsException if the position is out of range.
     * @see Update
     */
    @Override
    public void onBindViewHolder(@NonNull UpdateViewHolder holder, int position) {

        Update update = updates.get(position);

        holder.updateTitle.setText(update.getName());
        holder.updateDate.setText(TimeUtil.formatDate(update.getDate()));
        holder.updateTime.setText(TimeUtil.formatTime(update.getDate()));
        holder.updateDescription.setText(update.getDescription());

        // Show or hide the critical update image.
        int visibility = update.isCritical() ? View.VISIBLE : View.GONE;
        holder.updateImage.setVisibility(visibility);

        // Set long click listener for the update item (optional: show update details, etc.)
        holder.itemView.setOnLongClickListener(v -> {
            // Handle long click event (extend as needed)
            return true; // Return true to indicate the event was handled
        });

    }

    /**
     * Returns the number of update items managed by the adapter.
     *
     * @return The count of updates.
     */
    @Override
    public int getItemCount() {
        return updateManager.getUpdatesCount();
    }
}
