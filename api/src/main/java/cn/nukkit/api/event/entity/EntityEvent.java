package cn.nukkit.api.event.entity;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.Event;

public interface EntityEvent extends Event {

    Entity getEntity();
}
