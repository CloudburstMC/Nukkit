package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemStack;
import lombok.Getter;
import lombok.Setter;

/**
 * @author CreeperFace
 */

@Getter
@Setter
public class PlayerInteractEntityEvent extends PlayerEvent implements Cancellable {

    protected final Entity entity;
    protected final ItemStack item;
    private boolean cancelled;

    public PlayerInteractEntityEvent(Player player, Entity entity, ItemStack item) {
        super(player);
        this.entity = entity;
        this.item = item;
    }
}
