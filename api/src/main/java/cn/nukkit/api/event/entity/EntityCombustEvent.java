package cn.nukkit.api.event.entity;

import cn.nukkit.api.event.Cancellable;

public interface EntityCombustEvent extends EntityEvent, Cancellable {

    int getDuration();

    void setDuration(int duration);
}
