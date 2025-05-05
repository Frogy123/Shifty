package com.example.shifty.model;

import android.util.Log;

import com.example.shifty.model.SchedulingAlgorithm.Constraint;
import com.example.shifty.model.SchedulingAlgorithm.Shift;

import java.util.ArrayList;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Employee implements Comparable{




    ArrayList<Constraint> constraints;
    ArrayList<Integer> specialProperties;
    ArrayList<Shift> shifts;

    int uid;

    public Employee (int uid){
        this.uid = uid;
        constraints = new ArrayList<>();
        specialProperties = new ArrayList<>();
        shifts = new ArrayList<>();
    }


    @Override
    public int compareTo(Object o) {
        if(o instanceof Employee){
            return this.getPriorityScore() - ((Employee) o).getPriorityScore();
        }
        return 0;
    }

    public int getPriorityScore(){
        int priorityScore  = 0;

        //TODO implement priority score calculation

        return priorityScore;
    }

    public boolean isAvailable(int day, int hour) {
        for(Constraint c : constraints){
            if(c.getDay() == day){
                if(c.getStartHour() <= hour && c.getEndHour() >= hour){
                    return false;
                }
            }
        }
        return true;
    }

    public void addConstraint(int day, int startHour, int endHour) {
        Constraint c = new Constraint(day, startHour, endHour);
        constraints.add(c);
    }

    public void addShift(int day, int startHour, int endHour) {
        Shift s = new Shift(day, startHour, endHour);
        shifts.add(s);

    }

    //clears unneeded constraints
    private void clearConstraints(int day, int hour) {
        constraints.clear();
    }

    //load and save to firebase realtime

    public void loadEmp(){
        DatabaseReference empRef = FirebaseDatabase.getInstance().getReference("employees").child(String.valueOf(uid));

        empRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming constraints, specialProperties, and shifts are stored in the database
                    constraints = (ArrayList<Constraint>) dataSnapshot.child("constraints").getValue();
                    specialProperties = (ArrayList<Integer>) dataSnapshot.child("specialProperties").getValue();
                    shifts = (ArrayList<Shift>) dataSnapshot.child("shifts").getValue();
                    Log.d("Employee", "Employee data loaded successfully.");
                } else {
                    Log.d("Employee", "No data found for this employee.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Employee", "Failed to load employee data.", error.toException());
            }
        });
    }

    public void save(){

    }

}
