package cn.nukkit.server.metadata.serializer;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.api.metadata.blockentity.BlockEntity;
import cn.nukkit.api.metadata.item.Coal;
import cn.nukkit.server.nbt.tag.CompoundTag;

import static cn.nukkit.api.metadata.item.Coal.CHARCOAL;
import static cn.nukkit.api.metadata.item.Coal.REGULAR;

public class CoalSerializer implements Serializer {
    @Override
    public CompoundTag readNBT(BlockState state) {
        return null;
    }

    @Override
    public short readMetadata(BlockState state) {
        return 0;
    }

    @Override
    public CompoundTag readNBT(ItemInstance item) {
        return null;
    }

    @Override
    public short readMetadata(ItemInstance item) {
        Coal coal = getItemData(item);
        return (short) coal.ordinal();
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        return metadata != 0 ? CHARCOAL : REGULAR;
    }

    @Override
    public BlockEntity writeNBT(ItemType type, CompoundTag nbtTag) {
        return null;
    }
}
