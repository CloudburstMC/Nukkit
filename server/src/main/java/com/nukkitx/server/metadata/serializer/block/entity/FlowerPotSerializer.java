package com.nukkitx.server.metadata.serializer.block.entity;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.blockentity.BlockEntity;
import com.nukkitx.api.metadata.data.PlantType;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.server.block.entity.NukkitFlowerPotBlockEntity;
import com.nukkitx.server.metadata.serializer.NBTSerializer;

public class FlowerPotSerializer implements NBTSerializer {

    @Override
    public CompoundTag readNBT(BlockState state) {
        NukkitFlowerPotBlockEntity blockEntity = state.ensureBlockEntity(NukkitFlowerPotBlockEntity.class);
        return CompoundTagBuilder.builder()
                .stringTag("contents", blockEntity.getPlantType().name().toLowerCase())
                .buildRootTag();
    }

    @Override
    public CompoundTag readNBT(ItemInstance item) {
        NukkitFlowerPotBlockEntity blockEntity = item.ensureMetadata(NukkitFlowerPotBlockEntity.class);
        return CompoundTagBuilder.builder()
                .stringTag("contents", blockEntity.getPlantType().name().toLowerCase())
                .buildRootTag();
    }

    @Override
    public BlockEntity writeNBT(ItemType block, CompoundTag nbtTag) {
        PlantType type = PlantType.valueOf((String) nbtTag.get("contents").getValue());

        return new NukkitFlowerPotBlockEntity(type);
    }
}
