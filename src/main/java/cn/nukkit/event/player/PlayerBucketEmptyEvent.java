package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

public class PlayerBucketEmptyEvent extends PlayerBucketEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerBucketEmptyEvent(Player who, Block blockClicked, Integer blockFace, Item bucket, Item itemInHand) {
        super(who, blockClicked, blockFace, bucket, itemInHand);
    }

}
