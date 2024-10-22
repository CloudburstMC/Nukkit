package cn.nukkit.block;

import cn.nukkit.item.ItemID;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Utils;

public class BlockOreCoalDeepslate extends BlockOre {

    public BlockOreCoalDeepslate() {
    }

    @Override
    protected int getRawMaterial() {
        return ItemID.COAL;
    }

    @Override
    public int getId() {
        return DEEPSLATE_COAL_ORE;
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public String getName() {
        return "Deepslate Coal Ore";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_GRAY;
    }

    @Override
    public int getDropExp() {
        return Utils.rand(0, 2);
    }
}
