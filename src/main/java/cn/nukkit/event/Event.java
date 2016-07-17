package cn.nukkit.event;

import cn.nukkit.utils.EventException;

/**
 * 描述服务器中可能发生的事情的类。<br>
 * Describes things that happens in the server.
 * <p>
 * <p>服务器中可能发生的事情称作<b>事件</b>。定义一个需要它在一个事件发生时被运行的过程，这个过程称作<b>监听器</b>。<br>
 * Things that happens in the server is called a <b>event</b>. Define a procedure that should be executed
 * when a event happens, this procedure is called a <b>listener</b>.</p>
 * <p>
 * <p>Nukkit调用事件的处理器时，会通过参数的类型判断需要被监听的事件。<br>
 * When Nukkit is calling a handler, the event needed to listen is judged by the type of the parameter. </p>
 * <p>
 * <p>关于监听器的实现，参阅：{@link Listener} <br>
 * For the way to implement a listener, see: {@link cn.nukkit.event.Listener}</p>
 *
 * @author Unknown(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.event.EventHandler
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public abstract class Event {

    protected final String eventName = null;
    private boolean isCancelled = false;

    final public String getEventName() {
        return eventName == null ? getClass().getName() : eventName;
    }

    public boolean isCancelled() {
        if (!(this instanceof Cancellable)) {
            throw new EventException("Event is not Cancellable");
        }
        return isCancelled;
    }

    public void setCancelled() {
        setCancelled(true);
    }

    public void setCancelled(boolean value) {
        if (!(this instanceof Cancellable)) {
            throw new EventException("Event is not Cancellable");
        }
        isCancelled = value;
    }


}
