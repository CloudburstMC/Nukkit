package cn.nukkit.entity.data;

import cn.nukkit.entity.Entity;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class NBTEntityData extends EntityData<CompoundTag> {
    public CompoundTag tag;

    public NBTEntityData(int id, CompoundTag tag) {
        super(id);
        this.tag = tag;
    }

    @Override
    public CompoundTag getData() {
        return this.tag;
    }

    @Override
    public void setData(CompoundTag tag) {
        this.tag = tag;
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_NBT;
    }
}
