package cn.nukkit.plugin;

import cn.nukkit.event.Event;
import cn.nukkit.event.Listener;
import cn.nukkit.utils.EventException;

/**
 * author: iNevet
 * Nukkit Project
 */
public interface EventExecutor {

    void execute(Listener listener, Event event) throws EventException;
}
