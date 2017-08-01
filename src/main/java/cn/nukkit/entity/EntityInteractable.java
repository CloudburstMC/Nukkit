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

    // Todo: Passive entity?? i18n and boat leaving text
    public abstract String getInteractButtonText();
    
    public abstract boolean canDoInteraction();

}
