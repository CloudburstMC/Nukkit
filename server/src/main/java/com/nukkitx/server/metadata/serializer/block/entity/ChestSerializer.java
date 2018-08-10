package com.nukkitx.server.metadata.serializer.block.entity;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.blockentity.BlockEntity;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.server.block.entity.NukkitChestBlockEntity;
import com.nukkitx.server.inventory.NukkitInventory;
import com.nukkitx.server.inventory.NukkitInventoryType;
import com.nukkitx.server.item.ItemUtil;
import com.nukkitx.server.metadata.serializer.Serializer;

/**
 * @author CreeperFace
 */
public class ChestSerializer implements Serializer {

    @Override
    public CompoundTag readNBT(BlockState state) {
        return read(state.ensureBlockEntity(NukkitChestBlockEntity.class));
    }

    @Override
    public CompoundTag readNBT(ItemInstance item) {
        return read(item.ensureMetadata(NukkitChestBlockEntity.class));
    }

    protected CompoundTag read(NukkitChestBlockEntity entity) {
        CompoundTagBuilder builder = CompoundTagBuilder.builder()
                .listTag("Items", CompoundTag.class, ItemUtil.createNBT(entity.getInventory().getAllContents()));

        entity.getLock().ifPresent(lock -> builder.stringTag("Lock", lock));

        entity.getCustomName().ifPresent(name -> builder.stringTag("CustomName", name));

        return builder.buildRootTag();
    }

    @Override
    public BlockEntity writeNBT(ItemType block, CompoundTag nbtTag) {
        NukkitInventory inventory = new NukkitInventory(NukkitInventoryType.CHEST);
        nbtTag.getAsList("Items", CompoundTag.class).ifPresent(items -> inventory.setAllContents(ItemUtil.createItemStacks(items, inventory.getInventorySize())));

        NukkitChestBlockEntity blockEntity = new NukkitChestBlockEntity(inventory);

        nbtTag.getAsString("Lock").ifPresent(val -> blockEntity.setLock(val.getValue()));
        nbtTag.getAsString("CustomName").ifPresent(val -> blockEntity.setCustomName(val.getValue()));

        return blockEntity;
    }
}
