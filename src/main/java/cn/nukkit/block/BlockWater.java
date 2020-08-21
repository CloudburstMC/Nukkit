package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockWater extends BlockLiquid {


    public BlockWater() {
        this(0);
    }

    public BlockWater(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WATER;
    }

    @Override
    public String getName() {
        return "Water";
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        boolean ret = this.getLevel().setBlock(this, this, true, false);
        this.getLevel().scheduleUpdate(this, this.tickRate());

        return ret;
    }

    @Since("1.2.1.0-PN")
    @PowerNukkitOnly
    @Override
    public void afterRemoval(Block newBlock, boolean update) {
        if (!update) {
            return;
        }
        
        int newId = newBlock.getId();
        if (newId == WATER || newId == STILL_WATER) {
            return;
        }
        
        Block up = up(1, 0);
        for (BlockFace diagonalFace : BlockFace.Plane.HORIZONTAL) {
            Block diagonal = up.getSide(diagonalFace);
            if (diagonal.getId() == BlockID.SUGARCANE_BLOCK) {
                diagonal.onUpdate(Level.BLOCK_UPDATE_SCHEDULED);
            }
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WATER_BLOCK_COLOR;
    }

    @Override
    public BlockLiquid getBlock(int meta) {
        return (BlockLiquid) Block.get(BlockID.WATER, meta);
    }

    @Override
    public void onEntityCollide(Entity entity) {
        super.onEntityCollide(entity);

        if (entity.fireTicks > 0) {
            entity.extinguish();
        }
    }

    @Override
    public int tickRate() {
        return 5;
    }

    @Override
    public boolean usesWaterLogging() {
        return true;
    }
}
