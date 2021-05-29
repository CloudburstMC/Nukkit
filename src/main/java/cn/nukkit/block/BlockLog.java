package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;

import javax.annotation.Nonnull;

import static cn.nukkit.blockproperty.CommonBlockProperties.PILLAR_AXIS;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public abstract class BlockLog extends BlockSolidMeta {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected static final BlockProperties PILLAR_PROPERTIES = new BlockProperties(CommonBlockProperties.PILLAR_AXIS);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected BlockLog(int meta) {
        super(meta);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public abstract BlockProperties getProperties();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected abstract BlockState getStrippedState();

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
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (item.isAxe()) {
            Block strippedBlock = getStrippedState().getBlock(this);
            item.useOn(this);
            this.level.setBlock(this, strippedBlock, true, true);
            return true;
        }
        return false;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}
