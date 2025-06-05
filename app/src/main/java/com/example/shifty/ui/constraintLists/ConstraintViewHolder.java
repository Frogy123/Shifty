package com.example.shifty.ui.constraintLists;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shifty.R;

/**
 * ViewHolder for representing a single constraint entry in a RecyclerView list.
 * Holds references to UI components for displaying the constraint, the associated name, and a delete button.
 *
 * <p>
 * Used in conjunction with {@link ConstraintListAdapter} to efficiently manage constraint list items.
 * </p>
 *
 * @author Eitan Navon
 * @see ConstraintListAdapter
 */
public class ConstraintViewHolder extends RecyclerView.ViewHolder {

    /** TextView displaying the constraint description. */
    TextView constraintText;

    /** Button for deleting this constraint from the list. */
    Button deleteButton;

    /** TextView displaying the name associated with the constraint (if any). */
    TextView nameTextView;

    /**
     * Constructs a new ConstraintViewHolder and initializes its UI components.
     *
     * @param itemView the root view representing the constraint list item
     */
    public ConstraintViewHolder(@NonNull View itemView) {
        super(itemView);

        constraintText = itemView.findViewById(R.id.constraintText);
        deleteButton = itemView.findViewById(R.id.removeConstraintButton);
        nameTextView = itemView.findViewById(R.id.nameTextView);
    }
}
