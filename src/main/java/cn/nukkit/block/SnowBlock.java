package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.BlockColor;

public class SnowBlock extends Solid {

    public SnowBlock() {
        this(0);
    }

    public SnowBlock(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Snow Block";
    }

    @Override
    public int getId() {
        return SNOW_BLOCK;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_SHOVEL;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isShovel()) {
            return new int[][]{
                    {Item.SNOWBALL, 0, 4}
            };
        }
        return new int[][]{};
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SNOW_BLOCK_COLOR;
    }
}
