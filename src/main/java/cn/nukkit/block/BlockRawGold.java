package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockRawGold extends BlockRawOreVariant {

    public BlockRawGold() {
    }

    @Override
    public String getName() {
        return "Block of Raw Gold";
    }

    @Override
    public int getId() {
        return RAW_GOLD_BLOCK;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }
}
