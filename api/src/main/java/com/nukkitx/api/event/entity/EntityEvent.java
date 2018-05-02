package com.nukkitx.api.event.entity;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.event.Event;

public interface EntityEvent extends Event {

    Entity getEntity();
}
