package cn.nukkit.server.metadata.serializer;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.api.metadata.block.Cake;
import cn.nukkit.api.metadata.blockentity.BlockEntity;
import cn.nukkit.server.nbt.tag.CompoundTag;

public class CakeSerializer implements Serializer {
    @Override
    public CompoundTag readNBT(BlockState state) {
        return null;
    }

    @Override
    public short readMetadata(BlockState state) {
        Cake cake = getBlockData(state);
        return cake.getLevel();
    }

    @Override
    public CompoundTag readNBT(ItemInstance item) {
        return null;
    }

    @Override
    public short readMetadata(ItemInstance item) {
        return 0;
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        return Cake.of(metadata);
    }

    @Override
    public BlockEntity writeNBT(ItemType type, CompoundTag nbtTag) {
        return null;
    }
}
