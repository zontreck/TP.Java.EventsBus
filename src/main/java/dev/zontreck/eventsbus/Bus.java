package dev.zontreck.eventsbus;

public class Bus {
    public static Bus Main = new Bus("Main Event Bus");
    public final String BusName;

    public Bus(String name) {
        BusName = name;
    }
}
