package cn.nukkit.api.event.entity;

import cn.nukkit.api.item.ItemStack;
import lombok.Getter;
import lombok.Setter;

/**
 * author: MagicDroidX
 * Nukkit Project
 */

@Getter
@Setter
public class EntityDeathEvent extends EntityEvent {

    private ItemStack[] drops = new ItemStack[0];

    public EntityDeathEvent(Living entity) {
        this(entity, new ItemStack[0]);
    }

    public EntityDeathEvent(Living entity, ItemStack[] drops) {
        super(entity);
        this.drops = drops;
    }
}
