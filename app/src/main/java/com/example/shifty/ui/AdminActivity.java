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
import com.example.shifty.model.Role;
import com.example.shifty.ui.fragment.AdminFragment.ScheduleFragment;
import com.example.shifty.ui.fragment.EmpFragment.StatsFragment;
import com.example.shifty.ui.fragment.EmpFragment.UpdatesFragment;
import com.google.android.material.tabs.TabLayout;

/**
 * Activity representing the main admin screen for the application.
 * <p>
 * Allows switching between the Schedule, Stats, and Updates fragments using a TabLayout.
 * Handles edge-to-edge display, system bar insets, and fragment transactions for tab navigation.
 * </p>
 *
 * @author Eitan Navon
 * @see ScheduleFragment
 * @see StatsFragment
 * @see UpdatesFragment
 */
public class AdminActivity extends AppCompatActivity {

    /** Layout for displaying child fragments. */
    FrameLayout frameLayout;

    /** TabLayout for navigation between admin sections. */
    TabLayout tabLayout;

    /** Array holding all fragments available in this activity. */
    Fragment[] fragments;

    /**
     * Called when the activity is starting. Initializes the UI, sets up the fragments,
     * and attaches the TabLayout listener for navigation.
     *
     * @param savedInstanceState the previously saved state, or null if none exists
     * @see FragmentTransaction
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize layout and tabs
        frameLayout = findViewById(R.id.framelayout);
        tabLayout = findViewById(R.id.tabLayout);

        // Set up fragments for the tabs
        fragments = new Fragment[]{
                new ScheduleFragment(),
                new StatsFragment(),
                new UpdatesFragment(Role.ADMIN)
        };

        // Display the first fragment (ScheduleFragment) by default
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, fragments[0])
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        // Tab selection listener for switching fragments
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            /**
             * Called when a tab enters the selected state.
             *
             * @param tab the tab that was selected
             */
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, fragments[tab.getPosition()])
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }

            /**
             * Called when a tab exits the selected state.
             *
             * @param tab the tab that was unselected
             */
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not implemented
            }

            /**
             * Called when a tab that is already selected is chosen again.
             *
             * @param tab the tab that was reselected
             */
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not implemented
            }
        });

        // (Optional) Add refresh mechanism if needed in the future
    }
}
