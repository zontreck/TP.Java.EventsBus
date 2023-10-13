package dev.zontreck.eventsbus.events;

import dev.zontreck.eventsbus.Event;

/**
 * This event is sent out when the Event Bus is ready.
 * <p>
 * This is also dispatched when a reset occurs, prior to clearing the lists.
 */
public class EventBusReadyEvent extends Event {
}
