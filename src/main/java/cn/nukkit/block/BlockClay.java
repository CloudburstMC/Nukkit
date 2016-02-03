package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.BlockColor;

/**
 * @author Nukkit Project Team
 */
public class BlockClay extends BlockSolid {

    public BlockClay(int meta) {
        super(0);
    }

    public BlockClay() {
        this(0);
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_SHOVEL;
    }

    @Override
    public int getId() {
        return Block.CLAY_BLOCK;
    }

    @Override
    public String getName() {
        return "Clay Block";
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{{Item.CLAY, 0, 4}};
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CLAY_BLOCK_COLOR;
    }
}
