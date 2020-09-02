package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(info = "Extends BlockFallableMeta instead of BlockFallable")
public class BlockSand extends BlockFallableMeta {

    public static final int DEFAULT = 0;
    public static final int RED = 1;

    public BlockSand() {
        // Does nothing
    }

    public BlockSand(int meta) {
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
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        if (this.getDamage() == 0x01) {
            return "Red Sand";
        }

        return "Sand";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
