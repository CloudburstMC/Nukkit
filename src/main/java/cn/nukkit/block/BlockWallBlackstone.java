package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockWallBlackstone extends BlockWallBase {
    public BlockWallBlackstone() {
        this(0);
    }

    public BlockWallBlackstone(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Blackstone Wall";
    }

    @Override
    public int getId() {
        return BLACKSTONE_WALL;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[] { toItem() };
        }
        return new Item[0];
    }
}
