package cn.nukkit.block;

import cn.nukkit.item.Tool;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Sand extends Fallable {

    public Sand() {
        this(0);
    }

    public Sand(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SAND;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        if (this.meta == 0x01) {
            return "Red Sand";
        }

        return "Sand";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
