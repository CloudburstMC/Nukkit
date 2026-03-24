package cn.nukkit.entity.passive;

import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

public class EntityZombieNautilus extends EntityNautilus {

    public static final int NETWORK_ID = 150;

    public EntityZombieNautilus(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public Item[] getDrops() {
        if (!this.isBaby()) {
            return new Item[]{Item.get(Item.ROTTEN_FLESH, 0, Utils.rand(0, 3))};
        }

        return new Item[0];
    }
}
