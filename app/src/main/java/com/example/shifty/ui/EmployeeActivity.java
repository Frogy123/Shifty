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
import com.example.shifty.ui.fragment.ShiftsFragment;
import com.example.shifty.ui.fragment.StatsFragment;
import com.example.shifty.ui.fragment.UpdatesFragment;
import com.google.android.material.tabs.TabLayout;

public class EmployeeActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    TabLayout  tabLayout;

    Fragment[] fragments;

    Employee currentEmp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //add refresh Mechanisam
        currentEmp = CurrentUserManager.getInstance().getCurrentEmployee();
        currentEmp.getRefresh().observe(this, needRefresh -> {;
            if (needRefresh) {
                recreate();
                currentEmp.getRefresh().postValue(false);
            }
        });

        frameLayout = findViewById(R.id.framelayout);
        tabLayout = findViewById(R.id.tabLayout);

        fragments = new Fragment[]{
                new ShiftsFragment(),
                new StatsFragment(),
                new UpdatesFragment()
        };

        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, fragments[0])
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, fragments[tab.getPosition()])
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //null
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //null
            }
        });

        //add refresh Mechanisam


    }

}