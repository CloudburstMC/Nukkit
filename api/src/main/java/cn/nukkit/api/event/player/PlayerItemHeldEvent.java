package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemStack;
import lombok.Getter;
import lombok.Setter;

/**
 * author: MagicDroidX
 * Nukkit Project
 */

@Getter
@Setter
public class PlayerItemHeldEvent extends PlayerEvent implements Cancellable {

    private final ItemStack item;
    private final int slot;
    private boolean cancelled;

    public PlayerItemHeldEvent(Player player, ItemStack item, int slot) {
        super(player);
        this.item = item;
        this.slot = slot;
    }
}
