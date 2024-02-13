package dev.zontreck.eventsbus;

import dev.zontreck.eventsbus.annotations.Priority;
import dev.zontreck.eventsbus.annotations.SingleshotEvent;
import dev.zontreck.eventsbus.annotations.Subscribe;
import dev.zontreck.eventsbus.events.EventBusReadyEvent;
import dev.zontreck.eventsbus.events.ResetEventBusEvent;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * To be removed
 * <br/>
 * Use {} instead
 */
@Deprecated()
public class Bus {
    /**
     * The main event bus!
     */
    private static Bus Main;

    static {
        if(Main == null)
        {
            Main = new Bus("Main Event Bus", false);
        }
    }

    public static boolean debug = false;
    public final String BusName;
    public final boolean UsesInstances;

    public Bus(String name, boolean useInstances) {
        BusName = name;
        UsesInstances = useInstances;

        Post(new EventBusReadyEvent());
    }

    public Map<Class<?>, List<EventContainer>> static_events = new HashMap<Class<?>, List<EventContainer>>();
    public Map<Class<?>, List<EventContainer>> instanced_events = new HashMap<Class<?>, List<EventContainer>>();

    public static <T> void Register(Class<T> clazz, T instance) {

        List<Method> nonStaticMethods = Arrays.stream(clazz.getMethods())
                .filter(x -> x.isAnnotationPresent(Subscribe.class))
                .filter(x -> (x.getModifiers() & Modifier.STATIC) != Modifier.STATIC)
                .collect(Collectors.toList());

        List<Method> staticMethods = Arrays.stream(clazz.getMethods())
                .filter(x -> x.isAnnotationPresent(Subscribe.class))
                .filter(x -> (x.getModifiers() & Modifier.STATIC) == Modifier.STATIC)
                .collect(Collectors.toList());

        // Register the non-static methods if applicable
        if (instance != null) {
            for (Method m :
                    nonStaticMethods) {
                EventContainer container = new EventContainer();
                container.instance = instance;
                container.method = m;
                container.clazz = clazz;
                if (m.isAnnotationPresent(Priority.class))
                    container.Level = m.getAnnotation(Priority.class).Level();
                else container.Level = PriorityLevel.LOWEST;

                Class<?> clz = m.getParameters()[0].getType();

                container.IsSingleshot = m.isAnnotationPresent(SingleshotEvent.class);

                if (Main.instanced_events.containsKey(clz))
                    Main.instanced_events.get(clz).add(container);
                else {
                    Main.instanced_events.put(clz, new ArrayList<>());
                    Main.instanced_events.get(clz).add(container);
                }
            }
        }

        for (Method m : staticMethods) {
            EventContainer container = new EventContainer();
            container.instance = null;
            container.method = m;
            container.clazz = clazz;
            if (m.isAnnotationPresent((Priority.class)))
                container.Level = m.getAnnotation(Priority.class).Level();
            else container.Level = PriorityLevel.LOWEST;

            Class<?> clz = m.getParameters()[0].getType();
            container.IsSingleshot = m.isAnnotationPresent(SingleshotEvent.class);

            if (Main.static_events.containsKey(clz))
                Main.static_events.get(clz).add(container);
            else {
                Main.static_events.put(clz, new ArrayList<>());
                Main.static_events.get(clz).add(container);
            }
        }
    }

    /**
     * Posts an event to the bus.
     *
     * @param event The event you wish to post
     * @return True if the event was cancelled.
     */
    public static boolean Post(Event event) {
        for (PriorityLevel level :
                PriorityLevel.values()) {
            // Call each priority level in order of declaration
            // Static first, then instanced
            // At the end, this method will return the cancellation result.
            try {

                if (Main.static_events.containsKey(event.getClass())) {
                    EventContainer[] tempArray = (EventContainer[]) Main.static_events.get(event.getClass()).toArray();

                    for (EventContainer container : tempArray) {
                        if (container.invoke(event, level)) {
                            Main.static_events.get(event.getClass()).remove(container);
                        }
                    }
                }

                if (Main.instanced_events.containsKey(event.getClass())) {
                    EventContainer[] tempArray = (EventContainer[]) Main.instanced_events.get(event.getClass()).toArray();

                    for (EventContainer container : tempArray) {
                        if (container.invoke(event, level)) {
                            Main.instanced_events.get(event.getClass()).remove(container);
                        }
                    }
                }
            } catch (Exception e) {

            }
        }

        return event.isCancelled();
    }

    /**
     * Attempts to reset the Event Bus
     *
     * @return True if the bus was successfully reset (If not interrupts!)
     * @see dev.zontreck.eventsbus.events.ResetEventBusEvent
     */
    public static boolean Reset() {
        if (!Post(new ResetEventBusEvent())) {

            Main.static_events = new HashMap<>();
            Main.instanced_events = new HashMap<>();

            Post(new EventBusReadyEvent());

            return true;
        }

        return false;

    }
}
