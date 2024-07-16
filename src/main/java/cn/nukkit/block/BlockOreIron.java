package cn.nukkit.block;

import cn.nukkit.item.ItemID;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockOreIron extends BlockOre {

    @Override
    public int getId() {
        return IRON_ORE;
    }

    @Override
    protected int getRawMaterial() {
        return ItemID.RAW_IRON;
    }

    @Override
    public String getName() {
        return "Iron Ore";
    }
}
