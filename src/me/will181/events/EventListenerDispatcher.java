package uk.ac.soton.wm3g16.nanotechnology.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**<p>Used for storing a particular type of EventListener, and for dispatching events to those listeners.
 * Each EventListenerDispatcher only deals with a particular type of Event and a particular type of EventListener.
 * Trying to {@link #registerListener(EventListener) register} a listener that is of a different class to the one the EventListenerDispatcher handles will result in an error.
 * Likewise, trying to {@link #dispatch(Event) dispatch} an event that is of a different class to the one the EventListenerDispatcher handles will result in an error.</p>
 * 
 * <p>For example, if a given EventListenerDispatcher was {@link #EventListenerDispatcher(Class, Class, Set) created} to handle an event class called 'MyEvent' and a listener class of 'MyListener',
 * trying to register a listener of class 'MyOtherListener' would result in an error.
 * Similarly, trying to dispatch an event of class 'MyOtherEvent' would result in an error.</p>
 * @see Event
 * @see EventListener
 * @see EventManager
 * @see EventHandler */
public class EventListenerDispatcher {
	
	private final Class<? extends Event> eventClass;
	private final Class<? extends EventListener> listenerClass;
	private List<EventListener> listeners;
	private Set<Method> methods;
	
	/**Constructor. 
	 * All provided methods are verified to guarantee they can handle an event, using {@link EventManager#canHandleEvent(Method, Class)};
	 * any methods that are found to not be able to handle the given event are removed from the provided set of methods.
	 * @param eventClass the class of the type of event that this object should handle
	 * @param listenerClass the class of the type of listener that this object should handle
	 * @param methods a set of all the methods in the given listener class that can handle the given event class */
	public EventListenerDispatcher(Class<? extends Event> eventClass, Class<? extends EventListener> listenerClass, Set<Method> methods) {
		this.eventClass = eventClass;
		this.listenerClass = listenerClass;
		this.listeners = new ArrayList<EventListener>();
		this.methods = methods;
		
		for(Iterator<Method> it = this.methods.iterator(); it.hasNext();) {
			Method current = it.next();
			if(!EventManager.canHandleEvent(current, eventClass))
				this.methods.remove(current);
		}
	}

	/**Registers a new EventListener object with this class. 
	 * The class of this EventListener should match the class provided when this EventListenerDispatcher was created.
	 * Once an EventListener has been registered, whenever the {@link #dispatch(Event) dispatch} method is called, 
	 * it will have the event passed to all the EventHandler methods that handle such an event 
	 * (as were provided in the {@link #EventListenerDispatcher(Class, Class, Set) constructor}). 
	 * @param listener the EventListener object to register
	 * @throws IllegalArgumentException if the EventListener is null or is of an incorrect class */
	public void registerListener(EventListener listener) throws IllegalArgumentException {
		if(listener == null)
			throw new IllegalArgumentException("Listener can not be null!");
		
		if(!listenerClass.equals(listener.getClass()))
			throw new IllegalArgumentException("Provided listener is not of the correct class (is " + listener.getClass().getName() + " but should be " + listenerClass.getName() + ")");
		
		listeners.add(listener);
	}
	
	/**Removes the provided listener from this object, meaning it will not handle events from this object when the {@link #dispatch(Event) dispatch} method is called.
	 * Note that this works based on whether the listener .equals one to be removed, not whether they are actually the same object.
	 * @param listener the listener to remove */
	public void unregisterListener(EventListener listener) {		
		listeners.remove(listener);
	}
	
	/**Dispatches the given event to all the listeners registered with this class.
	 * Any method in a registered listener that handles the type of event provided will be called with the event given as the parameter.
	 * @param event the event to dispatch
	 * @throws IllegalArgumentException if the event is null or does not match the type of event this object handles */
	public void dispatch(Event event) throws IllegalArgumentException {		
		if(event == null)
			throw new IllegalArgumentException("Can not dispatch a null event!");
		
		if(!eventClass.equals(event.getClass()))
			throw new IllegalArgumentException("This EventListenerDispatcher is not setup for that type of event!");
		
		for(EventListener listener : listeners) {
			for(Method method : methods) {
				try {
					method.invoke(listener, event);
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**@return the class of the type of event this object handles */
	public Class<? extends Event> getEventClass() {
		return this.eventClass;
	}
	
	/**@return the class of listener that this object handles */
	public Class<? extends EventListener> getListenerClass() {
		return this.listenerClass;
	}
	
	/**@return the number of methods in the given listener class that handle the given event class */
	public int countHandlingMethods() {
		return this.methods.size();
	}

}
