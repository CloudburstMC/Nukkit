package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * Created by good777LUCKY
 */
public class BlockObsidianCrying extends BlockSolid {

    public BlockObsidianCrying() {
    }
    
    @Override
    public int getId() {
        return CRYING_OBSIDIAN;
    }
    
    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
    
    @Override
    public String getName() {
        return "Crying Obsidian";
    }
    
    @Override
    public double getHardness() {
        return 50;
    }
    
    @Override
    public double getResistance() {
        return 1200;
    }
    
    @Override
    public int getLightLevel() {
        return 10;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getToolTier() {
        return ItemTool.TIER_DIAMOND;
    }
    
    @Override
    public boolean canBePushed() {
        return false;
    }
    
    @Override
    public boolean canBePulled() {
        return false;
    }
    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }
}
