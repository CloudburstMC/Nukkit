package cn.nukkit.event.player;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.HandlerList;
import cn.nukkit.player.Player;

public class PlayerMouseOverEntityEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Entity entity;

    public PlayerMouseOverEntityEvent(Player player, Entity entity) {
        this.player = player;
        this.entity = entity;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Entity getEntity() {
        return entity;
    }
}
