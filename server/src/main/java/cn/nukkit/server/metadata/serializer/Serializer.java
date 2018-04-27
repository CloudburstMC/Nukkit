package cn.nukkit.server.metadata.serializer;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.api.metadata.blockentity.BlockEntity;
import cn.nukkit.server.nbt.tag.CompoundTag;

@SuppressWarnings("unchecked")
public interface Serializer {
    CompoundTag readNBT(BlockState state);

    short readMetadata(BlockState state);

    CompoundTag readNBT(ItemInstance item);

    short readMetadata(ItemInstance item);

    Metadata writeMetadata(ItemType type, short metadata);

    BlockEntity writeNBT(ItemType type, CompoundTag nbtTag);

    default <T extends Metadata> T getBlockData(BlockState state) {
        return (T) state.getBlockData().get();
    }

    default <T extends BlockEntity> T getBlockEntity(BlockState state) {
        return (T) state.getBlockEntity().get();
    }

    default <T extends Metadata> T getItemData(ItemInstance item) {
        return (T) item.getItemData().get();
    }
}