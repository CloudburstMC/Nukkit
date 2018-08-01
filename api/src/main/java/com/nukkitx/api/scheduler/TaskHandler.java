package com.nukkitx.api.scheduler;

/**
 * @author CreeperFace
 */
public interface TaskHandler<T> {

    int getTaskId();

    T getPlugin();

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
