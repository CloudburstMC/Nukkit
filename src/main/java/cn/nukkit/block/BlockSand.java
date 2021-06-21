package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.SandType;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(info = "Extends BlockFallableMeta instead of BlockFallable")
public class BlockSand extends BlockFallableMeta {

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final ArrayBlockProperty<SandType> SAND_TYPE = new ArrayBlockProperty<>("sand_type", true, SandType.class);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(SAND_TYPE);

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

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
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
