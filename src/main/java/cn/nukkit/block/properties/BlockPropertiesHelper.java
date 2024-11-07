package cn.nukkit.block.properties;

import cn.nukkit.block.custom.container.BlockStorageContainer;

public interface BlockPropertiesHelper extends BlockStorageContainer {

    int getId();

    int getDamage();
    void setDamage(int meta);

    @Override
    default int getStorage() {
        return this.getDamage();
    }

    @Override
    default void setStorage(int damage) {
        this.setDamage(damage);
    }

    @Override
    default int getNukkitId() {
        return this.getId();
    }
}
