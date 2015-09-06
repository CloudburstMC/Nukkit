package cn.nukkit.plugin;

import cn.nukkit.event.Event;
import cn.nukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MethodEventExecutor implements EventExecutor {

    private Method method;

    public MethodEventExecutor(Method method) {
        this.method = method;
    }

    @Override
    public void execute(Listener listener, Event event) {
        try {
            method.invoke(listener, event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
