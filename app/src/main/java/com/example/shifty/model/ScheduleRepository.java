package com.example.shifty.model;

import com.example.shifty.model.SchedulingAlgorithm.Schedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.CompletableFuture;

/**
 * Repository class responsible for fetching and saving scheduling data, such as system needs, to and from Firebase.
 * <p>
 * Implements the Singleton pattern to provide a single access point for schedule-related database operations.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * ScheduleRepository repo = ScheduleRepository.getInstance();
 * CompletableFuture<int[][]> needsFuture = repo.fetchSystemNeeds();
 * needsFuture.thenAccept(systemNeeds -> {
 *     // process needs
 * });
 * }
 * </pre>
 *
 * @author Eitan Navon
 * @see FirebaseDatabase
 * @see DatabaseReference
 * @see CompletableFuture
 * @see Schedule
 */
public class ScheduleRepository {

    private static ScheduleRepository instance;
    private static final String SERVER_URL = "https://shifty-1c786-default-rtdb.europe-west1.firebasedatabase.app";
    private static final String SYSTEM_NEED_PATH = "systemNeeds";
    private static final int HOUR_PER_DAY = 22;
    private static final int DAYS_IN_WEEK = 7;

    /**
     * Private constructor to prevent direct instantiation. Use {@link #getInstance()}.
     */
    public ScheduleRepository() {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the singleton instance of {@code ScheduleRepository}.
     *
     * @return the single instance of {@code ScheduleRepository}
     */
    public static ScheduleRepository getInstance() {
        if (instance == null) {
            instance = new ScheduleRepository();
        }
        return instance;
    }

    /**
     * Fetches the system needs (number of employees required) for each hour and day from Firebase.
     * <p>
     * The data is represented as a two-dimensional array where {@code systemNeeds[day][hour]} indicates
     * the number of employees required for that hour on the given day.
     * </p>
     *
     * @return a {@link CompletableFuture} that resolves to a 2D int array representing system needs.
     *         If the data does not exist in Firebase, all values will be set to zero.
     * @throws RuntimeException if there is a Firebase/database connectivity error.
     * @see CompletableFuture
     * @see DatabaseReference#get()
     * @link Schedule
     */
    public CompletableFuture<int[][]> fetchSystemNeeds() {
        CompletableFuture<int[][]> data = new CompletableFuture<>();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance(SERVER_URL).getReference(SYSTEM_NEED_PATH);

        databaseRef.get().addOnCompleteListener(task -> {
            int[][] systemNeeds = new int[DAYS_IN_WEEK][HOUR_PER_DAY];
            if (!task.getResult().exists()) {
                for (int day = 0; day < DAYS_IN_WEEK; day++) {
                    for (int hour = 0; hour < HOUR_PER_DAY; hour++) {
                        systemNeeds[day][hour] = 0;
                    }
                }
            } else if (task.isSuccessful()) {
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

    /**
     * Saves the system needs (number of employees required per hour and day) to Firebase.
     * <p>
     * Each cell in the array is saved at the path {@code /systemNeeds/{day}/{hour}}.
     * </p>
     *
     * @param systemNeeds a 2D int array where {@code systemNeeds[day][hour]} represents the number
     *                    of employees needed for the specified day and hour.
     * @throws NullPointerException if {@code systemNeeds} is {@code null}.
     * @see DatabaseReference#setValue(Object)
     * @link Schedule
     */
    public void saveSystemNeedsOnDayAndHour(int[][] systemNeeds) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance(SERVER_URL).getReference(SYSTEM_NEED_PATH);
        for (int day = 0; day < DAYS_IN_WEEK; day++) {
            for (int hour = 0; hour < HOUR_PER_DAY; hour++) {
                databaseRef.child(String.valueOf(day)).child(String.valueOf(hour)).setValue(systemNeeds[day][hour]);
            }
        }
    }

    /**
     * Saves the provided schedule to the database.
     * <p>
     * This method is a placeholder and should be implemented based on the structure of {@link Schedule}.
     * </p>
     *
     * @param s the schedule to save.
     * @throws UnsupportedOperationException if not implemented.
     * @see Schedule
     * @link DatabaseReference
     */
    public void saveSchedule(Schedule s) {
        // To be implemented: Save the provided schedule to the database
    }
}
