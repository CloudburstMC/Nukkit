package cn.nukkit.server.event;

import cn.nukkit.api.event.Event;
import cn.nukkit.api.event.EventHandler;
import cn.nukkit.api.event.EventManager;
import cn.nukkit.api.event.Listener;
import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.server.event.firehandler.ReflectionEventFireHandler;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Method;
import java.util.*;

public class NukkitEventManager implements EventManager{
    private final List<Object> listeners = new ArrayList<>();
    private final Map<Object, List<Object>> listenersByPlugin = new HashMap<>();
    private final Object registerLock = new Object();
    private volatile Map<Class<? extends Event>, EventFireHandler> eventHandlers = Collections.emptyMap();

    @Override
    public void register(Listener listener, Plugin plugin) {
        Preconditions.checkNotNull(plugin, "plugin");
        Preconditions.checkNotNull(listener, "listener");

        // Verify that all listeners are valid.
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                if (method.getParameterCount() != 1) {
                    throw new IllegalArgumentException("Method " + method.getName() + " in " + listener + " does not accept only one parameter.");
                }

                if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    throw new IllegalArgumentException("Method " + method.getName() + " in " + listener + " does not accept a subclass of Event.");
                }

                method.setAccessible(true);
            }
        }

        synchronized (registerLock) {
            listenersByPlugin.computeIfAbsent(plugin, k -> new ArrayList<>()).add(listener);
            listeners.add(listener);
            bakeHandlers();
        }
    }

    @Override
    public void fire(Event event) {
        Preconditions.checkNotNull(event, "event");
        EventFireHandler handler = eventHandlers.get(event.getClass());
        if (handler != null) {
            handler.fire(event);
        }
    }

    @Override
    public void unregisterListener(Listener listener) {
        Preconditions.checkNotNull(listener, "listener");
        synchronized (registerLock) {
            for (List<Object> objects : listenersByPlugin.values()) {
                objects.remove(listener);
            }
            listeners.remove(listener);
            bakeHandlers();
        }
    }

    @Override
    public void unregisterAllListeners(Plugin plugin) {
        Preconditions.checkNotNull(plugin, "plugin");
        synchronized (registerLock) {
            List<Object> objects = listenersByPlugin.remove(plugin);
            if (objects != null) {
                listeners.removeAll(objects);
                bakeHandlers();
            }
        }
    }

    private void bakeHandlers() {
        Map<Class<? extends Event>, List<ReflectionEventFireHandler.ListenerMethod>> listenerMap = new HashMap<>();

        for (Object listener : listeners) {
            for (Method method : listener.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(EventHandler.class)) {
                    listenerMap.computeIfAbsent((Class<? extends Event>) method.getParameterTypes()[0], (k) -> new ArrayList<>())
                            .add(new ReflectionEventFireHandler.ListenerMethod(listener, method));
                }
            }
        }

        for (List<ReflectionEventFireHandler.ListenerMethod> methods : listenerMap.values()) {
            Collections.sort(methods);
        }

        Map<Class<? extends Event>, EventFireHandler> handlerMap = new HashMap<>();
        for (Map.Entry<Class<? extends Event>, List<ReflectionEventFireHandler.ListenerMethod>> entry : listenerMap.entrySet()) {
            handlerMap.put(entry.getKey(), new ReflectionEventFireHandler(entry.getValue()));
        }
        this.eventHandlers = ImmutableMap.copyOf(handlerMap);
    }
}
