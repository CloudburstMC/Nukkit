package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockDoubleSlabBlackstonePolished extends BlockDoubleSlabBase {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockDoubleSlabBlackstonePolished() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockDoubleSlabBlackstonePolished(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_DOUBLE_SLAB;
    }

    @Override
    public int getSingleSlabId() {
        return POLISHED_BLACKSTONE_SLAB;
    }

    @Override
    public String getSlabName() {
        return "Polished Blackstone";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 6.0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }
}
