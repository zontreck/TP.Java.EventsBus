package dev.zontreck.eventsbus;

import dev.zontreck.eventsbus.annotations.EventSubscriber;
import dev.zontreck.eventsbus.annotations.Priority;
import dev.zontreck.eventsbus.annotations.SingleshotEvent;
import dev.zontreck.eventsbus.annotations.Subscribe;
import dev.zontreck.eventsbus.events.EventBusReadyEvent;
import dev.zontreck.eventsbus.events.ResetEventBusEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class EventDispatcher
{
    private static List<Method> singleshot = new ArrayList<>();
    private static List<Class<?>> subscribers = new ArrayList<>();

    /**
     * Scans every Java class that is currently loaded. It then checks for Subscribe, and a proper parameter before posting the Event.
     * The Event will only be posted if not cancelled using {@link Event#setCancelled(boolean)} and that {@link Subscribe#allowCancelled()} allows.
     * @param event The event to post
     * @return True if cancelled.
     */

    public static boolean Post(Event event)
    {
        for(PriorityLevel level : PriorityLevel.values())
        {

            for(Class<?> clazz : subscribers)
            {
                for(Method M :clazz.getMethods())
                {
                    if(!M.isAnnotationPresent(Subscribe.class)) continue;

                    Subscribe subscriber = M.getAnnotation(Subscribe.class);


                    boolean canPost=true;
                    Class<?> param = M.getParameterTypes()[0];
                    if(param == event.getClass())
                    {
                        if(M.isAnnotationPresent(SingleshotEvent.class))
                        {
                            if(singleshot.contains(M))
                            {
                                canPost=false;
                            }
                        }
                    } else canPost=false;

                    PriorityLevel eventPriotityLevel= PriorityLevel.HIGH; // Default

                    if(M.isAnnotationPresent(Priority.class))
                    {
                        Priority prio = M.getAnnotation(Priority.class);
                        eventPriotityLevel=prio.Level();
                    }

                    if(level != eventPriotityLevel)
                    {
                        canPost=false;
                    }


                    // Dispatch the event now

                    try {
                        if(event.isCancelled() && !subscriber.allowCancelled())
                            continue;
                        else
                            M.invoke(null, event);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }

        return event.isCancelled();
    }


    /**
     * Scan all event subscribers
     */
    public static void Scan()
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Package[] packages = Package.getPackages();

        List<Class<?>> loaded = new ArrayList<>();

        for(Package pkg : packages)
        {
            try{
                String packageName = pkg.getName();
                Field classesField = ClassLoader.class.getDeclaredField("classes");
                classesField.setAccessible(true);

                List<Class<?>> classes = (List<Class<?>>) classesField.get(classLoader);

                for(Class<?> clazz : classes)
                {
                    if(clazz.getPackage().getName().equalsIgnoreCase(packageName))
                    {

                        if(clazz.isAnnotationPresent(EventSubscriber.class))
                            loaded.add(clazz);
                    }
                }

            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        subscribers = loaded;
    }

    /**
     * Resets the events system.
     * <br/>
     * This action clears the Singleshot list for the events that should only be invoked once. And rescans all classes incase new classes were dynamically loaded.
     */
    public static void Reset()
    {
        Post(new ResetEventBusEvent());

        singleshot.clear();
        Scan();
        ClassScanner.DoScan();

        Post(new EventBusReadyEvent());
    }
}
