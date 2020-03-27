package cn.nukkit.level.particle;

import cn.nukkit.item.Item;
import cn.nukkit.registry.ItemRegistry;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.LevelEventType;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class ItemBreakParticle extends GenericParticle {
    public ItemBreakParticle(Vector3f pos, Item item) {
        super(pos, LevelEventType.PARTICLE_ITEM_BREAK, (ItemRegistry.get().getRuntimeId(item.getId()) << 16) | item.getMeta());
    }
}
