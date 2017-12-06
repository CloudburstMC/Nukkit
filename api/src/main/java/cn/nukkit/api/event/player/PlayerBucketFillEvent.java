package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.server.block.Block;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.math.BlockFace;

public class PlayerBucketFillEvent extends PlayerBucketEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerBucketFillEvent(Player who, Block blockClicked, BlockFace blockFace, Item bucket, Item itemInHand) {
        super(who, blockClicked, blockFace, bucket, itemInHand);
    }

}
