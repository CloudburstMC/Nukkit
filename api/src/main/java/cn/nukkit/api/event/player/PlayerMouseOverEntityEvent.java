package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.entity.Entity;

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
