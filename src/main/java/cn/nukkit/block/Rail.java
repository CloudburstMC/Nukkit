package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.BlockColor;

/**
 * Created by Snake1999 on 2016/1/11.
 * Package cn.nukkit.block in project nukkit
 */
public class Rail extends Transparent {

    public Rail() {
        this(0);
    }

    public Rail(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Rail";
    }

    @Override
    public int getId() {
        return RAIL;
    }

    @Override
    public double getHardness() {
        return 0.7;
    }

    @Override
    public double getResistance() {
        return 3.5;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.RAIL, 0, 1}
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }


}
