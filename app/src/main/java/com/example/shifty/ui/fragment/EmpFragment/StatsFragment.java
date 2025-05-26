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


public class StatsFragment extends Fragment {


    TextView emailTextView;
    TextView nameTextView;
    TextView passwordTextView;
    TextView roleTextView;
    StatsViewModel statsViewModel;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        statsViewModel = new ViewModelProvider(this).get(StatsViewModel.class);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        statsViewModel.loadData();
        initWidget(view);


        return view;
    }

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