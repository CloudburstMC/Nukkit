package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemStack;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerGlassBottleFillEvent extends PlayerEvent implements Cancellable {

    protected final ItemStack item;
    protected final Block target;
    private boolean cancelled;

    public PlayerGlassBottleFillEvent(Player player, Block target, ItemStack item) {
        super(player);
        this.target = target;
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public Block getBlock() {
        return target;
    }
}
