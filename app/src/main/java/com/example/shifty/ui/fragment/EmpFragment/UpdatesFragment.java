package com.example.shifty.ui.fragment.EmpFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.shifty.viewmodel.fragment.Employee.UpdatesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Fragment for displaying and managing updates (announcements, messages, etc.) for employees and admins.
 * <p>
 * Provides a RecyclerView of updates, handles add-update dialog interaction,
 * shows error messages, and conditionally shows/hides the add button based on the user's {@link Role}.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * // To display UpdatesFragment for admin
 * UpdatesFragment fragment = new UpdatesFragment(Role.ADMIN);
 * getSupportFragmentManager()
 *     .beginTransaction()
 *     .replace(R.id.fragment_container, fragment)
 *     .commit();
 * </pre>
 *
 * @author Eitan Navon
 * @see Role
 * @see UpdateAdapter
 * @see addUpdateDialog
 * @see UpdatesViewModel
 */
public class UpdatesFragment extends Fragment {

    /** The user's role (e.g. admin, employee). */
    Role role;

    /** FloatingActionButton for adding a new update (visible to admins/managers only). */
    FloatingActionButton addUpdateButton;

    /** Displays error messages to the user. */
    TextView errorMessage;

    /** LiveData for error message updates. */
    MutableLiveData<String> errorMsgData;

    /** RecyclerView displaying the list of updates. */
    RecyclerView recyclerView;

    /** ViewModel for managing update data. */
    UpdatesViewModel updateViewModel;

    /** LiveData for notifying when the update list needs to be refreshed. */
    MutableLiveData<Boolean> needRefresh;

    /**
     * Constructs a new UpdatesFragment for the given role.
     *
     * @param _role the user's role (affects UI and permissions)
     */
    public UpdatesFragment(Role _role) {
        this.role = _role;
    }

    /**
     * Inflates the fragment layout, initializes widgets and listeners, and observes LiveData.
     *
     * @param inflater the LayoutInflater used to inflate the view
     * @param container the parent view group
     * @param savedInstanceState saved instance state bundle
     * @return the root view of the fragment
     * @throws NullPointerException if inflater is {@code null}
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_updates, container, false);

        updateViewModel = new ViewModelProvider(this).get(UpdatesViewModel.class);

        addUpdateResultListener();
        initWidget(view);

        needRefresh.observe(getViewLifecycleOwner(), needRefresh -> {
            if (needRefresh) {
                // Refresh the updates list
                recyclerView.setAdapter(new UpdateAdapter());
            }
        });

        return view;
    }

    /**
     * Initializes all widgets and LiveData observers in the fragment.
     *
     * @param view the root view containing the widgets
     * @throws NullPointerException if view is {@code null}
     */
    public void initWidget(View view) {
        // Initialize error message and its LiveData
        errorMsgData = updateViewModel.getErrorMsg();
        errorMessage = view.findViewById(R.id.errorMsgId);
        errorMsgData.observe(getViewLifecycleOwner(), errorMessage::setText);

        // LiveData to refresh the updates list when data changes
        needRefresh = UpdateManager.getInstance().getNeedRefresh();

        // Initialize RecyclerView and its adapter
        recyclerView = view.findViewById(R.id.updatesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new UpdateAdapter());

        // Initialize the add-update button
        addUpdateButton = view.findViewById(R.id.addUpdateButton);
        addUpdateButton.setOnClickListener(this::addUpdateButtonOnClick);

        // Set add button visibility based on role
        setAddButtonVisiabilty();
    }

    /**
     * Registers a result listener to handle the result from the add-update dialog.
     * Adds the new update to the ViewModel when the dialog returns data.
     */
    public void addUpdateResultListener() {
        getParentFragmentManager().setFragmentResultListener("UpdateAddRequest", this, (requestKey, result) -> {
            String title = result.getString("title");
            String description = result.getString("description");
            boolean isPinned = result.getBoolean("isPinned");
            long date = result.getLong("date");

            updateViewModel.addUpadate(title, description, isPinned, date);
        });
    }

    /**
     * Handles the click event for the add-update button. Opens the add-update dialog.
     *
     * @param view the clicked button view
     */
    public void addUpdateButtonOnClick(View view) {
        addUpdateDialog dialog = new addUpdateDialog();
        dialog.show(getParentFragmentManager(), "addUpdateDialog");
    }

    /**
     * Sets the visibility of the add-update button based on the user's role.
     * Employees cannot add updates, so the button is hidden for them.
     */
    private void setAddButtonVisiabilty() {
        if (role == Role.EMPLOYEE) {
            addUpdateButton.setVisibility(View.GONE);
        } else {
            addUpdateButton.setVisibility(View.VISIBLE);
        }
    }
}
