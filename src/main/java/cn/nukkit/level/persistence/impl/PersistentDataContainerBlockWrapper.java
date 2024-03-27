package cn.nukkit.level.persistence.impl;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.persistence.ImmutableCompoundTag;
import cn.nukkit.level.persistence.PersistentDataContainer;
import cn.nukkit.nbt.tag.CompoundTag;

public class PersistentDataContainerBlockWrapper implements PersistentDataContainer {

    private final BlockEntity blockEntity;
    private CompoundTag storage;

    public PersistentDataContainerBlockWrapper(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
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

        if (this.blockEntity.namedTag.contains(STORAGE_TAG)) {
            return this.storage = this.blockEntity.namedTag.getCompound(STORAGE_TAG);
        }
        return null;
    }

    @Override
    public void setStorage(CompoundTag storage) {
        this.blockEntity.namedTag.putCompound(STORAGE_TAG, storage);
        this.storage = storage;
    }

    @Override
    public void clearStorage() {
        this.blockEntity.namedTag.remove(STORAGE_TAG);
        this.storage = null;
    }
}
