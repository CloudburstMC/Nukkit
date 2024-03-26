package cn.nukkit.level.persistence.impl;

import cn.nukkit.item.Item;
import cn.nukkit.level.persistence.ImmutableCompoundTag;
import cn.nukkit.level.persistence.PersistentItemDataContainer;
import cn.nukkit.nbt.tag.CompoundTag;

public class PersistentDataContainerItemWrapper implements PersistentItemDataContainer {

    private final Item item;
    private boolean convertsToBlock = false;
    private CompoundTag storage;

    public PersistentDataContainerItemWrapper(Item item) {
        this.item = item;
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

        if (this.item.hasCompoundTag() && this.item.getNamedTag().contains(STORAGE_TAG)) {
            return this.storage = this.item.getNamedTag().getCompound(STORAGE_TAG);
        }
        return null;
    }

    @Override
    public void setStorage(CompoundTag storage) {
        CompoundTag compoundTag = this.item.hasCompoundTag() ? this.item.getNamedTag() : new CompoundTag();
        compoundTag.putCompound(STORAGE_TAG, storage);
        this.item.setCompoundTag(compoundTag);
        this.storage = storage;
    }

    @Override
    public void write() {
        if (this.getReadStorage().isEmpty()) {
            this.clearStorage();
        } else {
            this.setStorage(this.getStorage());
        }
    }

    @Override
    public void setConvertsToBlock(boolean convertsToBlock) {
        this.convertsToBlock = convertsToBlock;
    }

    @Override
    public boolean convertsToBlock() {
        return this.convertsToBlock;
    }

    @Override
    public void clearStorage() {
        if (this.item.hasCompoundTag()) {
            CompoundTag compoundTag = this.item.getNamedTag();
            compoundTag.remove(STORAGE_TAG);
            this.item.setCompoundTag(compoundTag);
        }
        this.storage = null;
    }
}
