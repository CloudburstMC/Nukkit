package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.CompassRoseDirection;

import javax.annotation.Nonnull;

import static cn.nukkit.blockproperty.CommonBlockProperties.FACING_DIRECTION;

/**
 * @author Pub4Game
 * @since 26.12.2015
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
public class BlockWallSign extends BlockSignPost {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(FACING_DIRECTION);

    public BlockWallSign() {
        this(0);
    }

    public BlockWallSign(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WALL_SIGN;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getWallId() {
        return getId();
    }

    @Override
    protected int getPostId() {
        return SIGN_POST;
    }

    @Override
    public String getName() {
        return "Wall Sign";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getSide(getBlockFace().getOpposite()).getId() == AIR) {
                this.getLevel().useBreakOn(this);
            }
            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(FACING_DIRECTION, face);
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(FACING_DIRECTION);
    }

    @Override
    public void setSignDirection(CompassRoseDirection direction) {
        setBlockFace(direction.getClosestBlockFace());
    }

    @Override
    public CompassRoseDirection getSignDirection() {
        return getBlockFace().getCompassRoseDirection();
    }
}
