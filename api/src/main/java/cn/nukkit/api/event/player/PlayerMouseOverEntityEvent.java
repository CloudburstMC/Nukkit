package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.entity.Entity;
import lombok.Getter;

@Getter
public class PlayerMouseOverEntityEvent extends PlayerEvent {

    private final Entity entity;

    public PlayerMouseOverEntityEvent(Player player, Entity entity) {
        super(player);
        this.entity = entity;
    }
}
