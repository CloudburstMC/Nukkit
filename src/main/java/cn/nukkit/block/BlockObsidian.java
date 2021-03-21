package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * @author xtypr
 * @since 2015/12/2
 */
public class BlockObsidian extends BlockSolid {

    public BlockObsidian() {
    }

    @Override
    public String getName() {
        return "Obsidian";
    }

    @Override
    public int getId() {
        return OBSIDIAN;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public double getHardness() {
        return 35; //TODO Should be 50 but the break time calculation is broken
    }

    @Override
    public double getResistance() {
        return 6000;
    }

    @Override
    public boolean onBreak(Item item) {
        //destroy the nether portal
        Block[] nearby = new Block[]{
                this.up(), this.down(),
                this.north(), south(),
                this.west(), this.east(),
        };
        for (Block aNearby : nearby) {
            if (aNearby != null && aNearby.getId() == NETHER_PORTAL) {
                aNearby.onBreak(item);
            }
        }
        return super.onBreak(item);
    }

    @Since("1.2.1.0-PN")
    @PowerNukkitOnly
    @Override
    public void afterRemoval(Block newBlock, boolean update) {
        if (update) {
            onBreak(Item.get(BlockID.AIR));
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.OBSIDIAN_BLOCK_COLOR;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
