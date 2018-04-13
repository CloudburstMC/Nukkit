package cn.nukkit.server.metadata;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.block.entity.BlockEntity;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.server.metadata.serializer.MetadataSerializer;
import cn.nukkit.server.nbt.tag.CompoundTag;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MetadataSerializers {
    private static final TIntObjectHashMap<MetadataSerializer> SERIALIZERS = new TIntObjectHashMap<>();

    static {
    }

    public static Metadata deserializeMetadata(ItemType type, short metadata) {
        MetadataSerializer serializer = SERIALIZERS.get(type.getId());
        if (serializer == null) {
            return null;
        }

        return serializer.writeMetadata(type, metadata);
    }

    public static BlockEntity deserializeNBT(ItemType type, CompoundTag tag) {
        MetadataSerializer serializer = SERIALIZERS.get(type.getId());
        if (serializer == null) {
            return null;
        }

        return serializer.writeNBT(type, tag);
    }

    public static short serializeMetadata(BlockState block) {
        MetadataSerializer dataSerializer = SERIALIZERS.get(block.getBlockType().getId());
        if (dataSerializer == null) {
            return 0;
        }

        return dataSerializer.readMetadata(block);
    }

    public static short serializeMetadata(ItemInstance itemStack) {
        MetadataSerializer dataSerializer = SERIALIZERS.get(itemStack.getItemType().getId());
        if (dataSerializer == null) {
            return 0;
        }

        return dataSerializer.readMetadata(itemStack);
    }

    public static CompoundTag serializeNBT(BlockState block) {
        MetadataSerializer dataSerializer = SERIALIZERS.get(block.getBlockType().getId());
        if (dataSerializer == null) {
            return null;
        }

        return dataSerializer.readNBT(block);
    }
}