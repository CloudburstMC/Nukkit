package cn.nukkit.server.plugin;

import cn.nukkit.api.event.Listener;
import cn.nukkit.server.utils.EventException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MethodEventExecutor implements EventExecutor {

    private final Method method;

    public MethodEventExecutor(Method method) {
        this.method = method;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Listener listener, Event event) throws EventException {
        try {
            Class<Event>[] params = (Class<Event>[]) method.getParameterTypes();
            for (Class<Event> param : params) {
                if (param.isAssignableFrom(event.getClass())) {
                    method.invoke(listener, event);
                    break;
                }
            }
        } catch (InvocationTargetException ex) {
            throw new EventException(ex.getCause());
        } catch (ClassCastException ex) {
            // We are going to ignore ClassCastException because EntityDamageEvent can't be cast to EntityDamageByEntityEvent
        } catch (Throwable t) {
            throw new EventException(t);
        }
    }

    public Method getMethod() {
        return method;
    }
}