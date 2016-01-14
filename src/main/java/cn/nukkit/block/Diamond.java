package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.BlockColor;

/**
 * @author Nukkit Project Team
 */
public class Diamond extends Solid {

    public Diamond(int meta) {
        super(0);
    }

    public Diamond() {
        this(0);
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public int getId() {
        return Block.DIAMOND_BLOCK;
    }

    @Override
    public String getName() {
        return "Diamond Block";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() > Tool.TIER_IRON) {
            return new int[][]{{Item.DIAMOND_BLOCK, 0, 1}};
        } else {
            return new int[0][];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }

}
