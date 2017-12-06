package cn.nukkit.server.event.firehandler;

import cn.nukkit.api.event.Event;
import cn.nukkit.api.event.EventHandler;
import cn.nukkit.server.event.EventFireHandler;
import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ReflectionEventFireHandler implements EventFireHandler {
    private static final long LONG_RUNNING_EVENT_TIME = TimeUnit.MILLISECONDS.toNanos(5);
    private final List<ListenerMethod> methods;

    public ReflectionEventFireHandler(Collection<ListenerMethod> methods) {
        this.methods = ImmutableList.copyOf(methods);
    }

    @Override
    public void fire(Event event) {
        long start = System.nanoTime();
        for (ListenerMethod method : methods) {
            try {
                method.run(event);
            } catch (InvocationTargetException | IllegalAccessException e) {
                log.error("Exception occurred while executing method " + method + " for " + event, e);
            }
        }
        long differenceTaken = System.nanoTime() - start;
        if (differenceTaken >= LONG_RUNNING_EVENT_TIME) {
            log.warn("Event {} took {}ms to fire!", event, BigDecimal.valueOf(differenceTaken)
                    .divide(new BigDecimal("1000000"), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
        }
    }

    public static class ListenerMethod implements Comparable<ListenerMethod> {
        private final Object listener;
        private final Method method;

        public ListenerMethod(Object listener, Method method) {
            this.listener = listener;
            this.method = method;
        }

        public void run(Event event) throws InvocationTargetException, IllegalAccessException {
            method.invoke(listener, event);
        }

        @Override
        public String toString() {
            return listener.getClass().getName() + "#" + method.getName();
        }

        public Object getListener() {
            return listener;
        }

        public Method getMethod() {
            return method;
        }

        @Override
        public int compareTo(@Nonnull ListenerMethod o) {
            EventHandler handler = getMethod().getAnnotation(EventHandler.class);
            if (handler == null) {
                return -1;
            }

            EventHandler handler2 = o.getMethod().getAnnotation(EventHandler.class);
            if (handler2 == null) {
                return 1;
            }

            return Integer.compare(handler.order(), handler2.order());
        }
    }
}
