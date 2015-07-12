package cn.nukkit.event;

/**
 * Created by Nukkit Team.
 */
public abstract class Event {

    /**
     * Any callable event must declare the static variable
     *
     * public static handlerList = null;
     * public static eventPool = [];
     * public static nextEvent = 0;
     *
     * Not doing so will deny the proper event initialization
     */
    protected String eventName = null;
    private boolean isCancelled = false;
    /**
     * @return string
     */
    final public String getEventName(){
        return eventName == null ? getClass().getName() : eventName;
    }
    /**
     * @return bool
     *
     * @throws \
     */
    public boolean isCancelled() throws IllegalArgumentException{
        if(!(this instanceof Cancellable)){
            throw new IllegalArgumentException("Event is not Cancellable");
        }
        /** @var Event this */
        return isCancelled == true;
    }
    /**
     * @param value
     *
     * @return void
     *
     * @throws \BadMethodCallException
     */
    public void setCancelled(boolean value) throws IllegalAccessException{
        if(!(this instanceof Cancellable)){
            throw new IllegalArgumentException("Event is not Cancellable");
        }
        /** @var Event this */
        isCancelled = (boolean) value;
    }

    public void setCancelled() throws IllegalAccessException{
        setCancelled(true);
    }

    /**
     * @return HandlerList
     */
    public abstract HandlerList getHandlers();
    
}
