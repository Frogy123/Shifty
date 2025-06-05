package com.example.shifty.model;

/**
 * Enumeration representing the different roles available in the system.
 * <p>
 * A {@code Role} defines the permissions or responsibilities a user may have
 * within the application. The available roles are:
 * <ul>
 *     <li>{@link #ADMIN}: The administrator role, with highest permissions.</li>
 *     <li>{@link #EMPLOYEE}: Standard employee role, with basic permissions.</li>
 *     <li>{@link #SHIFTMANAGER}: Shift manager role, responsible for managing shifts.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * Role userRole = Role.ADMIN;
 * if (userRole == Role.EMPLOYEE) {
 *     // perform employee-specific actions
 * }
 * }
 * </pre>
 *
 * @author Eitan Navon
 * @see com.example.shifty.model.Employee
 * @see Enum
 */
public enum Role {
    /**
     * The administrator role, with permissions to manage users and system settings.
     */
    ADMIN,
    /**
     * The standard employee role, with permissions to view and manage their own shifts.
     */
    EMPLOYEE,

}
