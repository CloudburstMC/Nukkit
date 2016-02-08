package cn.nukkit.event.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityDamageByBlockEvent extends EntityDamageEvent {

    private Block damager;

    public EntityDamageByBlockEvent(Block damager, Entity entity, int cause, float damage) {
        super(entity, cause, damage);
        this.damager = damager;
    }

    public Block getDamager() {
        return damager;
    }

}
