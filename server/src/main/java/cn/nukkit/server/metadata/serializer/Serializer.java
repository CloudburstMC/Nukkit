package cn.nukkit.server.metadata.serializer;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.block.entity.BlockEntity;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.server.nbt.tag.CompoundTag;

import java.util.Optional;

@SuppressWarnings("unchecked")
public interface Serializer {
    CompoundTag readNBT(BlockState block);

    short readMetadata(BlockState block);

    CompoundTag readNBT(ItemInstance item);

    short readMetadata(ItemInstance item);

    Metadata writeMetadata(ItemType type, short metadata);

    BlockEntity writeNBT(ItemType type, CompoundTag nbtTag);

    default <T> T getItemDataOrNull(ItemInstance item) {
        return (T) item.getItemData().orElse(null);
    }

    default <T> Optional<T> getItemData(ItemInstance item) {
        return (Optional<T>) item.getItemData();
    }

    default <T> T getBlockData(BlockState block) {
        return (T) block.getBlockData();
    }

    default <T> T getBlockStateEntityOrNull(BlockState block) {
        return (T) block.getBlockEntity().orElse(null);
    }

    default <T> Optional<T> getBlockStateEntity(BlockState block) {
        return (Optional<T>) block.getBlockEntity();
    }
}