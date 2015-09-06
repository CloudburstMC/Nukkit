package cn.nukkit.event;

/**
 * Created by Nukkit Team.
 */
public interface Cancellable {

    boolean isCancelled();

    void setCancelled() throws IllegalAccessException;

    void setCancelled(boolean forceCancel) throws IllegalAccessException;
}
