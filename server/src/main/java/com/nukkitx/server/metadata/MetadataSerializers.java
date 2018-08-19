package com.nukkitx.server.metadata;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.item.ItemTypes;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.blockentity.BlockEntity;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.server.metadata.serializer.MetadataSerializer;
import com.nukkitx.server.metadata.serializer.NBTSerializer;
import com.nukkitx.server.metadata.serializer.block.*;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MetadataSerializers {

    private static final TIntObjectHashMap<MetadataSerializer> META_SERIALIZERS = new TIntObjectHashMap<>();
    private static final TIntObjectHashMap<NBTSerializer> NBT_SERIALIZERS = new TIntObjectHashMap<>();

    static {
        META_SERIALIZERS.put(BlockTypes.FURNACE.getId(), SimpleDirectionalSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.BURNING_FURNACE.getId(), SimpleDirectionalSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.CHEST.getId(), SimpleDirectionalSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.CAKE.getId(), CakeSerializer.INSTANCE);
        META_SERIALIZERS.put(ItemTypes.COAL.getId(), CoalSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.CROPS.getId(), CropsSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.STONE.getId(), StoneSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.TALL_GRASS.getId(), TallGrassSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.PUMPKIN_STEM.getId(), CropsSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.MELON_STEM.getId(), CropsSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.CARROTS.getId(), CropsSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.POTATO.getId(), CropsSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.BEETROOT.getId(), CropsSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.BED.getId(), BedSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.WOOL.getId(), DyedSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.COLORED_TERRACOTTA.getId(), DyedSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.STAINED_GLASS_PANE.getId(), DyedSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.CARPET.getId(), DyedSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.HARD_STAINED_GLASS_PANE.getId(), DyedSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.SHULKER_BOX.getId(), DyedSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.STAINED_GLASS.getId(), DyedSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.HARD_STAINED_GLASS.getId(), DyedSerializer.INSTANCE);
        META_SERIALIZERS.put(BlockTypes.ANVIL.getId(), AnvilSerializer.INSTANCE);

//        META_SERIALIZERS.put(BlockTypes..getId(), .INSTANCE); template
//        META_SERIALIZERS.put(ItemTypes..getId(), .INSTANCE); template
    }

    public static Metadata deserializeMetadata(ItemType type, short metadata) {
        MetadataSerializer serializer = META_SERIALIZERS.get(type.getId());
        if (serializer == null) {
            return null;
        }

        return serializer.writeMetadata(type, metadata);
    }

    public static BlockEntity deserializeNBT(ItemType type, CompoundTag tag) {
        NBTSerializer serializer = NBT_SERIALIZERS.get(type.getId());
        if (serializer == null) {
            return null;
        }

        return serializer.writeNBT(type, tag);
    }

    @SuppressWarnings("unchecked")
    public static short serializeMetadata(BlockState block) {
        MetadataSerializer dataSerializer = META_SERIALIZERS.get(block.getBlockType().getId());
        if (dataSerializer == null) {
            return 0;
        }

        try {
            return dataSerializer.readMetadata(block.getMetadata().orElseThrow(() -> new NullPointerException("BlockState instance has no data")));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid class supplied", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static short serializeMetadata(ItemInstance itemStack) {
        MetadataSerializer dataSerializer = META_SERIALIZERS.get(itemStack.getItemType().getId());
        if (dataSerializer == null) {
            return 0;
        }

        try {
            return dataSerializer.readMetadata(itemStack.getMetadata().orElseThrow(() -> new NullPointerException("BlockState instance has no data")));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid class supplied", e);
        }
    }

    public static CompoundTag serializeNBT(BlockState block) {
        NBTSerializer dataSerializer = NBT_SERIALIZERS.get(block.getBlockType().getId());
        if (dataSerializer == null) {
            return null;
        }

        return dataSerializer.readNBT(block);
    }
}