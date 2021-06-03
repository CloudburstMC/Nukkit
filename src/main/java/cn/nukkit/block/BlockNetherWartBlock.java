package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockNetherWartBlock extends BlockSolid {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockNetherWartBlock() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Nether Wart Block";
    }

    @Override
    public int getId() {
        return BLOCK_NETHER_WART_BLOCK;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    // TODO Fix it in https://github.com/PowerNukkit/PowerNukkit/pull/370, the same for BlockNetherWartBlock
    @PowerNukkitDifference(info = "It's now hoe instead of none", since = "1.4.0.0-PN")
    @Override
    public int getToolType() {
        return ItemTool.TYPE_HANDS_ONLY; //TODO Correct type is hoe
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
