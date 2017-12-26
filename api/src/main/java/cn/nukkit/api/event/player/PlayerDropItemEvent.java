package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemStack;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerDropItemEvent extends PlayerEvent implements Cancellable {

    private final ItemStack item;
    private boolean cancelled;

    public PlayerDropItemEvent(Player player, ItemStack drop) {
        super(player);
        this.item = drop;
    }
}
