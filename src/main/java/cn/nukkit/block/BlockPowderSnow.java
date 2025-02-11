package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockPowderSnow extends BlockTransparent {

    public BlockPowderSnow() {
        super();
    }

    @Override
    public String getName() {
        return "Powder Snow";
    }

    @Override
    public int getId() {
        return POWDER_SNOW;
    }

    @Override
    public double getHardness() {
        return 0.25;
    }

    @Override
    public double getResistance() {
        return 0.25;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SNOW_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }
}
