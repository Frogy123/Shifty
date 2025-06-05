package com.example.shifty.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Singleton class to manage employee data within the application. Handles
 * initialization, retrieval, saving, and real-time monitoring of employees
 * stored in a Firebase Realtime Database.
 *
 * <p>See also: {@link Employee}
 *
 * @author Eitan Navon
 */
public class EmployeeManager {

    /**
     * Firebase Realtime Database URL.
     */
    private static final String SERVER_URL = "https://shifty-1c786-default-rtdb.europe-west1.firebasedatabase.app";

    /**
     * The single instance of {@link EmployeeManager}.
     */
    private static EmployeeManager instance;

    /**
     * In-memory cache for employee data, keyed by UID.
     */
    private static final HashMap<String, Employee> employeeMap = new HashMap<>();

    /**
     * Indicates if all employees have been loaded and initialized.
     */
    private boolean initialized = false;

    /**
     * LiveData indicating the need for UI refresh on employee changes.
     */
    private MutableLiveData<Boolean> needRefresh = new MutableLiveData<>();

    /**
     * Gets the singleton instance of {@link EmployeeManager}.
     *
     * @return the single {@link EmployeeManager} instance
     */
    public static EmployeeManager getInstance() {
        if (instance == null) instance = new EmployeeManager();
        return instance;
    }

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes employee data and starts monitoring Firebase for changes.
     */
    private EmployeeManager() {
        initializeEmployee();
        monitorUIDs();
    }

    /**
     * Initializes employee data by loading all employee UIDs from Firebase,
     * instantiating {@link Employee} objects, and loading their data.
     * The process is asynchronous.
     */
    private void initializeEmployee() {
        CompletableFuture<List<String>> future = getAllEmployeeUIDs();
        future.thenAccept(uids -> {
            for (String uid : uids) {
                Employee employee = new Employee(uid);
                employee.loadEmp();
                employeeMap.put(uid, employee);
                initialized = true;
            }
        }).exceptionally(e -> {
            System.err.println("Error fetching UIDs: " + e.getMessage());
            return null;
        });
    }

    /**
     * Retrieves all employee UIDs from Firebase asynchronously.
     *
     * @return a {@link CompletableFuture} containing a list of employee UIDs
     */
    private CompletableFuture<List<String>> getAllEmployeeUIDs() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(SERVER_URL);
        DatabaseReference employeesRef = database.getReference("employees");
        CompletableFuture<List<String>> toReturn = new CompletableFuture<>();
        employeesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> uids = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String uid = childSnapshot.getKey();
                    if (uid != null) {
                        uids.add(uid);
                    }
                }
                toReturn.complete(uids);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error fetching UIDs: " + databaseError.getMessage());
                // Optionally, completeExceptionally can be used for error propagation:
                // toReturn.completeExceptionally(databaseError.toException());
            }
        });
        return toReturn;
    }

    /**
     * Monitors Firebase for any changes (additions/removals/updates) to employees,
     * and keeps the internal map updated accordingly.
     */
    private void monitorUIDs() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference employeesRef = database.getReference("employees");

        employeesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String newUID = dataSnapshot.getKey();
                Employee newEmployee = new Employee(newUID);
                newEmployee.loadEmp();
                employeeMap.put(newUID, newEmployee);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Optionally handle employee updates here
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                employeeMap.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Optionally handle moves here
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error monitoring UIDs: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Gets a list of all loaded employees.
     *
     * @return a {@link List} of {@link Employee} objects.
     */
    public List<Employee> getEmployees() {
        return new ArrayList<>(employeeMap.values());
    }

    /**
     * Gets the name of the employee with the specified UID.
     *
     * @param uid the unique ID of the employee
     * @return the name of the employee, or {@code null} if not found
     */
    public String getEmployeeName(String uid) {
        Employee employee = employeeMap.get(uid);
        if (employee != null) return employee.getName();
        Log.d("EmployeeManager", "Employee not found");
        return null;
    }

    /**
     * Gets the {@link Employee} object for the specified UID.
     *
     * @param uid the unique ID of the employee
     * @return the {@link Employee} object, or {@code null} if not found
     */
    public Employee getEmployee(String uid) {
        Employee employee = employeeMap.get(uid);
        if (employee != null) return employee;
        Log.d("EmployeeManager", "Employee not found");
        return null;
    }

    /**
     * Checks if the employee data has been initialized.
     *
     * @return {@code true} if employees are initialized, {@code false} otherwise
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Sets the initialization state of the employee data.
     *
     * @param initialized {@code true} if initialized, {@code false} otherwise
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Saves all employees to the database by calling {@link Employee#save()} on each employee.
     *
     * @see Employee#save()
     */
    public void saveAllEmployees() {
        for (Employee employee : employeeMap.values()) {
            employee.save();
        }
    }
}
