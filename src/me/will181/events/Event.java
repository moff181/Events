package uk.ac.soton.wm3g16.nanotechnology.events;

/**Children of this class should be used as an event.
 * An event can be dispatched to all the listeners using {@link EventManager#dispatch(Event)}, 
 * which will trigger all the methods in EventListener classes that have been registered with the {@link EventManager}.
 * Events need to be registered with the EventManager using {@link EventManager#registerEvent(Class)} before they can be invoked. 
 * @see EventListener
 * @see EventHandler
 * @see EventManager */
public abstract class Event {
} 
