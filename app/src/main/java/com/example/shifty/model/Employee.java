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

/**
 * Represents an employee in the Shifty system, including their constraints, assigned shifts, and
 * synchronization with Firebase Realtime Database.
 * <p>
 * Employees can have availability constraints (forbidden times), assigned shifts,
 * and are tracked by a unique UID. Employee data is loaded, saved, and synchronized
 * with the remote database. Implements {@link Comparable} for custom priority ordering.
 * <p>
 * This class supports reactive data change notification for UI through {@link MutableLiveData}.
 *
 * <h3>Main Responsibilities:</h3>
 * <ul>
 *     <li>Maintain the employee's identity and name.</li>
 *     <li>Store and manage constraints (e.g., unavailable times).</li>
 *     <li>Store and manage assigned {@link Shift}s.</li>
 *     <li>Handle saving and loading data from Firebase Realtime Database.</li>
 *     <li>Notify UI of changes using LiveData for MVVM architecture.</li>
 * </ul>
 *
 * <h3>Synchronization:</h3>
 * <ul>
 *     <li>Reads and writes are performed via Firebase DatabaseReference.</li>
 *     <li>Uses listeners to keep constraints and shifts in sync with the cloud.</li>
 *     <li>LiveData fields (<code>needRefresh</code>, <code>isDeleted</code>) enable real-time UI update when the employee's data changes.</li>
 * </ul>
 *
 * @author Eitan Navon
 */
public class Employee implements Comparable {

    /** The employee's display name. */
    String name;

    /** List of time constraints (unavailable times) for the employee. */
    ArrayList<Constraint> constraints;

    /** List of assigned shifts for the employee. */
    ArrayList<Shift> shifts;

    /** Maximum number of constraints allowed for each employee. */
    private static final int MAX_CONSTRAINTS = 2;

    /** Name of the employees collection in Firebase. */
    private static final String COLLECTION_NAME = "employees";
    /** Name of the constraints collection (under an employee node). */
    private static final String CONSTRAINTS_COLLECTION_NAME = "constraints";
    /** Name of the shifts collection (under an employee node). */
    private static final String SHIFTS_COLLECTION_NAME = "shifts";
    /** Firebase Realtime Database URL for the app. */
    private static final String SERVER_URL = "https://shifty-1c786-default-rtdb.europe-west1.firebasedatabase.app";

    /** LiveData indicating that this employee's data needs to be refreshed in the UI. */
    MutableLiveData<Boolean> needRefresh = new MutableLiveData<>(false);

    /** LiveData indicating that this employee is deleted (used for UI updates). */
    MutableLiveData<Boolean> isDeleted = new MutableLiveData<>(false);

    /** Unique identifier for this employee (typically Firebase UID). */
    String uid;

    /**
     * Constructs an employee with a unique UID.
     * Initializes empty lists for constraints and shifts.
     * @param uid The unique identifier for the employee.
     */
    public Employee(String uid) {
        this.uid = uid;
        constraints = new ArrayList<>();
        shifts = new ArrayList<>();
    }

    /**
     * Constructs an employee with a unique UID and a display name.
     * Initializes empty lists for constraints and shifts.
     * @param uid The unique identifier for the employee.
     * @param name The display name of the employee.
     */
    public Employee(String uid, String name) {
        this.uid = uid;
        this.name = name;
        constraints = new ArrayList<>();
        shifts = new ArrayList<>();
    }

    /**
     * Compares this employee to another by priority score.
     * Lower score means higher priority (suitable for priority queues).
     *
     * @param o Another {@link Employee} object.
     * @return The difference in priority score, or 0 if not an employee.
     */
    @Override
    public int compareTo(Object o) {
        if (o instanceof Employee) {
            return this.getPriorityScore() - ((Employee) o).getPriorityScore();
        }
        return 0;
    }

    /**
     * Computes and returns the employee's current priority score,
     * which determines the order in which employees are considered for scheduling.
     * (To be implemented with custom logic, e.g., based on constraints or availability).
     *
     * @return The employee's priority score (currently always 0).
     */
    public int getPriorityScore() {
        int priorityScore = 0;
        //TODO implement priority score calculation
        return priorityScore;
    }

    /**
     * Checks if the employee is available at the specified day and hour.
     *
     * @param day The day of the week (0=Sunday, 6=Saturday).
     * @param hour The hour of the day (0-23).
     * @return {@code true} if available, {@code false} if constrained/unavailable.
     */
    public boolean isAvailable(int day, int hour) {
        for (Constraint c : constraints) {
            if (c.getDay() == day) {
                if (c.getStartHour() <= hour && c.getEndHour() >= hour) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Adds a new availability constraint for the employee for a specific day and hour range.
     *
     * @param day The day of the week (0=Sunday, 6=Saturday).
     * @param startHour The starting hour (0-23).
     * @param endHour The ending hour (0-23).
     */
    public void addConstraint(int day, int startHour, int endHour) {
        LocalDate constraintDate = TimeUtil.sundayForDate(LocalDate.now().plusWeeks(1));
        constraintDate.plusDays(day);
        Constraint c = new Constraint(day, startHour, endHour, constraintDate.toEpochDay());
        constraints.add(c);
    }

    /**
     * Adds a new shift to this employee for a specified day and hour range.
     * If the shift is consecutive to an existing shift, merges them for a continuous block.
     *
     * @param day The day of the week (0=Sunday, 6=Saturday).
     * @param startHour The starting hour (0-23).
     * @param endHour The ending hour (0-23).
     */
    public void addShift(int day, int startHour, int endHour) {
        Shift newShift = new Shift(day, startHour, endHour, TimeUtil.nextWeekDay(day).toEpochDay());
        boolean isDisjointShift = true;
        for (Shift s : shifts) {
            if (s.isConsectuive(newShift)) {
                s.setEndHour(newShift.getEndHour());
                isDisjointShift = false;
            }
            if (newShift.isConsectuive(s)) {
                s.setStartHour(newShift.getStartHour());
                isDisjointShift = false;
            }
        }
        if (isDisjointShift) {
            shifts.add(newShift);
        }
    }

    /**
     * Checks if the employee has a shift assigned for a given date.
     *
     * @param date The date to check.
     * @return {@code true} if the employee has a shift on this date; {@code false} otherwise.
     */
    public boolean haveShift(LocalDate date) {
        for (Shift s : shifts) {
            if (s.getDate() == date.toEpochDay()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes all constraints from the employee.
     * Used for maintenance or cleaning up after scheduling.
     *
     * @param day  The day for which constraints are being cleared (unused in this version).
     * @param hour The hour for which constraints are being cleared (unused in this version).
     */
    private void clearConstraints(int day, int hour) {
        constraints.clear();
    }

    // ==================== Firebase Database Synchronization ====================

    /**
     * Loads this employee's data from Firebase Realtime Database, including
     * name, constraints, and shifts. Updates observers via LiveData when loaded.
     */
    public void loadEmp() {
        FirebaseDatabase fb = FirebaseDatabase.getInstance(SERVER_URL);
        DatabaseReference empRef = fb.getReference(COLLECTION_NAME).child(String.valueOf(uid));

        empRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming constraints and shifts are stored in the database
                    name = (String) dataSnapshot.child("name").getValue();
                    loadConstraint(dataSnapshot);
                    loadShifts(dataSnapshot);
                    // Notify observers that data has been loaded
                    needRefresh.postValue(true);
                    Log.d("Employee", "Employee data loaded successfully.");
                } else {
                    name = CurrentUserManager.getInstance().getUser().getUsername();
                    save();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Employee", "Failed to load employee data.", error.toException());
            }
        });
    }

    /**
     * Loads the constraints list from the provided DataSnapshot.
     *
     * @param ds DataSnapshot containing constraint data.
     */
    private void loadConstraint(DataSnapshot ds) {
        try {
            DataSnapshot constraintDataSnapshot = ds.child(CONSTRAINTS_COLLECTION_NAME);
            this.constraints = new ArrayList<>();
            for (DataSnapshot constraintSnapshot : constraintDataSnapshot.getChildren()) {
                Constraint constraint = constraintSnapshot.getValue(Constraint.class);
                if (constraint != null) {
                    constraints.add(constraint);
                }
            }
        } catch (Exception e) {
            Log.e("Employee", "Failed to load constraints.", e);
        }
    }

    /**
     * Loads the shifts list from the provided DataSnapshot.
     *
     * @param ds DataSnapshot containing shift data.
     */
    private void loadShifts(DataSnapshot ds) {
        try {
            DataSnapshot shiftsDataSnapshot = ds.child(SHIFTS_COLLECTION_NAME);
            this.shifts = new ArrayList<>();
            for (DataSnapshot shiftSnapshot : shiftsDataSnapshot.getChildren()) {
                Shift shift = shiftSnapshot.getValue(Shift.class);
                if (shift != null) {
                    shifts.add(shift);
                }
            }
        } catch (Exception e) {
            Log.e("Employee", "Failed to load shifts.", e);
        }
    }

    // ==================== LiveData Getters ====================

    /**
     * Returns the LiveData object for UI components to observe changes to this employee's data.
     *
     * @return MutableLiveData that signals when this employee's data should be refreshed.
     */
    public MutableLiveData<Boolean> getRefresh() {
        return needRefresh;
    }

    /**
     * Returns the LiveData object for UI components to observe if this employee is deleted.
     *
     * @return MutableLiveData indicating deletion status.
     */
    public MutableLiveData<Boolean> getIsDeleted() {
        return needRefresh; // Note: This appears to be a bug (should return isDeleted)
    }

    // ==================== Database Save/Update ====================

    /**
     * Saves the current employee's name, constraints, and shifts to Firebase Realtime Database.
     */
    public void save() {
        FirebaseDatabase fb = FirebaseDatabase.getInstance(SERVER_URL);
        DatabaseReference empRef = fb.getReference(COLLECTION_NAME).child(String.valueOf(uid));
        empRef.child("name").setValue(name);
        empRef.child(CONSTRAINTS_COLLECTION_NAME).setValue(constraints);
        // empRef.child(SPECIAL_PROPERTIES_COLLECTION_NAME).setValue(specialProperties);
        empRef.child(SHIFTS_COLLECTION_NAME).setValue(shifts);
        Log.d("Employee", "Employee data saved successfully.");
    }

    // ==================== Constraint/Shift Utility ====================

    /**
     * Deletes the constraint at the given index from the constraints list and saves changes to Firebase.
     *
     * @param index The index of the constraint to remove.
     * @throws IndexOutOfBoundsException If the index is invalid.
     */
    public void deleteConstraint(int index) {
        if (index < 0 || index >= constraints.size()) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        constraints.remove(index);
        save();
    }

    /**
     * Returns the number of constraints currently set for this employee.
     *
     * @return The size of the constraints list.
     */
    public int numberOfConstraints() {
        return constraints.size();
    }

    /**
     * Returns the list of all constraints for this employee.
     *
     * @return A list of {@link Constraint} objects.
     */
    public List<Constraint> getConstraints() {
        return constraints;
    }

    /**
     * Returns the UID of this employee.
     *
     * @return The unique identifier string.
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the name of this employee.
     *
     * @param name The new name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name of this employee.
     *
     * @return The name string.
     */
    public String getName() {
        return name;
    }
}
