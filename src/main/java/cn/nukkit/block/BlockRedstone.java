package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.redstone.Redstone;
import cn.nukkit.utils.BlockColor;

/*
 * Created on 2015/12/11 by Pub4Game.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockRedstone extends BlockSolid {

    public BlockRedstone() {
        this(0);
    }

    public BlockRedstone(int meta) {
        super(0);
        this.setPowerSource(true);
        this.setPowerLevel(Redstone.POWER_STRONGEST);
    }

    @Override
    public int getId() {
        return REDSTONE_BLOCK;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Redstone Block";
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        this.getLevel().setBlock(block, this, true, true);
        Redstone.active(this);
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        int level = this.getPowerLevel();
        this.getLevel().setBlock(this, new BlockAir(), true, false);
        Redstone.deactive(this, level);
        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new int[][]{{Item.REDSTONE_BLOCK, 0, 1}};
        } else {
            return new int[0][];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.REDSTONE_BLOCK_COLOR;
    }
}
