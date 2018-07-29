/*
    Leola Programming Language
    Author: Tony Sparks
    See license.txt
*/
package seventh.shared;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import leola.vm.util.ClassUtil;

/**
 * Dispatches events.  This implementation is thread safe. 
 * 
 * @author Tony
 *
 */
public class EventDispatcher {

    /**
     * Handle to all listeners
     */
    private Map<Class<?>, Collection<EventListener>> eventListenerMap;
    
    /**
     * Queue of events
     */
    private Queue<Event> eventQueue;        
    
    /**
     * Cache for method look up.
     */
    private Map<Class<?>, EventMethodEntry> eventMethodMap;
    
    /**
     * @author Tony
     */
    private class EventMethodEntry {        
        Map<Class<?>, Method> invokables;
                
        /**
         * @param aListenerClass
         * @param eventMethod
         */
        EventMethodEntry() {
            this.invokables = new ConcurrentHashMap<Class<?>, Method>();
        }
        
        /**
         * Adds an event listener.
         * 
         * @param aListenerClass
         * @param eventType
         */
        void addListener(Class<?> aListenerClass, Class<?> eventType) {
            scan(aListenerClass, eventType);
        }
        
        /**
         * Remove a listener type
         * @param aListenerClass
         * @param eventType
         */
        void removeListener(Class<?> eventType) {
            this.invokables.remove(eventType);
        }
        
        /**
         * Scan the listener class for EventMethods.
         * 
         * @param aListenerClass
         */
        void scan(Class<?> aListenerClass, Class<?> eventType) {            
            /* Only get the public members */
            Method[] methods = aListenerClass.getDeclaredMethods();            
            if ( methods.length > 0 ) {                            
                for ( Method method : methods) {
                    /* Query for the event method annotation */
                    EventMethod eventMethod = ClassUtil.getAnnotation(EventMethod.class, aListenerClass, method);
                    if ( eventMethod != null ) {

                        /* Verify this only has one parameter, the Event */
                        Class<?>[] paramTypes = method.getParameterTypes();
                        if ( paramTypes.length == 1 && paramTypes[0].equals(eventType) ) { 
                             //ClassUtil.inheritsFrom(paramTypes[0], eventType) ) {
                            method.setAccessible(true);
                            this.invokables.put(eventType, method);
                            break;
                        }
                    }
                    
                }
            }
            
        }
        
        /**
         * Invoke the method
         * 
         * @param event
         */
        void invoke(EventListener aListener, Event event) {
            try {
                Method method = this.invokables.get(event.getClass());
                if ( method != null ) {
                    method.invoke(aListener, event);
                }
            }
            catch(Exception e) {        
                System.err.println("Error invoking listener method - " + e);
                e.printStackTrace(System.err);
            }
        }
    }
    
    /**
     */
    public EventDispatcher() {
        this.eventListenerMap = new ConcurrentHashMap<Class<?>, Collection<EventListener>>();
        this.eventMethodMap = new ConcurrentHashMap<Class<?>, EventMethodEntry>();
        this.eventQueue = new ConcurrentLinkedQueue<Event>();
    }
    
    
    /**
     * Queues the {@link Event}
     * @param event
     */
    public void queueEvent(Event event) {
        this.eventQueue.add(event);
    }
    
    /**
     * Clear the event queue
     */
    public void clearQueue() {
        this.eventQueue.clear();
    }
    
    /**
     * Processes the next event in the event queue.
     * 
     * @return true processed an event, false if no events where processed (due to
     * the queue being empty).
     */
    public boolean processQueue() {
        boolean eventProcessed = false;
        
        /* Poll from the queue */
        Event event = this.eventQueue.poll();
        if ( event != null ) {
            /* Send the event to the listeners */
            sendNow(event);
            
            eventProcessed = true;
        }
        
        return eventProcessed;
    }
    
    /**
     * Sends the supplied event now, bypassing the queue.
     * @param event
     */
    public <E extends Event> void sendNow(E event) {
        Class<?> eventClass = event.getClass();
        if ( this.eventListenerMap.containsKey(eventClass)) {
            Collection<EventListener> eventListeners = this.eventListenerMap.get(eventClass);
            for(EventListener listener : eventListeners) {
                Class<?> listenerClass = listener.getClass();                
                
                /* Get the method lookup cache */
                EventMethodEntry entry = this.eventMethodMap.get(listenerClass);
                
                /* Send out the event */
                if(entry != null) {
                    entry.invoke(listener, event);
                }
                
                /* If its been consumed, don't continue */
                if ( event.isConsumed() ) {
                    break;
                }
            }
        }
    }
        
    /**
     * Add an {@link EventListener}.
     * 
     * @param eventClass
     * @param eventListener
     */
    public void addEventListener(Class<?> eventClass, EventListener eventListener) {         
        if ( ! this.eventListenerMap.containsKey(eventClass)) {
            this.eventListenerMap.put(eventClass, new ConcurrentLinkedQueue<EventListener>());
        }
        
        /* Place the method lookup cache in place */
        Class<?> listenerClass = eventListener.getClass();
        if ( ! this.eventMethodMap.containsKey(listenerClass) ) {
            this.eventMethodMap.put(listenerClass, new EventMethodEntry());    
        }
        
        EventMethodEntry methodEntry = this.eventMethodMap.get(listenerClass);
        methodEntry.addListener(listenerClass, eventClass);
        
        /* Add the listener object */
        Collection<EventListener> eventListeners = this.eventListenerMap.get(eventClass);
        eventListeners.add(eventListener);                
    }
    
    /**
     * Removes an {@link EventListener}
     * 
     * @param eventClass
     * @param eventListener
     */
    public void removeEventListener(Class<?> eventClass, EventListener eventListener) {         
        if ( this.eventListenerMap.containsKey(eventClass)) {
            Collection<EventListener> eventListeners = this.eventListenerMap.get(eventClass);
            eventListeners.remove(eventListener);
            
            Class<?> listenerClass = eventListener.getClass();
            
            EventMethodEntry methodEntry = this.eventMethodMap.get(listenerClass);
            if ( methodEntry != null ) {
                methodEntry.removeListener(eventClass);
            }
        }            
    }
    
    /**
     * Remove all the {@link EventListener}s
     * 
     */
    public void removeAllEventListeners() {
        this.eventListenerMap.clear();
        this.eventMethodMap.clear();
    }
    
    /**
     * Get all the {@link Event} classes that currently have listeners.
     * 
     * @return the set of event classes
     */
    public Set<Class<?>> getEventClasses() {
        return this.eventListenerMap.keySet();
    }

}

