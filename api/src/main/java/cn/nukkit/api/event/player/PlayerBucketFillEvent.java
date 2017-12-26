package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.item.ItemStack;
import cn.nukkit.api.util.data.BlockFace;

public class PlayerBucketFillEvent extends PlayerBucketEvent {

    public PlayerBucketFillEvent(Player who, Block blockClicked, BlockFace blockFace, ItemStack bucket, ItemStack itemInHand) {
        super(who, blockClicked, blockFace, bucket, itemInHand);
    }

}
