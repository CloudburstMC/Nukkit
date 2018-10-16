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
        SERIALIZERS.put(BlockTypes.NETHER_WART.getId(), NetherWartSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.PISTON.getId(), PistonSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.STICKY_PISTON.getId(), PistonSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.WOODEN_PRESSURE_PLATE.getId(), PressurePlateSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.WEIGHTED_PRESSURE_PLATE_LIGHT.getId(), WeightedPressurePlateSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.WEIGHTED_PRESSURE_PLATE_HEAVY.getId(), WeightedPressurePlateSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.STONE_PRESSURE_PLATE.getId(), PressurePlateSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.SPRUCE_PRESSURE_PLATE.getId(), PressurePlateSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.JUNGLE_PRESSURE_PLATE.getId(), PressurePlateSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.DARK_OAK_PRESSURE_PLATE.getId(), PressurePlateSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.BIRCH_PRESSURE_PLATE.getId(), PressurePlateSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.ACACIA_PRESSURE_PLATE.getId(), PressurePlateSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.PUMPKIN.getId(), SimpleDirectionalSerializer.INSTANCE_2);
        SERIALIZERS.put(BlockTypes.CARVED_PUMPKIN.getId(), SimpleDirectionalSerializer.INSTANCE_2);
        SERIALIZERS.put(BlockTypes.JACK_OLANTERN.getId(), SimpleDirectionalSerializer.INSTANCE_2);
        SERIALIZERS.put(BlockTypes.SAND.getId(), SandSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.SANDSTONE.getId(), SandstoneSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.RED_SANDSTONE.getId(), SandstoneSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.JUNGLE_WOOD_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.DARK_OAK_WOOD_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.BIRCH_WOOD_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.SPRUCE_WOOD_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.ACACIA_WOOD_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.OAK_WOOD_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.SANDSTONE_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.STONE_BRICK_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.BRICK_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.COBBLESTONE_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.DARK_PRISMARINE_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.NETHER_BRICK_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.PRISMARINE_BRICKS_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.PRISMARINE_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.PURPUR_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.QUARTZ_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.RED_SANDSTONE_STAIRS.getId(), StairsSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.STONE_SLAB.getId(), StoneSlabSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.RED_SANDSTONE_SLAB.getId(), StoneSlab2Serializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.WOODEN_SLAB.getId(), WoodSlabSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.TOP_SNOW.getId(), TopSnowSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.TRAPDOOR.getId(), TrapdoorSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.ACACIA_TRAPDOOR.getId(), TrapdoorSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.BIRCH_TRAPDOOR.getId(), TrapdoorSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.DARK_OAK_TRAPDOOR.getId(), TrapdoorSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.IRON_TRAPDOOR.getId(), TrapdoorSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.JUNGLE_TRAPDOOR.getId(), TrapdoorSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.SPRUCE_TRAPDOOR.getId(), TrapdoorSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.REDSTONE_COMPARATOR_POWERED.getId(), RedstoneComparatorSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.REDSTONE_COMPARATOR_UNPOWERED.getId(), RedstoneComparatorSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.REDSTONE_REPEATER.getId(), RedstoneRepeaterSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.REDSTONE_REPEATER_ACTIVE.getId(), RedstoneRepeaterSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.HOPPER.getId(), DirectionalPowerableSerializer.INSTANCE_1);
        SERIALIZERS.put(BlockTypes.DROPPER.getId(), DirectionalPowerableSerializer.INSTANCE_1);
        SERIALIZERS.put(BlockTypes.DISPENSER.getId(), DirectionalPowerableSerializer.INSTANCE_1);
        SERIALIZERS.put(BlockTypes.SPRUCE_BUTTON.getId(), DirectionalPowerableSerializer.INSTANCE_3);
        SERIALIZERS.put(BlockTypes.JUNGLE_BUTTON.getId(), DirectionalPowerableSerializer.INSTANCE_3);
        SERIALIZERS.put(BlockTypes.DARK_OAK_BUTTON.getId(), DirectionalPowerableSerializer.INSTANCE_3);
        SERIALIZERS.put(BlockTypes.ACACIA_BUTTON.getId(), DirectionalPowerableSerializer.INSTANCE_3);
        SERIALIZERS.put(BlockTypes.BIRCH_BUTTON.getId(), DirectionalPowerableSerializer.INSTANCE_3);
        SERIALIZERS.put(BlockTypes.WOODEN_BUTTON.getId(), DirectionalPowerableSerializer.INSTANCE_3);
        SERIALIZERS.put(BlockTypes.STONE_BUTTON.getId(), DirectionalPowerableSerializer.INSTANCE_3);
        SERIALIZERS.put(BlockTypes.TORCH.getId(), TorchSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.REDSTONE_TORCH.getId(), TorchSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.REDSTONE_TORCH_ACTIVE.getId(), TorchSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.DAYLIGHT_SENSOR.getId(), DaylightSensorSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.INVERTED_DAYLIGHT_SENSOR.getId(), DaylightSensorSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.WALL_BANNER.getId(), SimpleDirectionalSerializer.INSTANCE_1);
        SERIALIZERS.put(BlockTypes.STANDING_BANNER.getId(), CardinalDirectionalSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.FENCE_GATE.getId(), FenceGateSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.SPRUCE_FENCE_GATE.getId(), FenceGateSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.JUNGLE_FENCE_GATE.getId(), FenceGateSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.DARK_OAK_FENCE_GATE.getId(), FenceGateSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.BIRCH_FENCE_GATE.getId(), FenceGateSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.ACACIA_FENCE_GATE.getId(), FenceGateSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.ITEM_FRAME.getId(), SimpleDirectionalSerializer.INSTANCE_4);
        SERIALIZERS.put(BlockTypes.PORTAL.getId(), PortalSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.CACTUS.getId(), CactusSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.SUGAR_CANE.getId(), SugarCaneSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.RAIL.getId(), RailSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.ACTIVATOR_RAIL.getId(), ActivableRailSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.DETECTOR_RAIL.getId(), ActivableRailSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.POWERED_RAIL.getId(), ActivableRailSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.OBSERVER.getId(), DirectionalPowerableSerializer.INSTANCE_2);
        SERIALIZERS.put(BlockTypes.UNDERWATER_TORCH.getId(), TorchSerializer.INSTANCE); //check
        SERIALIZERS.put(BlockTypes.COLORED_TORCH_BP.getId(), TorchSerializer.INSTANCE); //check
        SERIALIZERS.put(BlockTypes.COLORED_TORCH_RG.getId(), TorchSerializer.INSTANCE); //check
        SERIALIZERS.put(BlockTypes.END_ROD.getId(), DirectionalSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.SHULKER_BOX.getId(), DyedSerializer.INSTANCE);
//        SERIALIZERS.put(BlockTypes.PISTON_HEAD.getId(), PistonHeadSerializer.INSTANCE); //TODO
        SERIALIZERS.put(BlockTypes.TRIPWIRE_HOOK.getId(), TripwireHookSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.SIGN.getId(), CardinalDirectionalSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.WALL_SIGN.getId(), SimpleDirectionalSerializer.INSTANCE_1);
        SERIALIZERS.put(BlockTypes.LADDER.getId(), SimpleDirectionalSerializer.INSTANCE);
        SERIALIZERS.put(BlockTypes.VINES.getId(), SimpleDirectionalSerializer.INSTANCE);
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