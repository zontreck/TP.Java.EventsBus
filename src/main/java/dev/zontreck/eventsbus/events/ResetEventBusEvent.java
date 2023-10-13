package dev.zontreck.eventsbus.events;

import dev.zontreck.eventsbus.Cancellable;
import dev.zontreck.eventsbus.Event;

/**
 * Posted when the event bus is about to be reset.
 * <p>
 * This event can be cancelled to prevent the reset.
 * <p>
 * The default behavior is: Allow Reset.
 * <p>
 * Recommended that handlers be implemented to re-register instances, and classes when a reset occurs.
 */
@Cancellable
public class ResetEventBusEvent extends Event {
}
