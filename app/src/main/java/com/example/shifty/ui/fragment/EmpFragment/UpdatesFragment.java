package com.example.shifty.ui.fragment.EmpFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shifty.R;
import com.example.shifty.model.Role;
import com.example.shifty.model.UpdateManager;
import com.example.shifty.ui.dialogFragment.addUpdateDialog;
import com.example.shifty.ui.updatesList.UpdateAdapter;
import com.example.shifty.ui.updatesList.UpdateViewHolder;
import com.example.shifty.viewmodel.fragment.Employee.UpdatesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UpdatesFragment extends Fragment {


    Role role;
    FloatingActionButton addUpdateButton;

    TextView errorMessage;
    MutableLiveData<String> errorMsgData;
    RecyclerView recyclerView;

    UpdatesViewModel updateViewModel;
    MutableLiveData<Boolean> needRefresh;

    public UpdatesFragment(Role _role) {
        this.role = _role;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_updates, container, false);



        updateViewModel = new ViewModelProvider(this).get(UpdatesViewModel.class);

        addUpdateResultListener();
        initWidget(view);

        needRefresh.observe(getViewLifecycleOwner(), needRefresh -> {;
            if (needRefresh) {
                // Refresh the updates list
                recyclerView.setAdapter(new UpdateAdapter());
            }
        });

        return view;
    }

    public void initWidget(View view) {


        //initialize errorMessage
        errorMsgData = updateViewModel.getErrorMsg();
        errorMessage = view.findViewById(R.id.errorMsgId);
        // Observe error messages
        errorMsgData.observe(getViewLifecycleOwner(), errorMessage::setText);
        needRefresh = UpdateManager.getInstance().getNeedRefresh();




        // Initialize the UpdateManager
        recyclerView = view.findViewById(R.id.updatesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new UpdateAdapter());



        // Initialize the addUpdateButton
        addUpdateButton = view.findViewById(R.id.addUpdateButton);

        addUpdateButton.setOnClickListener(this::addUpdateButtonOnClick);





        // Set the visibility of the add button based on the role
        setAddButtonVisiabilty();

        }


    public void addUpdateResultListener(){
        // Set up FragmentResultListener to get result from addConstraint
        getParentFragmentManager().setFragmentResultListener("UpdateAddRequest", this, (requestKey, result) -> {

            String title = result.getString("title");
            String description = result.getString("description");
            boolean isPinned = result.getBoolean("isPinned");
            long date = result.getLong("date");

            updateViewModel.addUpadate(title, description, isPinned, date);

        });

    }

    public void addUpdateButtonOnClick(View view) {
        // Handle the add update button click
        addUpdateDialog dialog = new addUpdateDialog();
        dialog.show(getParentFragmentManager(), "addUpdateDialog");

    }
    private void setAddButtonVisiabilty(){

        if (role == Role.EMPLOYEE) {
            // Hide the add button for employees
            addUpdateButton.setVisibility(View.GONE);
        } else {
            // Show the add button for other roles
            addUpdateButton.setVisibility(View.VISIBLE);
        }

    }
}
