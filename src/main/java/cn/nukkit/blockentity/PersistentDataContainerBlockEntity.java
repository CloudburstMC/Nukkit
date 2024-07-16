package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.persistence.PersistentDataContainer;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.ToString;

@ToString(callSuper = true)
public class PersistentDataContainerBlockEntity extends BlockEntity implements PersistentDataContainer {

    public PersistentDataContainerBlockEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean onUpdate() {
        if (!this.isBlockEntityValid()) {
            this.close();
        }
        return false;
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.level.getBlockIdAt(this.chunk, (int) this.x, (int) this.y, (int) this.z) != BlockID.AIR;
    }

    @Override
    public boolean isValid() {
        return super.isValid() && this.isBlockEntityValid();
    }

    @Override
    public CompoundTag getStorage() {
        if (this.namedTag.contains(STORAGE_TAG)) {
            return this.namedTag.getCompound(STORAGE_TAG);
        }

        CompoundTag storage = new CompoundTag();
        this.setStorage(storage);
        return storage;
    }

    @Override
    public void setStorage(CompoundTag storage) {
        this.namedTag.put(STORAGE_TAG, storage);
        setDirty();
    }

    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PersistentDataContainerBlockEntity && super.equals(obj);
    }

    @Override
    public boolean canSaveToStorage() {
        return !this.isEmpty();
    }

    @Override
    public void write() {
        setDirty();
    }
}
