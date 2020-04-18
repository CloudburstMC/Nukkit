package cn.nukkit.level.feature.tree;

import cn.nukkit.block.BlockHugeMushroomBrown;
import cn.nukkit.block.BlockHugeMushroomRed;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockLeaves2;
import cn.nukkit.block.BlockLog;
import cn.nukkit.block.BlockLog2;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.feature.FeatureChorusTree;
import cn.nukkit.level.feature.FeatureFallenTree;
import cn.nukkit.level.feature.WorldFeature;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.utils.Identifier;
import lombok.NonNull;
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
        public WorldFeature getDefaultGenerator(@NonNull IntRange height) {
            return new FeatureLargeOakTree(height, this, 0.1d, FeatureLargeOakTree.DEFAULT_HEIGHT);
        }

        @Override
        public WorldFeature getFallenGenerator() {
            return new FeatureFallenTree(FeatureNormalTree.DEFAULT_HEIGHT, this.logId, this.logDamage, 0.75d);
        }
    },
    SWAMP(BlockIds.LOG, BlockLog.OAK, BlockIds.LEAVES, BlockLeaves.OAK, null, -1) {
        @Override
        public WorldFeature getDefaultGenerator() {
            return this.getDefaultGenerator(FeatureSwampTree.DEFAULT_HEIGHT);
        }

        @Override
        public WorldFeature getDefaultGenerator(@NonNull IntRange height) {
            return new FeatureSwampTree(height, OAK);
        }
    },
    SPRUCE(BlockIds.LOG, BlockLog.SPRUCE, BlockIds.LEAVES, BlockLeaves.SPRUCE, BlockIds.SAPLING, BlockSapling.SPRUCE) {
        @Override
        public WorldFeature getDefaultGenerator() {
            return this.getDefaultGenerator(FeatureSpruceTree.DEFAULT_HEIGHT);
        }

        @Override
        public WorldFeature getDefaultGenerator(@NonNull IntRange height) {
            return new FeatureSpruceTree(height, this);
        }

        @Override
        public WorldFeature getHugeGenerator() {
            return new FeatureHugeSpruceTree(FeatureHugeSpruceTree.DEFAULT_HEIGHT, this);
        }

        @Override
        public WorldFeature getFallenGenerator() {
            return new FeatureFallenTree(FeatureSpruceTree.DEFAULT_HEIGHT, this.logId, this.logDamage, 0.0d);
        }
    },
    PINE(BlockIds.LOG, BlockLog.SPRUCE, BlockIds.LEAVES, BlockLeaves.SPRUCE, null, -1) {
        @Override
        public WorldFeature getDefaultGenerator() {
            return this.getDefaultGenerator(FeatureSpruceTree.DEFAULT_HEIGHT);
        }

        @Override
        public WorldFeature getDefaultGenerator(@NonNull IntRange height) {
            return new FeatureSpruceTree(FeatureSpruceTree.DEFAULT_HEIGHT, this);
        }

        @Override
        public WorldFeature getHugeGenerator() {
            return new FeatureHugePineTree(FeatureHugeSpruceTree.DEFAULT_HEIGHT, this);
        }
    },
    BIRCH(BlockIds.LOG, BlockLog.BIRCH, BlockIds.LEAVES, BlockLeaves.BIRCH, BlockIds.SAPLING, BlockSapling.BIRCH)   {
        @Override
        public WorldFeature getFallenGenerator() {
            return new FeatureFallenTree(new IntRange(5, 8), this.logId, this.logDamage, 0.0d);
        }
    },
    JUNGLE(BlockIds.LOG, BlockLog.JUNGLE, BlockIds.LEAVES, BlockLeaves.JUNGLE, BlockIds.SAPLING, BlockSapling.JUNGLE)   {
        @Override
        public WorldFeature getDefaultGenerator(@NonNull IntRange height) {
            return new FeatureJungleTree(height, this);
        }

        @Override
        public WorldFeature getHugeGenerator() {
            return new FeatureHugeJungleTree(FeatureHugeJungleTree.DEFAULT_HEIGHT, this);
        }

        @Override
        public WorldFeature getFallenGenerator() {
            return new FeatureFallenTree(new IntRange(4, 11), this.logId, this.logDamage, 0.75d);
        }
    },
    ACACIA(BlockIds.LOG2, BlockLog2.ACACIA, BlockIds.LEAVES2, BlockLeaves2.ACACIA, BlockIds.SAPLING, BlockSapling.ACACIA)   {
        @Override
        public WorldFeature getDefaultGenerator() {
            return this.getDefaultGenerator(FeatureSavannaTree.DEFAULT_HEIGHT);
        }

        @Override
        public WorldFeature getDefaultGenerator(@NonNull IntRange height) {
            return new FeatureSavannaTree(height, this);
        }
    },
    DARK_OAK(BlockIds.LOG2, BlockLog2.DARK_OAK, BlockIds.LEAVES2, BlockLeaves2.DARK_OAK, BlockIds.SAPLING, BlockSapling.DARK_OAK)   {
        @Override
        public WorldFeature getDefaultGenerator(@NonNull IntRange height) {
            return null;
        }

        @Override
        public WorldFeature getHugeGenerator() {
            return new FeatureDarkOakTree(FeatureDarkOakTree.DEFAULT_HEIGHT, this);
        }
    },
    MUSHROOM_RED(BlockIds.RED_MUSHROOM_BLOCK, BlockHugeMushroomRed.STEM, BlockIds.RED_MUSHROOM_BLOCK, BlockHugeMushroomRed.ALL, BlockIds.RED_MUSHROOM, 0)   {
        @Override
        public WorldFeature getDefaultGenerator(@NonNull IntRange height) {
            return new FeatureMushroomRed(height);
        }
    },
    MUSHROOM_BROWN(BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.STEM, BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.ALL, BlockIds.BROWN_MUSHROOM, 0)   {
        @Override
        public WorldFeature getDefaultGenerator(@NonNull IntRange height) {
            return new FeatureMushroomBrown(height);
        }
    },
    CHORUS(BlockIds.CHORUS_PLANT, 0, BlockIds.CHORUS_FLOWER, 5, BlockIds.CHORUS_FLOWER, 0)  {
        @Override
        public WorldFeature getDefaultGenerator() {
            return this.getDefaultGenerator(FeatureChorusTree.DEFAULT_BRANCH_HEIGHT);
        }

        @Override
        public WorldFeature getDefaultGenerator(@NonNull IntRange height) {
            return new FeatureChorusTree(height, FeatureChorusTree.DEFAULT_MAX_RECURSION, FeatureChorusTree.DEFAULT_MAX_OVERHANG);
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
        return this.getDefaultGenerator(FeatureNormalTree.DEFAULT_HEIGHT);
    }

    public WorldFeature getDefaultGenerator(@NonNull IntRange height) {
        return new FeatureNormalTree(height, this);
    }

    public WorldFeature getHugeGenerator()  {
        return null;
    }

    public WorldFeature getFallenGenerator()  {
        return null;
    }
}
