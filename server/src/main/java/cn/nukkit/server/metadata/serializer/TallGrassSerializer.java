package cn.nukkit.server.metadata.serializer;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.api.metadata.block.TallGrass;
import cn.nukkit.api.metadata.blockentity.BlockEntity;
import cn.nukkit.server.nbt.tag.CompoundTag;
import com.google.common.base.Preconditions;

public class TallGrassSerializer implements Serializer {
    private static final TallGrass[] VALUES = TallGrass.values();
    @Override
    public CompoundTag readNBT(BlockState state) {
        return null;
    }

    @Override
    public short readMetadata(BlockState state) {
        TallGrass grass = getBlockData(state);
        return (short) grass.ordinal();
    }

    @Override
    public CompoundTag readNBT(ItemInstance item) {
        return null;
    }

    @Override
    public short readMetadata(ItemInstance item) {
        TallGrass grass = getItemData(item);
        return (short) grass.ordinal();
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        Preconditions.checkArgument(metadata >= 0 && metadata < VALUES.length);
        return VALUES[metadata];
    }

    @Override
    public BlockEntity writeNBT(ItemType type, CompoundTag nbtTag) {
        return null;
    }
}
