package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockStairs extends BlockTransparentMeta implements Faceable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty UPSIDE_DOWN = new BooleanBlockProperty("upside_down_bit", false);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperty<BlockFace> STAIRS_DIRECTION = new ArrayBlockProperty<>("weirdo_direction", false, new BlockFace[]{
            BlockFace.EAST, BlockFace.WEST,
            BlockFace.SOUTH, BlockFace.NORTH
    }).ordinal(true);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(STAIRS_DIRECTION, UPSIDE_DOWN);
    

    protected BlockStairs(int meta) {
        super(meta);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getMinY() {
        return this.y + (isUpsideDown() ? 0.5 : 0);
    }

    @Override
    public double getMaxY() {
        return this.y + (isUpsideDown() ? 1 : 0.5);
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return side == BlockFace.UP && isUpsideDown() || side == BlockFace.DOWN && !isUpsideDown();
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player != null) {
            setBlockFace(player.getDirection());
        }
        
        if ((fy > 0.5 && face != BlockFace.UP) || face == BlockFace.DOWN) {
            setUpsideDown(true);
        }
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public boolean collidesWithBB(AxisAlignedBB bb) {
        BlockFace face = getBlockFace();
        double minSlabY = 0;
        double maxSlabY = 0.5;
        double minHalfSlabY = 0.5;
        double maxHalfSlabY = 1;
        
        if (isUpsideDown()) {
            minSlabY = 0.5;
            maxSlabY = 1;
            minHalfSlabY = 0;
            maxHalfSlabY = 0.5;
        }

        if (bb.intersectsWith(new SimpleAxisAlignedBB(
                this.x,
                this.y + minSlabY,
                this.z,
                this.x + 1,
                this.y + maxSlabY,
                this.z + 1
        ))) {
            return true;
        }


        switch (face) {
            case EAST:
                return bb.intersectsWith(new SimpleAxisAlignedBB(
                        this.x + 0.5,
                        this.y + minHalfSlabY,
                        this.z,
                        this.x + 1,
                        this.y + maxHalfSlabY,
                        this.z + 1
                ));
            case WEST:
                return bb.intersectsWith(new SimpleAxisAlignedBB(
                        this.x,
                        this.y + minHalfSlabY,
                        this.z,
                        this.x + 0.5,
                        this.y + maxHalfSlabY,
                        this.z + 1
                ));
            case SOUTH:
                return bb.intersectsWith(new SimpleAxisAlignedBB(
                        this.x,
                        this.y + minHalfSlabY,
                        this.z + 0.5,
                        this.x + 1,
                        this.y + maxHalfSlabY,
                        this.z + 1
                ));
            case NORTH:
                return bb.intersectsWith(new SimpleAxisAlignedBB(
                        this.x,
                        this.y + minHalfSlabY,
                        this.z,
                        this.x + 1,
                        this.y + maxHalfSlabY,
                        this.z + 0.5
                ));
            default:
                return false;
        }
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setUpsideDown(boolean upsideDown) {
        setBooleanValue(UPSIDE_DOWN, upsideDown);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isUpsideDown() {
        return getBooleanValue(UPSIDE_DOWN);
    }

    @PowerNukkitDifference(info = "Was returning the wrong face", since = "1.3.0.0-PN")
    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(STAIRS_DIRECTION);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(STAIRS_DIRECTION, face);
    }
}
