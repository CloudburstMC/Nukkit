package com.nukkitx.server.metadata;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.item.ItemTypes;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.Metadatable;
import com.nukkitx.api.metadata.blockentity.BlockEntity;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.server.metadata.serializer.Serializer;
import com.nukkitx.server.metadata.serializer.block.*;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class MetadataSerializers {

    private static final TIntObjectHashMap<Serializer> SERIALIZERS = new TIntObjectHashMap<>();

    static {
        SERIALIZERS.put(BlockTypes.FURNACE.getId(), SimpleDirectionalSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.BURNING_FURNACE.getId(), SimpleDirectionalSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.CHEST.getId(), SimpleDirectionalSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.CAKE.getId(), CakeSerializer.INSTANCE);
        SERIALIZERS.put(ItemTypes.COAL.getId(), CoalSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.CROPS.getId(), CropsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.STONE.getId(), StoneSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.TALL_GRASS.getId(), TallGrassSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.PUMPKIN_STEM.getId(), CropsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.MELON_STEM.getId(), CropsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.CARROTS.getId(), CropsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.POTATO.getId(), CropsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.BEETROOT.getId(), CropsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.BED.getId(), BedSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.WOOL.getId(), DyedSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.COLORED_TERRACOTTA.getId(), DyedSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.STAINED_GLASS_PANE.getId(), DyedSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.CARPET.getId(), DyedSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.HARD_STAINED_GLASS_PANE.getId(), DyedSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.SHULKER_BOX.getId(), DyedSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.STAINED_GLASS.getId(), DyedSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.HARD_STAINED_GLASS.getId(), DyedSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.ANVIL.getId(), AnvilSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.END_PORTAL_FRAME.getId(), SimpleDirectionalSerializer.INSTANCE_1);
        SERIALIZERS.put(BlockTypes.FARMLAND.getId(), FarmlandSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.FLOWER.getId(), FlowerSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.JUKEBOX.getId(), JukeboxSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.LEAVES.getId(), LeavesSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.LEAVES2.getId(), Leaves2Serializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.LEVER.getId(), LeverSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.WATER.getId(), LiquidSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.STATIONARY_WATER.getId(), LiquidSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.LAVA.getId(), LiquidSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.STATIONARY_LAVA.getId(), LiquidSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.WOOD.getId(), LogSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.WOOD2.getId(), LogSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.MONSTER_EGG.getId(), MonsterEggSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.STONE_BRICK.getId(), StoneBrickSerializer.INSTANCE);

//        SERIALIZERS.put(BlockTypes..getId(), .INSTANCE); template
//        SERIALIZERS.put(ItemTypes..getId(), .INSTANCE); template
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
        return serializeMetadata(block.getBlockType(), block);
    }

    public static short serializeMetadata(ItemInstance itemStack) {
        return serializeMetadata(itemStack.getItemType(), itemStack);
    }

    @SuppressWarnings("unchecked")
    public static short serializeMetadata(ItemType type, Metadatable metadatable) {
        Serializer dataSerializer = SERIALIZERS.get(type.getId());
        if (dataSerializer == null) {
            return 0;
        }

        try {
            return dataSerializer.readMetadata(metadatable.getMetadata().orElseThrow(() -> new NullPointerException(Objects.toString(metadatable) + " instance has no data")));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid class supplied", e);
        }
    }

    public static CompoundTag serializeNBT(BlockState block) {
        Serializer dataSerializer = SERIALIZERS.get(block.getBlockType().getId());
        if (dataSerializer == null) {
            return null;
        }

        return dataSerializer.readNBT(block);
    }
}