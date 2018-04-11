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

package cn.nukkit.api.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义一个事件的处理器的注解。<br>
 * Annotation that defines a handler.
 * <p>
 * <p>一个处理器的重要程度被称作处理器的<b>优先级</b>，优先级高的处理器有更多的决定权。参见：{@link #priority()}<br>
 * The importance of a handler is called its <b>priority</b>, handlers with higher priority speaks louder then
 * lower ones. See: {@link #priority()}</p>
 * <p>
 * <p>处理器可以选择忽略或不忽略被取消的事件，这种特性可以在{@link #ignoreCancelled()}中定义。<br>
 * A handler can choose to ignore a cancelled event or not, that can be defined in {@link #ignoreCancelled()}.</p>
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author null(javadoc) @ Nukkit Project
 * @see cn.nukkit.api.event.Listener
 * @see cn.nukkit.api.event.Event
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
    /**
     * 定义这个处理器的优先级。<br>
     * Define the priority of the handler.
     * <p>
     * <p>Nukkit调用处理器时会按照优先级从低到高的顺序调用，这样保证了高优先级的监听器能覆盖低优先级监听器做出的处理。
     * 调用的先后顺序如下：<br>
     * When Nukkit calls all handlers, ones with lower priority is called earlier,
     * that make handlers with higher priority can replace the decisions made by lower ones.
     * The order that Nukkit call handlers is from the first to the last as:
     * <ol>
     * <li>EventPriority.LOWEST
     * <li>EventPriority.LOW
     * <li>EventPriority.NORMAL
     * <li>EventPriority.HIGH
     * <li>EventPriority.HIGHEST
     * <li>EventPriority.MONITOR
     * </ol>
     * </p>
     *
     * @return 这个处理器的优先级。<br>The priority of this handler.
     * @see cn.nukkit.api.event.EventHandler
     */
    EventPriority priority() default EventPriority.NORMAL;

    /**
     * 定义这个处理器是否忽略被取消的事件。<br>
     * Define if the handler ignores a cancelled event.
     * <p>
     * <p>如果为{@code true}而且事件发生，这个处理器不会被调用，反之相反。<br>
     * If ignoreCancelled is {@code true} and the event is cancelled, the method is
     * not called. Otherwise, the method is always called.</p>
     *
     * @return 这个处理器是否忽略被取消的事件。<br>Whether cancelled events should be ignored.
     * @see cn.nukkit.api.event.EventHandler
     */
    boolean ignoreCancelled() default false;
}
