package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockPlanksCrimson extends BlockSolid {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockPlanksCrimson() {
        // Does nothing
    }

    @Override
    public int getId() {
        return CRIMSON_PLANKS;
    }
    
    @Override
    public String getName() {
        return "Crimson Planks";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 3;
    }
    
    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CRIMSON_STEM_BLOCK_COLOR;
    }
    
    @Override
    public int getBurnChance() {
        return 0;
    }
    
    @Override
    public int getBurnAbility() {
        return 0;
    }

}
