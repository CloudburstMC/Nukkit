package com.nukkitx.api.event.entity;

import com.nukkitx.api.event.Cancellable;

public interface EntityCombustEvent extends EntityEvent, Cancellable {

    int getDuration();

    void setDuration(int duration);
}
