package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockNetheriteBlock extends BlockSolid{
    public BlockNetheriteBlock() {
    }

    @Override
    public int getId() {
        return NETHERITE_BLOCK;
    }

    @Override
    public String getName() {
        return "Netherite Block";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 35; // TODO Should be 50, but the break time is glitchy (same with obsidian but less noticeable because of the texture)
    }

    @Override
    public double getResistance() {
        return 6000;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getToolTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isLavaResistant() {
        return true;
    }
}
