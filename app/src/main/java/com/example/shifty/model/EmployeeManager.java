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

public class EmployeeManager {

    private static final String SERVER_URL = "https://shifty-1c786-default-rtdb.europe-west1.firebasedatabase.app";
    private static EmployeeManager instance;
    private static final HashMap<String, Employee> employeeMap = new HashMap<>();
    private boolean initialized = false;

    private MutableLiveData<Boolean> needRefresh = new MutableLiveData<>(); //just when something is added

    public static EmployeeManager getInstance() {
        if(instance == null) instance = new EmployeeManager();
        return instance;
    }


    private EmployeeManager() {
        initializeEmployee();
        monitorUIDs();
    }


    private void initializeEmployee(){
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
                // Print or use the list of UIDs
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error fetching UIDs: " + databaseError.getMessage());
            }
        });
        return toReturn;
    }

    private void monitorUIDs() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference employeesRef = database.getReference("employees");

        employeesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // This method is triggered when a new child is added
                String newUID = dataSnapshot.getKey();
                Employee newEmployee = new Employee(newUID);
                newEmployee.loadEmp();
                employeeMap.put(newUID, newEmployee);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Triggered when a child node is updated
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                employeeMap.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Triggered when a child node is moved
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error monitoring UIDs: " + databaseError.getMessage());
            }
        });
    }

    public List<Employee> getEmployees(){
        List<Employee> employees = new ArrayList<>(employeeMap.values());
        return employees;
    }

    public String getEmployeeName(String uid){
        Employee employee = employeeMap.get(uid);
        if(employee != null) return employee.getName();
        Log.d("EmployeeManager", "Employee not found");
        return null;
    }

    public Employee getEmployee(String uid) {
        Employee employee = employeeMap.get(uid);
        if(employee != null) return employee;
        Log.d("EmployeeManager", "Employee not found");
        return null;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}







