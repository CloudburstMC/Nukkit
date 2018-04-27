package cn.nukkit.server.metadata.serializer;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.api.metadata.blockentity.BlockEntity;
import cn.nukkit.api.metadata.data.PlantType;
import cn.nukkit.server.block.entity.NukkitFlowerPotBlockEntity;
import cn.nukkit.server.nbt.CompoundTagBuilder;
import cn.nukkit.server.nbt.tag.CompoundTag;

public class FlowerPotSerializer implements Serializer {
    @Override
    public CompoundTag readNBT(BlockState state) {
        NukkitFlowerPotBlockEntity blockEntity = getBlockEntity(state);
        return CompoundTagBuilder.builder()
                .stringTag("contents", blockEntity.getPlantType().name().toLowerCase())
                .buildRootTag();
    }

    @Override
    public short readMetadata(BlockState state) {
        return 0;
    }

    @Override
    public CompoundTag readNBT(ItemInstance item) {
        NukkitFlowerPotBlockEntity blockEntity = getItemData(item);
        return CompoundTagBuilder.builder()
                .stringTag("contents", blockEntity.getPlantType().name().toLowerCase())
                .buildRootTag();
    }

    @Override
    public short readMetadata(ItemInstance item) {
        return 0;
    }

    @Override
    public Metadata writeMetadata(ItemType block, short metadata) {
        return null;
    }

    @Override
    public BlockEntity writeNBT(ItemType block, CompoundTag nbtTag) {
        PlantType type = PlantType.valueOf((String) nbtTag.get("contents").getValue());
        return new NukkitFlowerPotBlockEntity(type);
    }
}
