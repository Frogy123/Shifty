package com.example.shifty.ui;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.shifty.R;
import com.example.shifty.model.CurrentUserManager;
import com.example.shifty.model.Employee;
import com.example.shifty.model.Role;
import com.example.shifty.ui.fragment.EmpFragment.ShiftsFragment;
import com.example.shifty.ui.fragment.EmpFragment.StatsFragment;
import com.example.shifty.ui.fragment.EmpFragment.UpdatesFragment;
import com.google.android.material.tabs.TabLayout;

/**
 * Activity representing the main employee interface in the application.
 * <p>
 * Displays a tabbed navigation bar and handles fragment switching for the employee's shifts, statistics, and updates.
 * Supports edge-to-edge layouts, automatic refresh on employee data changes, and fragment transitions.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * // In AndroidManifest.xml:
 * &lt;activity android:name=".ui.EmployeeActivity" ... /&gt;
 * </pre>
 *
 * @author Eitan Navon
 * @see ShiftsFragment
 * @see StatsFragment
 * @see UpdatesFragment
 * @see TabLayout
 * @see CurrentUserManager
 */
public class EmployeeActivity extends AppCompatActivity {

    /** Container for displaying the selected fragment. */
    FrameLayout frameLayout;

    /** TabLayout for navigation between fragments. */
    TabLayout tabLayout;

    /** Array of fragments corresponding to each tab. */
    Fragment[] fragments;

    /** The currently logged-in employee. */
    Employee currentEmp;

    /**
     * Called when the activity is starting. Initializes the UI, sets up tab navigation,
     * observes refresh triggers, and loads the default fragment.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied. Otherwise, it is null.
     * @throws NullPointerException if the layout or resources are missing
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_screen);

        // Handle system window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Observe the current employee's refresh LiveData; recreate the activity on changes
        currentEmp = CurrentUserManager.getInstance().getCurrentEmployee();
        currentEmp.getRefresh().observe(this, needRefresh -> {
            if (needRefresh) {
                recreate();
                currentEmp.getRefresh().postValue(false);
            }
        });

        frameLayout = findViewById(R.id.framelayout);
        tabLayout = findViewById(R.id.tabLayout);

        // Initialize the fragments array
        fragments = new Fragment[]{
                new ShiftsFragment(),
                new StatsFragment(),
                new UpdatesFragment(Role.EMPLOYEE)
        };

        // Load the default fragment (shifts) on startup
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, fragments[0])
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        // Set up tab selection listener for fragment switching
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, fragments[tab.getPosition()])
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No action needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No action needed
            }
        });
    }
}
