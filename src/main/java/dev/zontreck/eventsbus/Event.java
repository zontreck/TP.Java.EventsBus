package dev.zontreck.eventsbus;

public class Event {
    private boolean cancelled = false;

    /**
     * Checks if the event can be cancelled.
     * 
     * @see Cancellable
     * @return True if the cancellation annotation is present.
     */
    public boolean IsCancellable() {
        Class<?> Current = this.getClass();
        return Current.isAnnotationPresent(Cancellable.class);
    }

    /**
     * Checks if the event is cancelled.
     * 
     * @return False if the event cannot be cancelled, or returns the current status
     *         for the event.
     */
    public boolean isCancelled() {
        if (!IsCancellable())
            return false;
        return cancelled;
    }

    /**
     * Sets the cancelled status for the event
     * 
     * @param cancel Whether the event should be marked as cancelled or not.
     */
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}