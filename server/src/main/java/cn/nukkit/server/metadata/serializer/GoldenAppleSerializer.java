package cn.nukkit.server.metadata.serializer;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.api.metadata.blockentity.BlockEntity;
import cn.nukkit.api.metadata.item.GoldenApple;
import cn.nukkit.server.nbt.tag.CompoundTag;

import static cn.nukkit.api.metadata.item.GoldenApple.ENCHANTED;
import static cn.nukkit.api.metadata.item.GoldenApple.REGULAR;

public class GoldenAppleSerializer implements Serializer {
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
        GoldenApple goldenApple = getItemData(item);
        return (short) (goldenApple.isEnchanted() ? 1 : 0);
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        return metadata != 0 ? ENCHANTED : REGULAR;
    }

    @Override
    public BlockEntity writeNBT(ItemType type, CompoundTag nbtTag) {
        return null;
    }
}
