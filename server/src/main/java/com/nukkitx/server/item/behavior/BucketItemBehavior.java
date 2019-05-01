package com.nukkitx.server.item.behavior;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.network.bedrock.session.PlayerSession;

/**
 * @author CreeperFace
 */
public class BucketItemBehavior extends SimpleItemBehavior {

    @Override
    public void onInteract(PlayerSession player, ItemStack item, Block block) {
        //TODO: liquids
    }

    @Override
    public void onInteract(PlayerSession player, ItemStack item, BaseEntity entity) {
        //TODO: cow milk
    }
}
