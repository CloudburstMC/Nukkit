package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockDoubleSlabWarped extends BlockDoubleSlabBase {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockDoubleSlabWarped() {
        super(0);
    }

    @Override
    public int getId() {
        return WARPED_DOUBLE_SLAB;
    }
    
    @Override
    public String getSlabName() {
        return "Warped";
    }

    @Override
    public int getSingleSlabId() {
        return WARPED_SLAB;
    }

    //TODO Adjust or remove this when merging https://github.com/PowerNukkit/PowerNukkit/pull/370
    @Override
    protected boolean isCorrectTool(Item item) {
        return true;
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
        return BlockColor.CYAN_BLOCK_COLOR;
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
