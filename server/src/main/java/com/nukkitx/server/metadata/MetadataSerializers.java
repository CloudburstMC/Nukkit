package com.nukkitx.server.metadata;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.blockentity.BlockEntity;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.server.metadata.serializer.Serializer;
import com.nukkitx.server.metadata.serializer.block.SimpleDirectionalSerializer;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MetadataSerializers {

    private static final TIntObjectHashMap<Serializer> SERIALIZERS = new TIntObjectHashMap<>();

    static {
        SERIALIZERS.put(BlockTypes.FURNACE.getId(), SimpleDirectionalSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.BURNING_FURNACE.getId(), SimpleDirectionalSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.CHEST.getId(), SimpleDirectionalSerializer.INSTANCE);
    }

    public static Metadata deserializeMetadata(ItemType type, short metadata) {
        Serializer serializer = SERIALIZERS.get(type.getId());
        if (serializer == null) {
            return null;
        }

        return serializer.writeMetadata(type, metadata);
    }

    public static BlockEntity deserializeNBT(ItemType type, CompoundTag tag) {
        Serializer serializer = SERIALIZERS.get(type.getId());
        if (serializer == null) {
            return null;
        }

        return serializer.writeNBT(type, tag);
    }

    public static short serializeMetadata(BlockState block) {
        Serializer dataSerializer = SERIALIZERS.get(block.getBlockType().getId());
        if (dataSerializer == null) {
            return 0;
        }

        return dataSerializer.readMetadata(block);
    }

    public static short serializeMetadata(ItemInstance itemStack) {
        Serializer dataSerializer = SERIALIZERS.get(itemStack.getItemType().getId());
        if (dataSerializer == null) {
            return 0;
        }

        return dataSerializer.readMetadata(itemStack);
    }

    public static CompoundTag serializeNBT(BlockState block) {
        Serializer dataSerializer = SERIALIZERS.get(block.getBlockType().getId());
        if (dataSerializer == null) {
            return null;
        }

        return dataSerializer.readNBT(block);
    }
}