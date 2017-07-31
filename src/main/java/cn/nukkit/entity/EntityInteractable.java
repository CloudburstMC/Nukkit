package cn.nukkit.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Adam Matthew
 */
public abstract class EntityInteractable extends Entity {

    public EntityInteractable(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    // Todo: Passive entity
    public abstract String getInteractButton();
    
    public abstract boolean canDoInteraction();

}
