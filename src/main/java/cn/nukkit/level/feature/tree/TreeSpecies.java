package cn.nukkit.level.feature.tree;

import cn.nukkit.block.BlockHugeMushroomBrown;
import cn.nukkit.block.BlockHugeMushroomRed;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockLeaves2;
import cn.nukkit.block.BlockLog;
import cn.nukkit.block.BlockLog2;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.feature.WorldFeature;
import cn.nukkit.utils.Identifier;
import lombok.RequiredArgsConstructor;

/**
 * The different tree varieties in Minecraft.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public enum TreeSpecies {
    OAK(BlockIds.LOG, BlockLog.OAK, BlockIds.LEAVES, BlockLeaves.OAK, BlockIds.SAPLING, BlockSapling.OAK) {
        @Override
        public WorldFeature getDefaultGenerator() {
            return new FeatureLargeOakTree(FeatureNormalTree.DEFAULT_HEIGHT, this, 0.1d, FeatureLargeOakTree.DEFAULT_HEIGHT);
        }
    },
    SWAMP(BlockIds.LOG, BlockLog.OAK, BlockIds.LEAVES, BlockLeaves.OAK, null, -1) {
        @Override
        public WorldFeature getDefaultGenerator() {
            return new FeatureSwampTree(FeatureSwampTree.DEFAULT_HEIGHT, OAK);
        }
    },
    SPRUCE(BlockIds.LOG, BlockLog.SPRUCE, BlockIds.LEAVES, BlockLeaves.SPRUCE, BlockIds.SAPLING, BlockSapling.SPRUCE) {
        @Override
        public WorldFeature getDefaultGenerator() {
            return new FeatureSpruceTree(FeatureSpruceTree.DEFAULT_HEIGHT, this);
        }

        @Override
        public WorldFeature getHugeGenerator() {
            return new FeatureHugeSpruceTree(FeatureHugeSpruceTree.DEFAULT_HEIGHT, this);
        }
    },
    PINE(BlockIds.LOG, BlockLog.SPRUCE, BlockIds.LEAVES, BlockLeaves.SPRUCE, null, -1) {
        @Override
        public WorldFeature getDefaultGenerator() {
            //porktodo: this
            return new FeatureSpruceTree(FeatureSpruceTree.DEFAULT_HEIGHT, this);
        }

        @Override
        public WorldFeature getHugeGenerator() {
            return new FeatureHugePineTree(FeatureHugeSpruceTree.DEFAULT_HEIGHT, this);
        }
    },
    BIRCH(BlockIds.LOG, BlockLog.BIRCH, BlockIds.LEAVES, BlockLeaves.BIRCH, BlockIds.SAPLING, BlockSapling.BIRCH),
    JUNGLE(BlockIds.LOG, BlockLog.JUNGLE, BlockIds.LEAVES, BlockLeaves.JUNGLE, BlockIds.SAPLING, BlockSapling.JUNGLE),
    ACACIA(BlockIds.LOG2, BlockLog2.ACACIA, BlockIds.LEAVES2, BlockLeaves2.ACACIA, BlockIds.SAPLING, BlockSapling.ACACIA),
    DARK_OAK(BlockIds.LOG2, BlockLog2.DARK_OAK, BlockIds.LEAVES2, BlockLeaves2.DARK_OAK, BlockIds.SAPLING, BlockSapling.DARK_OAK),
    MUSHROOM_RED(BlockIds.RED_MUSHROOM_BLOCK, BlockHugeMushroomRed.STEM, BlockIds.RED_MUSHROOM_BLOCK, BlockHugeMushroomRed.ALL, BlockIds.RED_MUSHROOM, 0)   {
        @Override
        public WorldFeature getDefaultGenerator() {
            return new FeatureMushroomRed(FeatureNormalTree.DEFAULT_HEIGHT);
        }
    },
    MUSHROOM_BROWN(BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.STEM, BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.ALL, BlockIds.BROWN_MUSHROOM, 0)   {
        @Override
        public WorldFeature getDefaultGenerator() {
            return new FeatureMushroomBrown(FeatureNormalTree.DEFAULT_HEIGHT);
        }
    };

    private static final TreeSpecies[] VALUES = values();

    public static TreeSpecies fromItem(Identifier id, int damage) {
        for (TreeSpecies species : VALUES) {
            if (species.itemId == id && species.itemDamage == damage) {
                return species;
            }
        }
        throw new IllegalArgumentException(String.format("Unknown tree species item %s:%d", id, damage));
    }

    protected final Identifier logId;
    protected final int        logDamage;
    protected final Identifier leavesId;
    protected final int        leavesDamage;

    protected final Identifier itemId;
    protected final int        itemDamage;

    public Identifier getLogId() {
        return this.logId;
    }

    public int getLogDamage() {
        return this.logDamage;
    }

    public Identifier getLeavesId() {
        return this.leavesId;
    }

    public int getLeavesDamage() {
        return this.leavesDamage;
    }

    public Identifier getItemId() {
        return this.itemId;
    }

    public int getItemDamage() {
        return this.itemDamage;
    }

    public WorldFeature getDefaultGenerator() {
        return new FeatureNormalTree(FeatureNormalTree.DEFAULT_HEIGHT, this);
    }

    public WorldFeature getHugeGenerator()  {
        return null;
    }
}
