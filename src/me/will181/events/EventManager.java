package uk.ac.soton.wm3g16.nanotechnology.events;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**Events can be registered to this class, allowing them to be dispatched.
 * Listeners can be registered to this class, allowing them to listen for events that are dispatched.
 * @see Event
 * @see EventListener
 * @see EventHandler */
public abstract class EventManager {
	
	private static Map<Class<? extends Event>, Map<Class<? extends EventListener>, EventListenerDispatcher>> dispatchDictionary = new HashMap<>();
	
	/**<p>Registers a new Event in to the system, allowing for that Event to be called in future. 
	 * If the event is already registered, the method simply returns without doing anything.</p>
	 * 
	 * <p>It is recommended that registering events is done early, 
	 * as any EventListeners that have already been registered with this class will not be able to handle this Event unless re-registered.</p>
	 * @param event the event to register
	 * @throws IllegalArgumentException if the argument is null */
	public static void registerEvent(Class<? extends Event> event) throws IllegalArgumentException {
		if(event == null)
			throw new IllegalArgumentException("Can not register a null event!");
		
		if(isEventRegistered(event))
			return;
		
		dispatchDictionary.put(event, new HashMap<>());
	}
	
	/**Unregisters the given event, meaning it would need to be {@link #registerEvent(Class) registered} again to be dispatched.
	 * If the event is null or not registered, no action is taken.
	 * @param event the event to be unregistered */
	public static void unregisterEvent(Class<? extends Event> event) {
		if(event == null)
			return;
		
		if(!isEventRegistered(event))
			return;
		
		dispatchDictionary.remove(event);
	}
	
	/**Registers a new listener, allowing for it to listen for events.
	 * If the given listener listens for a type of event that has not been registered yet, it will not listen for that event, even if the event is later registered.
	 * @param listener the listener to registeer 
	 * @throws IllegalArgumentException if the given listener is null */
	public static void registerListener(EventListener listener) throws IllegalArgumentException {
		if(listener == null)
			throw new IllegalArgumentException("Can not register a null listener!");
		
		Class<? extends EventListener> listenerClass = listener.getClass();
		
		for(Class<? extends Event> currentEventClass : dispatchDictionary.keySet()) {
			Map<Class<? extends EventListener>, EventListenerDispatcher> currentEventListeners = dispatchDictionary.get(currentEventClass);
			
			if(currentEventListeners.containsKey(listenerClass)) {
				currentEventListeners.get(listenerClass).registerListener(listener);
			} else {
				Set<Method> eventHandlersForEvent = findEventHandlers(currentEventClass, listenerClass);
				
				if(eventHandlersForEvent.isEmpty())
					continue;
				
				EventListenerDispatcher dispatcher = new EventListenerDispatcher(currentEventClass, listenerClass, eventHandlersForEvent);
				dispatcher.registerListener(listener);				
				currentEventListeners.put(listenerClass, dispatcher);
			}
		}
	}
	
	/**Removes the provided listener from this class, meaning it will not handle events from this object when the {@link #dispatch(Event) dispatch} method is called.
	 * Note that this works based on whether the listener .equals one to be removed, not whether they are actually the same object.
	 * @param listener the listener to remove */
	public static void unregisterListener(EventListener listener) {
		for(Class<? extends Event> currentEventClass : dispatchDictionary.keySet()) {
			Map<Class<? extends EventListener>, EventListenerDispatcher> currentEventListeners = dispatchDictionary.get(currentEventClass);
			
			if(!currentEventListeners.containsKey(listener.getClass()))
				continue;
			
			currentEventListeners.get(listener.getClass()).unregisterListener(listener);
		}
	}
	
	/**Determines if the given type of event has been registered yet.
	 * If a given event has been registered and then unregistered, this method will return false for that event, unless it is registered again.
	 * @param event the event to check for
	 * @return true if the event has been registered; false if the event has not been registered */
	public static boolean isEventRegistered(Class<? extends Event> event) {
		return dispatchDictionary.containsKey(event);
	}

	/**Dispatches the given event to all the listeners registered with this class.
	 * Any method in a registered listener that handles the type of event provided will be called with the event given as the parameter.
	 * @param event the event to dispatch
	 * @throws IllegalArgumentException if the event is null or does not match the type of event this object handles */
	public static void dispatch(Event event) throws IllegalArgumentException {		
		if(!isEventRegistered(event.getClass()))
			throw new IllegalArgumentException("That type of event has not been registered with this class!");
		
		for(EventListenerDispatcher currentDispatcher : dispatchDictionary.get(event.getClass()).values())
			currentDispatcher.dispatch(event);
	}
	
	/**Determines whether the given method is an event handler for the given event.
	 * For this method to return true the following criteria must be met:
	 * 
	 * <ul>
	 * 	<li>The event must be annotated with @EventHandler;</li>
	 * 	<li>The event must have one parameter, where the class of the parameter taken must equal the given event class</li>
	 * </ul>
	 * @param method the method to check
	 * @param eventClass the class of event to check if the method can handle
	 * @return true if the method can handle the event; false if the method can not handle the event */
	public static boolean canHandleEvent(Method method, Class<? extends Event> eventClass) {
		if(!method.isAnnotationPresent(EventHandler.class))
			return false;
		
		Class<?>[] parameterTypes = method.getParameterTypes();
		
		if(parameterTypes.length != 1)
			return false;
		
		if(!eventClass.equals(parameterTypes[0]))
			return false;
		
		return true;
	}
	
	/**Finds all the methods that handle the given event within the given listener class.
	 * @param eventClass the type of event to find event handling methods for
	 * @param listenerClass the class of the listener to search for methods within
	 * @return a Set containing all the methods that handle the given event within the given listener class */
	private static Set<Method> findEventHandlers(Class<? extends Event> eventClass, Class<? extends EventListener> listenerClass) {
		Method[] listenerMethods = listenerClass.getMethods();
		Set<Method> eventHandlerMethods = new HashSet<>();
		
		for(Method current : listenerMethods) {
			if(canHandleEvent(current, eventClass))
				eventHandlerMethods.add(current);
		}
		
		return eventHandlerMethods;
	}

}
