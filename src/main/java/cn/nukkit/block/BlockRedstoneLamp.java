package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * @author Nukkit Project Team
 */
public class BlockRedstoneLamp extends BlockSolid {

    public BlockRedstoneLamp(int meta) {
        super(meta);
    }

    public BlockRedstoneLamp() {
        this(0);
    }

    @Override
    public String getName() {
        return "Redstone Lamp";
    }

    @Override
    public int getId() {
        return REDSTONE_LAMP;
    }

    @Override
    public double getHardness() {
        return 0.3D;
    }

    @Override
    public double getResistance() {
        return 1.5D;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.REDSTONE_LAMP, 0, 1}
        };
    }

}
