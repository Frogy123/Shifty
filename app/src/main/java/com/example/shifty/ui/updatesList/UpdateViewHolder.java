package com.example.shifty.ui.updatesList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shifty.R;

/**
 * ViewHolder for the updates list in the RecyclerView.
 * <p>
 * This class holds references to the views for each update item (title, date, time, description, and image).
 * It is used by {@link UpdateAdapter} to efficiently bind data to the item views.
 * </p>
 *
 * <p>
 * Usage example: In your adapter's {@code onBindViewHolder()}, you access these fields to update the UI.
 * </p>
 *
 * @author Eitan Navon
 * @see UpdateAdapter
 */
public class UpdateViewHolder extends RecyclerView.ViewHolder {

    /**
     * The title of the update message.
     */
    TextView updateTitle;

    /**
     * The date of the update.
     */
    TextView updateDate;

    /**
     * The time of the update.
     */
    TextView updateTime;

    /**
     * The full description or message body of the update.
     */
    TextView updateDescription;

    /**
     * An icon or image related to the update (e.g., a pin icon).
     */
    ImageView updateImage;

    /**
     * Constructs a new {@code UpdateViewHolder} and initializes view references.
     *
     * @param itemView The root view of the item layout. Typically provided by {@link UpdateAdapter} in {@code onCreateViewHolder()}.
     */
    public UpdateViewHolder(@NonNull View itemView) {
        super(itemView);
        updateTitle = itemView.findViewById(R.id.updateTitle);
        updateDate = itemView.findViewById(R.id.updateDate);
        updateTime = itemView.findViewById(R.id.updateTime);
        updateDescription = itemView.findViewById(R.id.updateMessage);
        updateImage = itemView.findViewById(R.id.pinIcon);
    }
}
