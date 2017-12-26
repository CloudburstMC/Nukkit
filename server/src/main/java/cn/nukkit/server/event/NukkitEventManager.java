package cn.nukkit.server.event;

import cn.nukkit.api.event.*;
import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.server.event.firehandler.ReflectionEventFireHandler;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Method;
import java.util.*;

public class NukkitEventManager implements EventManager {

    private volatile Map<Class<? extends Event>, EventFireHandler> eventHandlers = Collections.emptyMap();
    private final Map<Plugin, List<Listener>> listenersByPlugin = new HashMap<>();
    private final List<Listener> listeners = new ArrayList<>();
    private final Object registerLock = new Object();

    @Override
    public void registerListener(Listener listener, Plugin plugin) {
        Preconditions.checkNotNull(plugin, "plugin");
        Preconditions.checkNotNull(listener, "listener");

        // Verify that all listeners are valid.
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                if (method.isBridge() || method.isSynthetic()) {
                    continue;
                }
                if (method.getParameterCount() != 1) {
                    throw new IllegalArgumentException("Method " + method.getName() + " in " + listener + " does not accept only one parameter.");
                }

                if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    throw new IllegalArgumentException("Method " + method.getName() + " in " + listener + " does not accept a subclass of Event.");
                }

                method.setAccessible(true);

                /*for (Class<?> clazz = eventClass; Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) { TODO: Reimplement verbose for deprecated events.
                    // This loop checks for extending deprecated events
                    if (clazz.getAnnotation(Deprecated.class) != null) {
                        if (Boolean.valueOf(String.valueOf(this.server.getConfig("settings.deprecated-verbose", true)))) {
                            log.warn(this.server.getLanguage().translateString("nukkit.plugin.deprecatedEvent", plugin.getName(), clazz.getName(), listener.getClass().getName() + "." + method.getName() + "()"));
                        }
                        break;
                    }
                }*/
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
            for (List<Listener> listeners : listenersByPlugin.values()) {
                listeners.remove(listener);
            }
            listeners.remove(listener);
            bakeHandlers();
        }
    }

    @Override
    public void unregisterAllListeners(Plugin plugin) {
        Preconditions.checkNotNull(plugin, "plugin");
        synchronized (registerLock) {
            List<Listener> listeners = listenersByPlugin.remove(plugin);
            if (listeners != null) {
                this.listeners.removeAll(listeners);
                bakeHandlers();
            }
        }
    }

    public void unregisterListeners(Collection<Listener> listeners) {
        Preconditions.checkNotNull(listeners, "listeners");
        synchronized (registerLock) {
            if (listeners.size() > 0) {
                this.listeners.removeAll(listeners);
                bakeHandlers();
            }
        }
    }

    public void getEventListenerMethods(Class<? extends Event> eventClass) {
        Preconditions.checkNotNull(eventClass, "eventClass");
        eventHandlers.get(eventClass).getMethods();
    }

    private void bakeHandlers() {
        Map<Class<? extends Event>, List<ReflectionEventFireHandler.ListenerMethod>> listenerMap = new HashMap<>();

        for (Listener listener : listeners) {
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
