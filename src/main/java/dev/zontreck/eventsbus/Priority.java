package dev.zontreck.eventsbus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface Priority {
    PriorityLevel Level();
}
