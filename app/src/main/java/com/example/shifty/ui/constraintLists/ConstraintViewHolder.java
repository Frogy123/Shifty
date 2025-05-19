package com.example.shifty.ui.constraintLists;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shifty.R;

public class ConstraintViewHolder extends RecyclerView.ViewHolder {

    TextView constraintText;
    Button deleteButton;

    TextView nameTextView;


    public ConstraintViewHolder(@NonNull View itemView) {
        super(itemView);

        constraintText = itemView.findViewById(R.id.constraintText);
        deleteButton = itemView.findViewById(R.id.removeConstraintButton);
        nameTextView = itemView.findViewById(R.id.nameTextView);
    }
}
