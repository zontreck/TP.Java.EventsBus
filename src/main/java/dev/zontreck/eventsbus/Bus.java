package dev.zontreck.eventsbus;

import org.checkerframework.common.reflection.qual.GetClass;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class Bus {
    /**
     * The main event bus!
     */
    private static Bus Main = new Bus("Main Event Bus", false);

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

    public Map<Class<?>, List<EventContainer>> static_events = new HashMap<Class<?>, List<EventContainer>>();
    public Map<Class<?>, List<EventContainer>> instanced_events = new HashMap<Class<?>, List<EventContainer>>();

    public static <T> void Register(Class<T> clazz, T instance) {

        List<Method> nonStaticMethods = Arrays.stream(clazz.getMethods())
                .filter(x -> x.isAnnotationPresent(Subscribe.class))
                .filter(x -> x.getModifiers() != Modifier.STATIC)
                .toList();

        List<Method> staticMethods = Arrays.stream(clazz.getMethods())
                .filter(x -> x.isAnnotationPresent(Subscribe.class))
                .filter(x -> x.getModifiers() == Modifier.STATIC)
                .toList();

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

                container.IsSingleshot = m.isAnnotationPresent(SingleshotEvent.class);

                if (Main.instanced_events.containsKey(clazz))
                    Main.instanced_events.get(clazz).add(container);
                else {
                    Main.instanced_events.put(clazz, new ArrayList<>());
                    Main.instanced_events.get(clazz).add(container);
                }
            }
        }

        for (Method m : staticMethods) {
            EventContainer container = new EventContainer();
            container.instance = null;
            container.clazz = clazz;
            if (m.isAnnotationPresent((Priority.class)))
                container.Level = m.getAnnotation(Priority.class).Level();
            else container.Level = PriorityLevel.LOWEST;

            container.IsSingleshot = m.isAnnotationPresent(SingleshotEvent.class);

            if (Main.static_events.containsKey(clazz))
                Main.static_events.get(clazz).add(container);
            else {
                Main.static_events.put(clazz, new ArrayList<>());
                Main.static_events.get(clazz).add(container);
            }
        }
    }
}
