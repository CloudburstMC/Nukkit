package com.nukkitx.server.metadata.serializer.block.entity;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.blockentity.BlockEntity;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.nbt.tag.ListTag;
import com.nukkitx.server.block.entity.NukkitChestBlockEntity;
import com.nukkitx.server.container.ContainerType;
import com.nukkitx.server.container.NukkitContainer;
import com.nukkitx.server.container.NukkitFillingContainer;
import com.nukkitx.server.item.ItemUtils;
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
    public CompoundTag readNBT(ItemStack item) {
        return read(item.ensureMetadata(NukkitChestBlockEntity.class));
    }

    protected CompoundTag read(NukkitChestBlockEntity entity) {
        CompoundTagBuilder builder = CompoundTagBuilder.builder()
                .listTag("Items", CompoundTag.class, ItemUtils.createNBT(entity.getContainer().getContents()));

        entity.getLock().ifPresent(lock -> builder.stringTag("Lock", lock));

        entity.getCustomName().ifPresent(name -> builder.stringTag("CustomName", name));

        return builder.buildRootTag();
    }

    @Override
    public BlockEntity writeNBT(ItemType block, CompoundTag nbtTag) {
        NukkitContainer container = new NukkitFillingContainer(null, 27, ContainerType.CHEST);

        ListTag<CompoundTag> listTag = nbtTag.getAs("Items", ListTag.class);

        ItemStack[] items = ItemUtils.createItemStacks(listTag, container.getSize());
        for (int i = 0, len = container.getSize(); i < len; i++) {
            container.setItem(i, items[i], null);
        }

        NukkitChestBlockEntity blockEntity = new NukkitChestBlockEntity(container);

        nbtTag.listen("Lock", String.class, blockEntity::setLock);
        nbtTag.listen("CustomName", String.class, blockEntity::setCustomName);

        return blockEntity;
    }
}
