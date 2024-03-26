package cn.nukkit.level.persistence.impl;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.persistence.ImmutableCompoundTag;
import cn.nukkit.level.persistence.PersistentDataContainer;
import cn.nukkit.nbt.tag.CompoundTag;

public class PersistentDataContainerEntityWrapper implements PersistentDataContainer {

    private final Entity entity;
    private CompoundTag storage;

    public PersistentDataContainerEntityWrapper(Entity entity) {
        this.entity = entity;
    }

    @Override
    public CompoundTag getReadStorage() {
        CompoundTag storage = this.getInternalStorage();
        if (storage == null) {
            return ImmutableCompoundTag.EMPTY;
        }
        return storage;
    }

    @Override
    public CompoundTag getStorage() {
        CompoundTag storage = this.getInternalStorage();
        if (storage == null) {
            storage = new CompoundTag();
            this.setStorage(storage);
        }
        return storage;
    }

    private CompoundTag getInternalStorage() {
        if (this.storage != null) {
            return this.storage;
        }

        if (this.entity.namedTag.contains(STORAGE_TAG)) {
            return this.storage = this.entity.namedTag.getCompound(STORAGE_TAG);
        }
        return null;
    }

    @Override
    public void setStorage(CompoundTag storage) {
        this.entity.namedTag.putCompound(STORAGE_TAG, storage);
        this.storage = storage;
    }

    @Override
    public void write() {
        this.setStorage(this.getStorage());
    }

    @Override
    public void clearStorage() {
        this.entity.namedTag.remove(STORAGE_TAG);
        this.storage = null;
    }
}
