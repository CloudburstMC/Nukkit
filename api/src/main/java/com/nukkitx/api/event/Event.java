package com.nukkitx.api.event;

/**
 * This interface specifies that this class is a event.
 */
public interface Event {

    /**
     * Gets the name of the event.
     * @return name of event
     */
    default String getEventName(){
        return getClass().getName();
    }
}
