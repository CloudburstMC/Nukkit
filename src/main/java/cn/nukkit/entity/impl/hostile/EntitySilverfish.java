package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.Arthropod;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.hostile.Silverfish;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntitySilverfish extends EntityHostile implements Silverfish, Arthropod {

    public EntitySilverfish(EntityType<Silverfish> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public String getName() {
        return "Silverfish";
    }

    @Override
    public float getWidth() {
        return 0.4f;
    }

    @Override
    public float getHeight() {
        return 0.3f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(8);
    }
}
