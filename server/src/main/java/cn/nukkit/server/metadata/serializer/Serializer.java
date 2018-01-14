package cn.nukkit.server.metadata.serializer;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.block.entity.BlockEntity;
import cn.nukkit.api.item.ItemStack;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.server.nbt.tag.CompoundTag;

import java.util.Optional;

public interface Serializer {
    CompoundTag readNBT(BlockState block);

    short readMetadata(BlockState block);

    CompoundTag readNBT(ItemStack itemStack);

    short readMetadata(ItemStack itemStack);

    Metadata writeMetadata(ItemType block, short metadata);

    BlockEntity writeNBT(ItemType block, CompoundTag nbtTag);

    default <T> T getItemData(ItemStack itemStack) {
        Optional<Metadata> optional = itemStack.getItemData();
        return (T) optional.orElse(null);
    }

    default <T> T getBlockData(BlockState block) {
        return (T) block.getBlockData();
    }

    default <T> T getBlockStateEntity(BlockState block) {
        Optional<BlockEntity> optional = block.getBlockEntity();
        return (T) optional.orElse(null);
    }
}
