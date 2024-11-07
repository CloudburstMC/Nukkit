package cn.nukkit.block;

import cn.nukkit.item.ItemID;
import cn.nukkit.utils.Utils;

public class BlockOreCopper extends BlockOre {

    public BlockOreCopper() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Copper Ore";
    }

    @Override
    public int getId() {
        return COPPER_ORE;
    }

    @Override
    protected int getRawMaterial() {
        return ItemID.RAW_COPPER;
    }

    @Override
    protected float getDropMultiplier() {
        return 3;
    }

    @Override
    public int getDropExp() {
        return Utils.rand(0, 2);
    }
}
