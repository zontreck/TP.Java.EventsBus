package dev.zontreck.eventsbus;

/**
 * Event priority level.
 * <p>
 * The higher the priority, the sooner it gets executed. High is executed after
 * Highest.
 */
public enum PriorityLevel {
    HIGHEST,
    HIGH,
    MEDIUM,
    LOW,
    LOWEST
}
