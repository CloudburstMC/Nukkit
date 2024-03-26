package cn.nukkit.level.persistence.impl;

import cn.nukkit.level.persistence.ImmutableCompoundTag;
import cn.nukkit.level.persistence.PersistentDataContainer;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class DelegatePersistentDataContainer implements PersistentDataContainer {

    private PersistentDataContainer delegate;

    protected abstract PersistentDataContainer createDelegate();

    protected final PersistentDataContainer getDelegate() {
        if (this.delegate == null) {
            this.delegate = this.createDelegate();
        }
        return this.delegate;
    }

    @Override
    public CompoundTag getStorage() {
        return this.getDelegate().getStorage();
    }

    @Override
    public void setStorage(CompoundTag storage) {
        this.getDelegate().setStorage(storage);
    }

    @Override
    public CompoundTag getReadStorage() {
        return this.delegate == null ? ImmutableCompoundTag.EMPTY : this.getStorage();
    }
}
