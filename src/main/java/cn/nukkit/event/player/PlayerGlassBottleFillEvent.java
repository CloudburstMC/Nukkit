package cn.nukkit.event.player;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.item.Item;
import cn.nukkit.player.Player;

public class PlayerGlassBottleFillEvent extends PlayerEvent implements Cancellable {

    protected final Item item;
    protected final Block target;

    public PlayerGlassBottleFillEvent(Player player, Block target, Item item) {
        super(player);
        this.target = target;
        this.item = item.clone();
    }

    public Item getItem() {
        return item;
    }

    public Block getBlock() {
        return target;
    }
}
