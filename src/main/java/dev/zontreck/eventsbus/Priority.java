package dev.zontreck.eventsbus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface Priority {
    public PriorityLevel Level = PriorityLevel.LOW;
}
