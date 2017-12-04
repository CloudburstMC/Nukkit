package cn.nukkit.server.plugin;

import cn.nukkit.server.event.Event;
import cn.nukkit.server.event.Listener;
import cn.nukkit.server.utils.EventException;

/**
 * author: iNevet
 * Nukkit Project
 */
public interface EventExecutor {

    void execute(Listener listener, Event event) throws EventException;
}
