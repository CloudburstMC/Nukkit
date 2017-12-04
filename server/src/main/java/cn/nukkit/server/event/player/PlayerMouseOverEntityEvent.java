package cn.nukkit.server.event.player;

import cn.nukkit.server.Player;
import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.event.HandlerList;

public class PlayerMouseOverEntityEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Entity entity;

    public PlayerMouseOverEntityEvent(Player player, Entity entity) {
        this.player = player;
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
