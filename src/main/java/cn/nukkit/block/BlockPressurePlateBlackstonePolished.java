package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockPressurePlateBlackstonePolished extends BlockPressurePlateStone {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockPressurePlateBlackstonePolished() {
        // Does nothing
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockPressurePlateBlackstonePolished(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_PRESSURE_PLATE;
    }

    @Override
    public String getName() {
        return "Polished Blackstone Pressure Plate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }
}
