package com.example.shifty.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.CompletableFuture;

public class ScheduleRepository {

    private static ScheduleRepository instance;
    private static final String SERVER_URL = "https://shifty-1c786-default-rtdb.europe-west1.firebasedatabase.app";
    private static final String SYSTEM_NEED_PATH = "systemNeeds";
    private static final int HOUR_PER_DAY = 22;
    private static final int DAYS_IN_WEEK = 7;


    public ScheduleRepository() {
        // Private constructor to prevent instantiation
    }

    public static ScheduleRepository getInstance() {
        if (instance == null) {
            instance = new ScheduleRepository();
        }
        return instance;
    }


    public CompletableFuture<int[][]> fetchSystemNeeds(){
        CompletableFuture<int[][]> data = new CompletableFuture<>();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance(SERVER_URL).getReference(SYSTEM_NEED_PATH);

        databaseRef.get().addOnCompleteListener(task -> {
            int[][] systemNeeds = new int[DAYS_IN_WEEK][HOUR_PER_DAY];
            if (!task.getResult().exists()) {
                for(int day = 0; day < DAYS_IN_WEEK; day++){
                    for (int hour = 0; hour < HOUR_PER_DAY; hour++){
                        systemNeeds[day][hour] = 0;
                    }
                }
            } else if(task.isSuccessful()){
                DataSnapshot snapshot = task.getResult();
                for (int day = 0; day < DAYS_IN_WEEK; day++) {
                    DataSnapshot daySnapshot = snapshot.child(String.valueOf(day));
                    for (int hour = 0; hour < HOUR_PER_DAY; hour++) {
                        Integer value = daySnapshot.child(String.valueOf(hour)).getValue(Integer.class);
                        systemNeeds[day][hour] = (value != null) ? value : 0; // Default to 0 if null
                    }
                }
            }
            data.complete(systemNeeds);
        });

    return data;
    }


    public void saveSystemNeedsOnDayAndHour(int[][] systemNeeds) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance(SERVER_URL).getReference(SYSTEM_NEED_PATH);
        for(int day = 0; day< DAYS_IN_WEEK; day++){
            for(int hour = 0; hour < HOUR_PER_DAY; hour++){
                databaseRef.child(String.valueOf(day)).child(String.valueOf(hour)).setValue(systemNeeds[day][hour]);
            }
        }
    }



}
