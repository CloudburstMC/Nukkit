package com.nukkitx.api.metadata.blockentity;

import com.nukkitx.api.container.ContainerHolder;
import com.nukkitx.api.inventory.Inventory;
import com.nukkitx.api.item.ItemStack;

import javax.annotation.Nullable;

public interface FurnaceBlockEntity extends NameableBlockEntity, ContainerHolder {

    boolean isFuel(@Nullable ItemStack item);

    int getBurnDuration(@Nullable ItemStack item);

    boolean isIngredient(@Nullable ItemStack item);

    int getCookTime();

    int getBurnTime();

    @Override
    Inventory getContainer();
}
