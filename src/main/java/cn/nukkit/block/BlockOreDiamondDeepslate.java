package cn.nukkit.block;

import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Utils;

public class BlockOreDiamondDeepslate extends BlockOre {

    public BlockOreDiamondDeepslate() {
    }

    @Override
    protected int getRawMaterial() {
        return ItemID.DIAMOND;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    public int getId() {
        return DEEPSLATE_DIAMOND_ORE;
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public String getName() {
        return "Deepslate Diamond Ore";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_GRAY;
    }

    @Override
    public int getDropExp() {
        return Utils.rand(3, 7);
    }
}
