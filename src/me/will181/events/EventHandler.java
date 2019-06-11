package uk.ac.soton.wm3g16.nanotechnology.events;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**Used to mark methods that are used to handle events.
 * Any methods that are not marked with this will be ignored when registering listeners.
 * @see EventManager
 * @see Event
 * @see EventListener */
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
}
