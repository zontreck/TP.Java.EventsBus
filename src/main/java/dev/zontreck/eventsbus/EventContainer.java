package dev.zontreck.eventsbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventContainer {
    public Class<?> clazz;
    public Object instance;

    /**
     * The method that gets invoked, either statically, or via the instance.
     */
    public Method method;

    /**
     * The event that gets called!
     *
     * @see Event
     */
    public Event event;

    /**
     * Indicates whether an event gets removed from the register after being invoked once.
     */
    public boolean IsSingleshot;

    /**
     * The current method's priority level
     */
    public PriorityLevel Level;

    /**
     * Invokes the event
     *
     * @param EventArg The event instance to pass to the subscribed function
     * @param level    Current priority level on the call loop. Will refuse to invoke if the priority level mismatches.
     * @return True if the event was single shot and should be deregistered
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public boolean invoke(Event EventArg, PriorityLevel level) throws InvocationTargetException, IllegalAccessException {
        if (Level != level) return false;
        
        if (instance == null) {
            method.invoke(null, EventArg);
        } else {
            method.invoke(instance, EventArg);
        }

        return IsSingleshot;
    }
}
