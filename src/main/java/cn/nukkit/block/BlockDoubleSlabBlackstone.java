package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockDoubleSlabBlackstone extends BlockDoubleSlabBase {
    public BlockDoubleSlabBlackstone() {
        this(0);
    }

    protected BlockDoubleSlabBlackstone(int meta) {
        super(meta);
    }

    @Override
    public String getSlabName() {
        return "Double Blackstone Slab";
    }

    @Override
    public int getId() {
        return BLACKSTONE_DOUBLE_SLAB;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getSingleSlabId() {
        return RED_SANDSTONE_SLAB;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
