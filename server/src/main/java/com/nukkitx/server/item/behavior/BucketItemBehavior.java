package com.nukkitx.server.item.behavior;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;

/**
 * @author CreeperFace
 */
public class BucketItemBehavior extends SimpleItemBehavior {

    @Override
    public void onInteract(NukkitPlayerSession player, ItemInstance item, Block block) {
        //TODO: liquids
    }

    @Override
    public void onInteract(NukkitPlayerSession player, ItemInstance item, BaseEntity entity) {
        //TODO: cow milk
    }
}
