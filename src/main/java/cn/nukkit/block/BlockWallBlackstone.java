package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockWallBlackstone extends BlockWallBase {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockWallBlackstone() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockWallBlackstone(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Blackstone Wall";
    }

    @Override
    public int getId() {
        return BLACKSTONE_WALL;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
}
