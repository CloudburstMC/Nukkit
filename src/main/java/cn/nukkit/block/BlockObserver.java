package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cn.nukkit.blockproperty.CommonBlockProperties.FACING_DIRECTION;
import static cn.nukkit.blockproperty.CommonBlockProperties.POWERED;

/**
 * @author Leonidius20, joserobjr
 * @since 18.08.18
 */
public class BlockObserver extends BlockSolidMeta implements Faceable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(FACING_DIRECTION, POWERED);

    public BlockObserver() {
        this(0);
    }

    public BlockObserver(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Observer";
    }

    @Override
    public int getId() {
        return OBSERVER;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player != null) {
            if (Math.abs(player.getFloorX() - this.x) <= 1 && Math.abs(player.getFloorZ() - this.z) <= 1) {
                double y = player.y + player.getEyeHeight();
                if (y - this.y > 2) {
                    setBlockFace(BlockFace.DOWN);
                } else if (this.y - y > 0) {
                    setBlockFace(BlockFace.UP);
                } else {
                    setBlockFace(player.getHorizontalFacing());
                }
            } else {
                setBlockFace(player.getHorizontalFacing());
            }
        }
        
        this.getLevel().setBlock(block, this, true, true);
        return true;
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
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public double getResistance() {
        return 17.5;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isPowered() {
        return getBooleanValue(POWERED);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setPowered(boolean powered) {
        setBooleanValue(POWERED, powered);
    }
    
    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(FACING_DIRECTION);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(FACING_DIRECTION, face);
    }
}
