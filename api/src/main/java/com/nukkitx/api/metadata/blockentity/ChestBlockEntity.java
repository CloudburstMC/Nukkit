package com.nukkitx.api.metadata.blockentity;

import com.nukkitx.api.container.Container;
import com.nukkitx.api.container.ContainerHolder;

public interface ChestBlockEntity extends NameableBlockEntity, ContainerHolder {

    boolean isTrapped();

    @Override
    Container getContainer();

    boolean isLargeChest();
}
