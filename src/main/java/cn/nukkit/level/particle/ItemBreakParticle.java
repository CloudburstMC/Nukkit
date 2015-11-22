package cn.nukkit.level.particle;

import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class ItemBreakParticle extends GenericParticle {
    public ItemBreakParticle(Vector3 pos, Item item) {
        super(pos, Particle.TYPE_ITEM_BREAK, (item.getId() << 16) | item.getDamage());
    }
}
