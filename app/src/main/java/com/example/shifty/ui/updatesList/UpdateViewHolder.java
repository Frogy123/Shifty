package com.example.shifty.ui.updatesList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shifty.R;

public class UpdateViewHolder extends RecyclerView.ViewHolder {
    TextView updateTitle;
    TextView updateDate;

    TextView updateTime;

    TextView updateDescription;
    ImageView updateImage;



    public UpdateViewHolder(@NonNull View itemView) {
        super(itemView);
        updateTitle = itemView.findViewById(R.id.updateTitle);
        updateDate = itemView.findViewById(R.id.updateDate);
        updateTime = itemView.findViewById(R.id.updateTime);
        updateDescription = itemView.findViewById(R.id.updateMessage);
        updateImage = itemView.findViewById(R.id.pinIcon);
    }
}