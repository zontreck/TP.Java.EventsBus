package dev.zontreck.eventsbus.annotations;

import dev.zontreck.eventsbus.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)

public @interface Subscribe {
    /**
     * Marks that the subscribed method will not receive the signal if the event was cancelled with {@link Event#setCancelled(boolean)}
     */
    boolean allowCancelled();
}
