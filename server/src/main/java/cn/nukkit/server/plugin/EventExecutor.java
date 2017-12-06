package cn.nukkit.server.plugin;

import cn.nukkit.api.event.Listener;
import cn.nukkit.server.utils.EventException;

/**
 * author: iNevet
 * Nukkit Project
 */
public interface EventExecutor {

    void execute(Listener listener, Event event) throws EventException;
}
