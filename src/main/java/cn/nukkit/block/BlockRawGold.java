package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

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

    @Override
    public BlockColor getColor() {
        return BlockColor.GOLD_BLOCK_COLOR;
    }
}
