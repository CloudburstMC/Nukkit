package cn.nukkit.block;

import cn.nukkit.item.ItemID;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockOreGold extends BlockOre {

    @Override
    public int getId() {
        return GOLD_ORE;
    }

    @Override
    protected int getRawMaterial() {
        return ItemID.RAW_GOLD;
    }

    @Override
    public String getName() {
        return "Gold Ore";
    }
}
