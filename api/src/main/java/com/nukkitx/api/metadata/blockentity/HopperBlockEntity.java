package com.nukkitx.api.metadata.blockentity;

import com.nukkitx.api.container.ContainerHolder;
import com.nukkitx.api.inventory.Inventory;

public interface HopperBlockEntity extends BlockEntity, ContainerHolder {

    @Override
    Inventory getContainer();
}
