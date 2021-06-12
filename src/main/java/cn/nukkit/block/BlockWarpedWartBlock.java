package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockWarpedWartBlock extends BlockSolid {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockWarpedWartBlock() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Warped Wart Block";
    }

    @Override
    public int getId() {
        return WARPED_WART_BLOCK;
    }

    // TODO Fix it in https://github.com/PowerNukkit/PowerNukkit/pull/370, the same for BlockNetherWartBlock
    @Override
    public int getToolType() {
        return ItemTool.TYPE_HANDS_ONLY; //TODO Correct type is hoe
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_WART_BLOCK_COLOR;
    }
}
