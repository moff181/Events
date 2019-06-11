package uk.ac.soton.wm3g16.nanotechnology.events;

/**Used to mark classes that contain methods that handle events.
 * EventListeners need to be registered with {@link EventManager#registerListener(EventListener)} in order for them to work.
 * Any methods that handle events should be marked with {@link EventHandler}.
 * @see Event
 * @see EventHandler
 * @see EventManager */
public interface EventListener {
}
