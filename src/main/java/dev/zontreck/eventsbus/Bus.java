package dev.zontreck.eventsbus;

import org.checkerframework.common.reflection.qual.GetClass;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bus {
    /**
     * This bus disallows registering of instances, and will only invoke static events.
     */
    private static Bus Static = new Bus("Main Event Bus", false);
    /**
     * This bus requires registering of instances. Events fired here do not call static events.
     */
    private static Bus Directed = new Bus("Direct Event Bus", true);
    public final String BusName;
    public final boolean UsesInstances;

    public Bus(String name, boolean useInstances) {
        BusName = name;
        UsesInstances = useInstances;
    }

    /**
     * Posts an event to the event message bus.
     *
     * @param event The event to be posted to all classes
     * @return True if the event was cancelled.
     */
    public boolean Post(Event event) {

        return false;
    }

    public Map<Class<?>, EventContainer> static_events = new HashMap<Class<?>, EventContainer>();
    public Map<Class<?>, EventContainer> instanced_events = new HashMap<Class<?>, EventContainer>();

    public static <T> void Register(Class<T> clazz, T instance)
    {
        EventContainer container = new EventContainer();
        if(instance == null)
        {
            // Will not register the instanced handlers
        }
        else {
            // Will register instanced handlers.
        }

        // Register static handlers.
        Arrays.stream(clazz.getMethods())
                .filter(x->x.isAnnotationPresent(Subscribe.class))

    }
}
