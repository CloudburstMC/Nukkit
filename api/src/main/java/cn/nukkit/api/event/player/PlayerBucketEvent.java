package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemStack;
import cn.nukkit.api.util.data.BlockFace;
import lombok.Getter;
import lombok.Setter;

@Getter
abstract class PlayerBucketEvent extends PlayerEvent implements Cancellable {

    @Getter
    private final Block blockClicked;
    @Getter
    private final BlockFace blockFace;
    @Getter
    private final ItemStack bucket;
    @Getter
    @Setter
    private boolean cancelled;
    @Getter
    private ItemStack item;


    public PlayerBucketEvent(Player who, Block blockClicked, BlockFace blockFace, ItemStack bucket, ItemStack itemInHand) {
        this.player = who;
        this.blockClicked = blockClicked;
        this.blockFace = blockFace;
        this.item = itemInHand;
        this.bucket = bucket;
    }
}
