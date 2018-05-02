package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.entity.Entity;

public class PlayerMouseOverEntityEvent implements PlayerEvent {
    private final Player player;
    private final Entity entity;

    public PlayerMouseOverEntityEvent(Player player, Entity entity) {
        this.player = player;
        this.entity = entity;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public Entity getEntity() {
        return entity;
    }
}
