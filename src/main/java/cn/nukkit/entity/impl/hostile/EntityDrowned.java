package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.Smiteable;
import cn.nukkit.entity.hostile.Drowned;
import cn.nukkit.item.Item;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

import static cn.nukkit.item.ItemIds.ROTTEN_FLESH;

/**
 * Created by PetteriM1
 */
public class EntityDrowned extends EntityHostile implements Drowned, Smiteable {

    public EntityDrowned(EntityType<Drowned> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.95f;
    }

    @Override
    public String getName() {
        return "Drowned";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(ROTTEN_FLESH)};
    }

    @Override
    public boolean isUndead() {
        return true;
    }

}
