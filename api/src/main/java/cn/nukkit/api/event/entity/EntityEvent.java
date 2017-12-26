package cn.nukkit.api.event.entity;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * author: MagicDroidX
 * Nukkit Project
 */

@AllArgsConstructor
@Getter
public abstract class EntityEvent implements Event {

    protected Entity entity;
}
