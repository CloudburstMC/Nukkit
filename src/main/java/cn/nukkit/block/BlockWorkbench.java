package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/5 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockWorkbench extends BlockSolid {
    public BlockWorkbench() {
        this(0);
    }

    public BlockWorkbench(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Crafting Table";
    }

    @Override
    public int getId() {
        return WORKBENCH;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            player.craftingType = Player.CRAFTING_BIG;
        }
        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {this.getId(), 0, 1}
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
