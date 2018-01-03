package cn.nukkit.api.scheduler;

import cn.nukkit.api.plugin.Plugin;

/**
 * @author CreeperFace
 */
public interface TaskHandler {

    int getTaskId();

    Plugin getPlugin();

    void cancel();

    Runnable getTask();

    boolean isRepeating();

    boolean isDelayed();

    int getPeriod();

    int getDelay();

    boolean isAsync();

    void setDelay(int delay);

    void setPeriod(int period);
}
