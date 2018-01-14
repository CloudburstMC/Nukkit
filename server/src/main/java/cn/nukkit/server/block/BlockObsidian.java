package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.util.BlockColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockObsidian extends BlockSolid {

    public BlockObsidian() {
        this(0);
    }

    public BlockObsidian(int meta) {
        super(0);
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
    public double getHardness() {
        return 50;
    }

    @Override
    public double getResistance() {
        return 6000;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_DIAMOND) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
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
            if (aNearby != null) if (aNearby.getId() == NETHER_PORTAL) {
                aNearby.onBreak(item);
            }
        }
        return super.onBreak(item);
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
    public boolean canHarvestWithHand() {
        return false;
    }
}
