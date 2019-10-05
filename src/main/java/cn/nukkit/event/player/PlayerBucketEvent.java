package cn.nukkit.event.player;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;

abstract class PlayerBucketEvent extends PlayerEvent implements Cancellable {

    private final Block blockClicked;

    private final BlockFace blockFace;

    private final Item bucket;

    private Item item;


    public PlayerBucketEvent(Player who, Block blockClicked, BlockFace blockFace, Item bucket, Item itemInHand) {
        this.player = who;
        this.blockClicked = blockClicked;
        this.blockFace = blockFace;
        this.item = itemInHand;
        this.bucket = bucket;
    }

    /**
     * Returns the bucket used in this event
     * @return bucket
     */
    public Item getBucket() {
        return this.bucket;
    }

    /**
     * Returns the item in hand after the event
     * @return item
     */
    public Item getItem() {
        return this.item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Block getBlockClicked() {
        return this.blockClicked;
    }

    public BlockFace getBlockFace() {
        return this.blockFace;
    }
}
