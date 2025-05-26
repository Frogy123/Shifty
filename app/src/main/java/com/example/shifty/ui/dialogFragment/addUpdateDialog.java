package com.example.shifty.ui.dialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.shifty.R;
import com.example.shifty.viewmodel.dialogFragment.ConstraintDialogViewModel;

public class addUpdateDialog extends DialogFragment  implements View.OnClickListener {

    EditText titleInput;
    EditText descriptionInput;

    Button addButton;
    Button cancelButton;

    CheckBox isPinnedCheckBox;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_add_dialog, container, false);

        initWidget(view);
        setCancelable(false);

        return  view;
    }

    private void initWidget(View view) {

        titleInput = view.findViewById(R.id.titleInput);
        descriptionInput = view.findViewById(R.id.descriptionInput);

        addButton = view.findViewById(R.id.add);
        cancelButton = view.findViewById(R.id.cancel);

        isPinnedCheckBox = view.findViewById(R.id.pinCheckbox);

        addButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.add) {
            onAddUpdateClick(view);
        } else if(view.getId() == R.id.cancel) {
            dismiss();
        }

    }

    private void onAddUpdateClick(View view) {

        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();
        long date = System.currentTimeMillis(); // Use current time as a placeholder
        boolean isPinned = isPinnedCheckBox.isChecked();


        Bundle result = new Bundle();
        result.putString("title", title);
        result.putString("description", description);
        result.putLong("date", date);
        result.putBoolean("isPinned", isPinned);

        getParentFragmentManager().setFragmentResult("UpdateAddRequest", result);
        dismiss();
    }

    public interface Communicator {
        void onDialogMessage(String message);
    }
}
