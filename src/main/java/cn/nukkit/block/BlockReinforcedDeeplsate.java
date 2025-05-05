package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockReinforcedDeeplsate extends BlockSolid {

    @Override
    public int getId() {
        return REINFORCED_DEEPSLATE;
    }

    @Override
    public String getName() {
        return "Reinforced Deeplsate";
    }

    @Override
    public double getHardness() {
        return 55;
    }

    @Override
    public double getResistance() {
        return 1200;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_GRAY_BLOCK_COLOR;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }
}
