package cn.nukkit.block;

import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.BlockColor;

public class BlockOreLapisDeepslate extends BlockOre {

    public BlockOreLapisDeepslate() {
    }

    @Override
    protected int getRawMaterial() {
        return ItemID.DYE;
    }

    @Override
    protected int getRawMaterialMeta() {
        return ItemDye.LAPIS_LAZULI;
    }

    @Override
    public int getId() {
        return DEEPSLATE_LAPIS_ORE;
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public String getName() {
        return "Deepslate Lapis Lazuli Ore";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_GRAY;
    }
}
