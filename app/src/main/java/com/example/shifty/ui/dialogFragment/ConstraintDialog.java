package com.example.shifty.ui.dialogFragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.shifty.R;
import com.example.shifty.viewmodel.LoginViewModel;
import com.example.shifty.viewmodel.dialogFragment.ConstraintDialogViewModel;

public class ConstraintDialog extends DialogFragment implements View.OnClickListener {

    ConstraintDialogViewModel constraintDialogViewModel;

    Button add, cancel;
    Spinner daySpinner;

    EditText startHourEditText;
    EditText endHourEditText;

    Communicator comm;

    public void onAttach(Activity activity){
        super.onAttach(activity);

        comm = (Communicator) activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_constraint_dialog, container, false);

        //init view model
        constraintDialogViewModel = new ViewModelProvider(this).get(ConstraintDialogViewModel.class);

        //buttons
        add = (Button) view.findViewById(R.id.add);
        cancel = (Button) (view.findViewById(R.id.cancel));

        add.setOnClickListener(this);
        cancel.setOnClickListener(this);

        //spinner (day select)
        daySpinner = view.findViewById(R.id.daySpinner);

        // Set up the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.days_of_week,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);

        //edit text
        startHourEditText = view.findViewById(R.id.fromInput);
        endHourEditText = view.findViewById(R.id.toInput);


        setCancelable(false);

        return  view;
    }

    public void onCancelClick(View view){
        dismiss();
    }


    public void onAddConstraintClick(View view){
        int selectedDay = daySpinner.getSelectedItemPosition();
        int startHour = Integer.parseInt(startHourEditText.getText().toString());
        int endHour = Integer.parseInt(startHourEditText.getText().toString());

        comm.onConstraintAdded(selectedDay, startHour, endHour);

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.addConstraints){
            onAddConstraintClick(view);
        }else if ((view.getId() == R.id.cancel)) {
            onCancelClick(view);
        }
    }

    public interface Communicator{
        void onConstraintAdded(int day, int startHour, int endHour);
    }
}
