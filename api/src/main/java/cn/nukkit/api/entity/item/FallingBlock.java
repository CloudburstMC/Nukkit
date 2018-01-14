package cn.nukkit.api.entity.item;

import cn.nukkit.api.entity.Entity;

/**
 * @author CreeperFace
 */
public interface FallingBlock extends Entity {

    int getBlockId();

    int getBlockDamage();
}
