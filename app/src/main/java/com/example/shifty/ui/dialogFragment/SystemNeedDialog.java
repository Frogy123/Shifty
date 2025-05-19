package com.example.shifty.ui.dialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.shifty.R;
import com.example.shifty.viewmodel.dialogFragment.ConstraintDialogViewModel;

public class SystemNeedDialog extends DialogFragment implements View.OnClickListener {



    ConstraintDialogViewModel constraintDialogViewModel;

    Button add, cancel;
    Spinner daySpinner;
    int startingDay;

    EditText EmpPerHourEditText;

    ConstraintDialog.Communicator comm;

    public SystemNeedDialog(int day){
        startingDay = day;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_system_need_dialog, container, false);

        //init view model
        constraintDialogViewModel = new ViewModelProvider(this).get(ConstraintDialogViewModel.class);
        initWidget(view);


        setCancelable(false);

        return  view;
    }

    private void initWidget(View view){
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

        daySpinner.setSelection(startingDay);

        //edit text
        EmpPerHourEditText = view.findViewById(R.id.EmpPerHourInput);
    }

    public void onCancelClick(View view){
        dismiss();
    }


    public void onAddSystemNeedClick(View view){
        int selectedDay = daySpinner.getSelectedItemPosition();
        String empPerHourString = EmpPerHourEditText.getText().toString();

        Bundle result = new Bundle();
        result.putInt("day", selectedDay);
        result.putInt("empPerHour", Integer.parseInt(empPerHourString));

        getParentFragmentManager().setFragmentResult("SystemNeedRequest", result);
        dismiss();

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.add){
            onAddSystemNeedClick(view);
        }else if ((view.getId() == R.id.cancel)) {
            onCancelClick(view);
        }
    }

    public interface Communicator{
        void onSystemNeedAdded(int day, int startHour, int endHour);
    }

}
