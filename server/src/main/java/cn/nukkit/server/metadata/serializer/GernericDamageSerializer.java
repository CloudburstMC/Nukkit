package cn.nukkit.server.metadata.serializer;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.api.metadata.blockentity.BlockEntity;
import cn.nukkit.api.metadata.item.GenericDamageValue;
import cn.nukkit.server.nbt.tag.CompoundTag;

import java.util.Optional;

public class GernericDamageSerializer implements MetadataSerializer {

    @Override
    public CompoundTag readNBT(BlockState block) {
        return null;
    }

    @Override
    public short readMetadata(BlockState block) {
        return 0;
    }

    @Override
    public CompoundTag readNBT(ItemInstance item) {
        return null;
    }

    @Override
    public short readMetadata(ItemInstance item) {
        Optional<GenericDamageValue> optional = getItemData(item);
        return optional.map(GenericDamageValue::getDamage).orElse((short) 0);
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        return metadata == 0 ? null : new GenericDamageValue(metadata);
    }

    @Override
    public BlockEntity writeNBT(ItemType type, CompoundTag nbtTag) {
        return null;
    }
}
