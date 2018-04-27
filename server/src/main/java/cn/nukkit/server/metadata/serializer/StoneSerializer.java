package cn.nukkit.server.metadata.serializer;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.api.metadata.block.Stone;
import cn.nukkit.api.metadata.blockentity.BlockEntity;
import cn.nukkit.server.nbt.tag.CompoundTag;

public class StoneSerializer implements Serializer {
    @Override
    public CompoundTag readNBT(BlockState block) {
        return null;
    }

    @Override
    public short readMetadata(BlockState block) {
        Stone stone = getBlockData(block);
        return (short) stone.ordinal();
    }

    @Override
    public CompoundTag readNBT(ItemInstance item) {
        return null;
    }

    @Override
    public short readMetadata(ItemInstance item) {
        Stone stone = getItemData(item);
        return (short) stone.ordinal();
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        if (metadata < 0 || metadata >= Stone.values().length) {
            metadata = 0;
        }
        return Stone.values()[metadata];
    }

    @Override
    public BlockEntity writeNBT(ItemType type, CompoundTag nbtTag) {
        return null;
    }
}
