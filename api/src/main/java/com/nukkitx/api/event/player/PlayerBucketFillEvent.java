package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.util.data.BlockFace;

public class PlayerBucketFillEvent extends PlayerBucketEvent {

    public PlayerBucketFillEvent(Player who, Block blockClicked, BlockFace blockFace, ItemStack bucket, ItemStack itemInHand) {
        super(who, blockClicked, blockFace, bucket, itemInHand);
    }
}
