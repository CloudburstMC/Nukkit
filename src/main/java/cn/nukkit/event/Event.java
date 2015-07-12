package cn.nukkit.event;

/**
 * Created by Nukkit Team.
 */
public abstract class Event {

    protected String eventName = null;
    private boolean isCancelled = false;

    final public String getEventName() {
        return eventName == null ? getClass().getName() : eventName;
    }

    public boolean isCancelled() throws IllegalArgumentException {
        if (!(this instanceof Cancellable)) {
            throw new IllegalArgumentException("Event is not Cancellable");
        }
        return isCancelled;
    }

    public void setCancelled() throws IllegalAccessException {
        setCancelled(true);
    }

    public void setCancelled(boolean value) throws IllegalAccessException {
        if (!(this instanceof Cancellable)) {
            throw new IllegalArgumentException("Event is not Cancellable");
        }
        isCancelled = (boolean) value;
    }


    public abstract HandlerList getHandlers();

}
