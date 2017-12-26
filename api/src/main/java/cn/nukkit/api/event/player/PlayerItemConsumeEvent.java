package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemStack;
import lombok.Getter;
import lombok.Setter;

/**
 * Called when a player eats something
 */

@Getter
@Setter
public class PlayerItemConsumeEvent extends PlayerEvent implements Cancellable {

    private final ItemStack item;
    private boolean cancelled;

    public PlayerItemConsumeEvent(Player player, ItemStack item) {
        super(player);
        this.item = item;
    }
}
