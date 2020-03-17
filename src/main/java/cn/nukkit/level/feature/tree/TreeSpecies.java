package cn.nukkit.level.feature.tree;

import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockLeaves2;
import cn.nukkit.block.BlockLog;
import cn.nukkit.block.BlockLog2;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.feature.WorldFeature;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;

/**
 * The different tree varieties in Minecraft.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public enum TreeSpecies {
    OAK(BlockIds.LOG, BlockLog.OAK, BlockIds.LEAVES, BlockLeaves.OAK, BlockSapling.OAK) {
        @Override
        public WorldFeature getDefaultGenerator() {
            return new FeatureLargeOakTree(FeatureNormalTree.DEFAULT_HEIGHT, this, 0.1d, FeatureLargeOakTree.DEFAULT_HEIGHT);
        }
    },
    SPRUCE(BlockIds.LOG, BlockLog.SPRUCE, BlockIds.LEAVES, BlockLeaves.SPRUCE, BlockSapling.SPRUCE),
    BIRCH(BlockIds.LOG, BlockLog.BIRCH, BlockIds.LEAVES, BlockLeaves.BIRCH, BlockSapling.BIRCH),
    JUNGLE(BlockIds.LOG, BlockLog.JUNGLE, BlockIds.LEAVES, BlockLeaves.JUNGLE, BlockSapling.JUNGLE),
    ACACIA(BlockIds.LOG2, BlockLog2.ACACIA, BlockIds.LEAVES2, BlockLeaves2.ACACIA, BlockSapling.ACACIA),
    DARK_OAK(BlockIds.LOG2, BlockLog2.DARK_OAK, BlockIds.LEAVES2, BlockLeaves2.DARK_OAK, BlockSapling.DARK_OAK);

    private static final TreeSpecies[] VALUES = values();

    public static TreeSpecies fromSaplingDamage(int saplingDamage) {
        saplingDamage &= 0x7;
        Preconditions.checkArgument(saplingDamage >= 0 && saplingDamage < VALUES.length, "invalid sapling damage: %s", saplingDamage);
        return VALUES[saplingDamage];
    }

    protected final Identifier logId;
    protected final int        logDamage;
    protected final Identifier leavesId;
    protected final int        leavesDamage;

    protected final int saplingDamage;

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

    public int getSaplingDamage() {
        return this.saplingDamage;
    }

    public WorldFeature getDefaultGenerator() {
        return new FeatureNormalTree(FeatureNormalTree.DEFAULT_HEIGHT, this);
    }
}
