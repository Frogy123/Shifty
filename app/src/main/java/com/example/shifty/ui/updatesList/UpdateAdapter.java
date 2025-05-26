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

import java.sql.Time;
import java.util.List;


public class UpdateAdapter extends RecyclerView.Adapter<UpdateViewHolder>{

    private final String TAG = "UpdateAdapter";
    private List<Update> updates;
    private final UpdateManager updateManager = UpdateManager.getInstance();

    public UpdateAdapter() {
        updates = updateManager.getUpdates();
    }



    @NonNull
    @Override
    public UpdateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_update, parent, false);
        return new UpdateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateViewHolder holder, int position) {

        Update update = updates.get(position);

        holder.updateTitle.setText(update.getName());
        holder.updateDate.setText(TimeUtil.formatDate(update.getDate()));
        holder.updateTime.setText(TimeUtil.formatTime(update.getDate()));
        holder.updateDescription.setText(update.getDescription());
        int visibility = update.isCritical() ? View.VISIBLE : View.GONE;
        holder.updateImage.setVisibility(visibility);


        // Set click listener for the update item
        holder.itemView.setOnLongClickListener(v -> {
            // Handle click event, e.g., show update details

            return true; // Return true to indicate the click was handled
        });

    }

    @Override
    public int getItemCount() {
        return updateManager.getUpdatesCount();
    }
}
