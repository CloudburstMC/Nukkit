package com.nukkitx.server.block.behavior;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.nukkitx.api.block.BlockType;
import com.nukkitx.api.block.BlockTypes;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class BlockBehaviors {
    private static final Map<BlockType, BlockBehavior> BLOCK_BEHAVIORS;

    static {
        BLOCK_BEHAVIORS = ImmutableMap.<BlockType, BlockBehavior>builder()
                .put(BlockTypes.DIRT, DirtBlockBehavior.INSTANCE)
                .put(BlockTypes.GRASS_BLOCK, DirtBlockBehavior.INSTANCE)
                .put(BlockTypes.COAL_ORE, CoalOreBlockBehavior.INSTANCE)
                .put(BlockTypes.DIAMOND_BLOCK, DiamondOreBlockBehavior.INSTANCE)
                .put(BlockTypes.EMERALD_ORE, EmeraldOreBlockBehavior.INSTANCE)
                .put(BlockTypes.GOLD_ORE, GoldOreBlockBehavior.INSTANCE)
                .put(BlockTypes.IRON_ORE, IronOreBlockBehavior.INSTANCE)
                .put(BlockTypes.LAPIS_LAZULI_ORE, LapisLazuliOreBlockBehavior.INSTANCE)
                .put(BlockTypes.NETHER_QUARTZ_ORE, NetherQuartzOreBlockBehavior.INSTANCE)
                .put(BlockTypes.REDSTONE_ORE, RedstoneOreBlockBehavior.INSTANCE)
                .build();
    }

    public static BlockBehavior getBlockBehavior(BlockType type) {
        Preconditions.checkNotNull(type, "type");
        BlockBehavior behavior = BLOCK_BEHAVIORS.get(type);
        return behavior == null ? SimpleBlockBehavior.INSTANCE : behavior;
    }
}
