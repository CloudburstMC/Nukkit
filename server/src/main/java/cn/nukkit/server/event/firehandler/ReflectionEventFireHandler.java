/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.event.firehandler;

import cn.nukkit.api.event.Event;
import cn.nukkit.api.event.EventFireHandler;
import cn.nukkit.api.event.EventHandler;
import cn.nukkit.api.event.Listener;
import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
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

    @Override
    public ImmutableList<EventFireHandler.ListenerMethod> getMethods() {
        return ImmutableList.copyOf(methods);
    }

    public static class ListenerMethod implements Comparable<ListenerMethod>, EventFireHandler.ListenerMethod {
        private final Listener listener;
        private final Method method;

        public ListenerMethod(Listener listener, Method method) {
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

        @Override
        public Listener getListener() {
            return listener;
        }

        @Override
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

            return Integer.compare(handler.priority().ordinal(), handler2.priority().ordinal());
        }
    }
}
