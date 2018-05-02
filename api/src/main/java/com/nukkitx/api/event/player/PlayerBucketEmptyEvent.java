package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.util.data.BlockFace;

public class PlayerBucketEmptyEvent extends PlayerBucketEvent {

    public PlayerBucketEmptyEvent(Player who, Block blockClicked, BlockFace blockFace, ItemInstance bucket, ItemInstance itemInHand) {
        super(who, blockClicked, blockFace, bucket, itemInHand);
    }
}
