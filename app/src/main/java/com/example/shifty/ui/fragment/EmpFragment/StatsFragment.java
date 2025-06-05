package com.example.shifty.ui.fragment.EmpFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shifty.R;
import com.example.shifty.viewmodel.fragment.Employee.StatsViewModel;

/**
 * Fragment for displaying basic employee statistics and profile information.
 * <p>
 * Shows email, username, password, and role for the current employee,
 * with all data provided by a {@link StatsViewModel}.
 * </p>
 *
 * <b>Note:</b> Displaying the password is generally discouraged for security reasons.
 *
 * @author Eitan Navon
 * @see StatsViewModel
 * @see androidx.fragment.app.Fragment
 */
public class StatsFragment extends Fragment {

    /** Displays the user's email. */
    TextView emailTextView;
    /** Displays the user's username. */
    TextView nameTextView;
    /** Displays the user's password (discouraged). */
    TextView passwordTextView;
    /** Displays the user's role. */
    TextView roleTextView;
    /** ViewModel providing the statistics and user data. */
    StatsViewModel statsViewModel;

    /**
     * Called to do initial creation of the fragment and to initialize the {@link StatsViewModel}.
     *
     * @param savedInstanceState the previously saved state, if any
     * @see ViewModelProvider
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        statsViewModel = new ViewModelProvider(this).get(StatsViewModel.class);
    }

    /**
     * Inflates the fragment's layout and initializes the UI widgets.
     *
     * @param inflater           the LayoutInflater object that can be used to inflate views
     * @param container          the parent view that the fragment's UI should attach to
     * @param savedInstanceState previously saved state, if any
     * @return the root view for the fragment's UI
     * @see #initWidget(View)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        statsViewModel.loadData();
        initWidget(view);
        return view;
    }

    /**
     * Initializes the UI components and sets their values based on the ViewModel.
     *
     * @param view the root view of the fragment's layout
     */
    private void initWidget(View view) {
        emailTextView = view.findViewById(R.id.textViewEmail);
        nameTextView = view.findViewById(R.id.textViewUsername);
        passwordTextView = view.findViewById(R.id.textViewPassword);
        roleTextView = view.findViewById(R.id.textViewRole);

        emailTextView.setText(statsViewModel.getEmail());
        nameTextView.setText(statsViewModel.getName());
        passwordTextView.setText(statsViewModel.getPassword());
        roleTextView.setText(statsViewModel.getRole());
        // Set the text views with data from ViewModel
    }
}
