package cn.nukkit.server.event.player;

import cn.nukkit.server.Player;
import cn.nukkit.server.block.Block;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.item.Item;

public class PlayerGlassBottleFillEvent extends PlayerEvent implements Cancellable {

    protected final Item item;
    protected final Block target;

    public PlayerGlassBottleFillEvent(Player player, Block target, Item item) {
        this.player = player;
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
