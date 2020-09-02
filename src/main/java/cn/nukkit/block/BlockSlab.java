package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;

import javax.annotation.Nonnull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockSlab extends BlockTransparentMeta {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty TOP_SLOT_PROPERTY = new BooleanBlockProperty("top_slot_bit", false);
    public static final BlockProperties SIMPLE_SLAB_PROPERTIES = new BlockProperties(TOP_SLOT_PROPERTY);

    protected final int doubleSlab;

    public BlockSlab(int meta, int doubleSlab) {
        super(meta);
        this.doubleSlab = doubleSlab;
    }
    
    public abstract String getSlabName();

    @Override
    public String getName() {
        return (isOnTop()? "Upper " : "") + getSlabName() + " Slab";
    }

    @Override
    public double getMinY() {
        return isOnTop() ? this.y + 0.5 : this.y;
    }

    @Override
    public double getMaxY() {
        return isOnTop() ? this.y + 1 : this.y + 0.5;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return getToolType() < ItemTool.TYPE_AXE ? 30 : 15;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isOnTop() {
        return getBooleanValue(TOP_SLOT_PROPERTY);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setOnTop(boolean top) {
        setBooleanValue(TOP_SLOT_PROPERTY, top);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract boolean isSameType(BlockSlab slab);

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return side == BlockFace.UP && isOnTop() || side == BlockFace.DOWN && !isOnTop();
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        setOnTop(false);
        if (face == BlockFace.DOWN) {
            if (target instanceof BlockSlab && target.getBooleanValue(TOP_SLOT_PROPERTY) && isSameType((BlockSlab) target)) {
                this.getLevel().setBlock(target, getCurrentState().withBlockId(doubleSlab).getBlock(target), true);

                return true;
            } else if (block instanceof BlockSlab && isSameType((BlockSlab) block)) {
                this.getLevel().setBlock(block, getCurrentState().withBlockId(doubleSlab).getBlock(target), true);

                return true;
            } else {
                setOnTop(true);
            }
        } else if (face == BlockFace.UP) {
            if (target instanceof BlockSlab && !target.getBooleanValue(TOP_SLOT_PROPERTY) && isSameType((BlockSlab) target)) {
                this.getLevel().setBlock(target, getCurrentState().withBlockId(doubleSlab).getBlock(target), true);

                return true;
            } else if (block instanceof BlockSlab && isSameType((BlockSlab) block)) {
                this.getLevel().setBlock(block, getCurrentState().withBlockId(doubleSlab).getBlock(target), true);

                return true;
            }
            //TODO: check for collision
        } else {
            if (block instanceof BlockSlab) {
                if (isSameType((BlockSlab) block)) {
                    this.getLevel().setBlock(block, getCurrentState().withBlockId(doubleSlab).getBlock(block), true);

                    return true;
                }

                return false;
            } else {
                if (fy > 0.5) {
                    setOnTop(true);
                }
            }
        }

        if (block instanceof BlockSlab && !isSameType((BlockSlab) block)) {
            return false;
        }
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }
}
