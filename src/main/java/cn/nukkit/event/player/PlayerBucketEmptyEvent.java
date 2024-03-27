package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

public class PlayerBucketEmptyEvent extends PlayerBucketEvent {

    private static final HandlerList handlers = new HandlerList();

    private boolean mobSpawningAllowed;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerBucketEmptyEvent(Player who, Block blockClicked, BlockFace blockFace, Item bucket, Item itemInHand) {
        this(who, blockClicked, blockFace, bucket, itemInHand, true);
    }

    public PlayerBucketEmptyEvent(Player who, Block blockClicked, BlockFace blockFace, Item bucket, Item itemInHand, boolean mobSpawningAllowed) {
        super(who, blockClicked, blockFace, bucket, itemInHand);
        this.mobSpawningAllowed = mobSpawningAllowed;
    }

    /**
     * Whether a fish can be spawned if a fish bucket is emptied.
     * Notice: Disabling 'block-listener' in server.properties disables fish spawning from buckets.
     *
     * @return can spawn a fish
     */
    public boolean isMobSpawningAllowed() {
        return this.mobSpawningAllowed;
    }

    /**
     * Whether a fish can be spawned if a fish bucket is emptied.
     * Notice: Disabling 'block-listener' in server.properties disables fish spawning from buckets.
     *
     * @param mobSpawningAllowed can spawn a fish
     */
    public void setMobSpawningAllowed(boolean mobSpawningAllowed) {
        this.mobSpawningAllowed = mobSpawningAllowed;
    }
}
