package cn.nukkit.api.event;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Method;

public interface EventFireHandler {

    void fire(Event event);

    ImmutableList<ListenerMethod> getMethods();

    interface ListenerMethod {

        Listener getListener();

        Method getMethod();
    }
}

