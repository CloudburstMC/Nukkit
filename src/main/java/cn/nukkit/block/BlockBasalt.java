package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

import static cn.nukkit.blockproperty.CommonBlockProperties.PILLAR_AXIS;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockBasalt extends BlockSolidMeta {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(PILLAR_AXIS);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockBasalt() { this(0); }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockBasalt(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Basalt";
    }

    @Override
    public int getId() {
        return BlockID.BASALT;
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
        return 1.25;
    }

    @Override
    public double getResistance() {
        return 4.2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockFace.Axis getPillarAxis() {
        return getPropertyValue(PILLAR_AXIS);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setPillarAxis(BlockFace.Axis axis) {
        setPropertyValue(PILLAR_AXIS, axis);
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        setPillarAxis(face.getAxis());
        getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }
}
