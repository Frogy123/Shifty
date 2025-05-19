package com.example.shifty.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.shifty.model.SchedulingAlgorithm.Constraint;
import com.example.shifty.model.SchedulingAlgorithm.Shift;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.shifty.model.SchedulingAlgorithm.TimeUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Employee implements Comparable{




    String name;
    ArrayList<Constraint> constraints;
    ArrayList<Integer> specialProperties;
    ArrayList<Shift> shifts;


    private static final int MAX_CONSTRAINTS = 2;
    private static final String COLLECTION_NAME = "employees";
    private static final String CONSTRAINTS_COLLECTION_NAME = "constraints";
    private static final String SPECIAL_PROPERTIES_COLLECTION_NAME = "specialProperties";
    private static final String SHIFTS_COLLECTION_NAME = "shifts";

    private static final String SERVER_URL = "https://shifty-1c786-default-rtdb.europe-west1.firebasedatabase.app";

    MutableLiveData<Boolean> needRefresh = new MutableLiveData<>(false);
    MutableLiveData<Boolean> isDeleted = new MutableLiveData<>(false);

    String uid;

    public Employee (String uid){
        this.uid = uid;
        constraints = new ArrayList<>();
        specialProperties = new ArrayList<>();
        shifts = new ArrayList<>();


    }

    public Employee (String uid, String name){
        this.uid = uid;
        this.name = name;
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
        LocalDate constraintDate = TimeUtil.sundayForDate(LocalDate.now().plusWeeks(1));
        constraintDate.plusDays(day);

        Constraint c = new Constraint(day, startHour, endHour, constraintDate.toEpochDay());
        constraints.add(c);
    }

    public void addShift(int day, int startHour, int endHour) {
        Shift s = new Shift(day, startHour, endHour, 0);
        shifts.add(s);

    }

    public boolean haveShift(LocalDate date){
        for(Shift s : shifts){
            if(s.getDate() == date.toEpochDay()){
                return true;
            }
        }
        return false;
    }

    //clears unneeded constraints
    private void clearConstraints(int day, int hour) {
        constraints.clear();
    }

    //load and save to firebase realtime

    public void loadEmp(){
        FirebaseDatabase fb = FirebaseDatabase.getInstance(SERVER_URL);
        DatabaseReference empRef = fb.getReference(COLLECTION_NAME).child(String.valueOf(uid));


        empRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming constraints, specialProperties, and shifts are stored in the database
                    name = (String) dataSnapshot.child("name").getValue();
                    loadConstraint(dataSnapshot);
                    loadShifts(dataSnapshot);
                    // Update the MutableLiveData to notify observers
                    needRefresh.postValue(true);
                    Log.d("Employee", "Employee data loaded successfully.");
                } else {
                    isDeleted.postValue(true);
                    Log.d("Employee", "No data found for this employee.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Employee", "Failed to load employee data.", error.toException());
            }
        });
    }

    private void loadConstraint(DataSnapshot ds){
        try{
            DataSnapshot constraintDataSnapshot = ds.child(CONSTRAINTS_COLLECTION_NAME);

            this.constraints = new ArrayList<>();
            for (DataSnapshot constraintSnapshot : constraintDataSnapshot.getChildren()) {
                Constraint constraint = constraintSnapshot.getValue(Constraint.class);
                if (constraint != null) {
                    constraints.add(constraint);
                }
            }
        }catch (Exception e){
            Log.e("Employee", "Failed to load constraints.", e);
        }

    }

    private void loadShifts(DataSnapshot ds){
        try{
            DataSnapshot shiftsDataSnapshot = ds.child(SHIFTS_COLLECTION_NAME);

            this.shifts = new ArrayList<>();
            for (DataSnapshot shiftSnapshot : shiftsDataSnapshot.getChildren()) {
                Shift shift = shiftSnapshot.getValue(Shift.class);
                if (shift != null) {
                    shifts.add(shift);
                }
            }
        }catch (Exception e){
            Log.e("Employee", "Failed to load shifts.", e);
        }

    }

    public MutableLiveData<Boolean> getRefresh(){
        return needRefresh;
    }
    public MutableLiveData<Boolean> getIsDeleted(){
        return needRefresh;
    }

    public void save(){
        FirebaseDatabase fb = FirebaseDatabase.getInstance(SERVER_URL);
        DatabaseReference empRef = fb.getReference(COLLECTION_NAME).child(String.valueOf(uid));

        empRef.child("name").setValue(name);

        empRef.child(CONSTRAINTS_COLLECTION_NAME).setValue(constraints);

        //empRef.child(SPECIAL_PROPERTIES_COLLECTION_NAME).setValue(specialProperties);
        empRef.child(SHIFTS_COLLECTION_NAME).setValue(shifts);

        Log.d("Employee", "Employee data saved successfully.");

    }

    public void deleteConstraint(int index){
        if(index < 0 || index >= constraints.size()){
            throw new IndexOutOfBoundsException("Invalid index");
        }
        constraints.remove(index);
        save();
    }

    public int numberOfConstraints() {
        return constraints.size();
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public String getUid(){
        return uid;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
