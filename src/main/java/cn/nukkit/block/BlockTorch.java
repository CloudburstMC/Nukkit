package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyValueException;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author xtypr
 * @since 2015/12/2
 */
public class BlockTorch extends BlockFlowable implements Faceable {
    public static final BlockProperty<TorchAttachment> TORCH_FACING_DIRECTION = new ArrayBlockProperty<>("torch_facing_direction", false, TorchAttachment.class);
    public static final BlockProperties PROPERTIES = new BlockProperties(TORCH_FACING_DIRECTION);

    public BlockTorch() {
        this(0);
    }

    public BlockTorch(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Torch";
    }

    @Override
    public int getId() {
        return TORCH;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getLightLevel() {
        return 14;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed the block update logic to follow the same behaviour has vanilla")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            TorchAttachment torchAttachment = getTorchAttachment();

            Block support = this.getSide(torchAttachment.getAttachedFace());
            if (!BlockLever.isSupportValid(support, torchAttachment.getTorchDirection())) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }
    
    @Nullable
    private BlockFace findValidSupport() {
        for (BlockFace horizontalFace : BlockFace.Plane.HORIZONTAL) {
            if (BlockLever.isSupportValid(getSide(horizontalFace.getOpposite()), horizontalFace)) {
                return horizontalFace;
            }
        }
        if (BlockLever.isSupportValid(down(), BlockFace.UP)) {
            return BlockFace.UP;
        }
        return null;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed the logic to follow the same behaviour has vanilla")
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.canBeReplaced()) {
            target = target.down();
            face = BlockFace.UP;
        }
        
        if (face == BlockFace.DOWN || !BlockLever.isSupportValid(target, face)) {
            BlockFace valid = findValidSupport();
            if (valid == null) {
                return false;
            }
            face = valid;
        }
        
        this.setBlockFace(face);
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public BlockFace getBlockFace() {
        return getTorchAttachment().getTorchDirection();
    }

    /**
     * Sets the direction that the flame is pointing.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public void setBlockFace(BlockFace face) {
        TorchAttachment torchAttachment = TorchAttachment.getByTorchDirection(face);
        if (torchAttachment == null) {
            throw new InvalidBlockPropertyValueException(TORCH_FACING_DIRECTION, getTorchAttachment(), face, "The give BlockFace can't be mapped to TorchFace");
        }
        
        setTorchAttachment(torchAttachment);
    }

    @Deprecated
    @DeprecationDetails(reason = "Using magic value", replaceWith = "getBlockFace()", since = "1.4.0.0-PN")
    public BlockFace getBlockFace(int meta) {
        return TORCH_FACING_DIRECTION.getValueForMeta(meta).getTorchDirection();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public TorchAttachment getTorchAttachment() {
        return getPropertyValue(TORCH_FACING_DIRECTION);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setTorchAttachment(TorchAttachment face) {
        setPropertyValue(TORCH_FACING_DIRECTION, face);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Getter
    public enum TorchAttachment {
        UNKNOWN, WEST, EAST, NORTH, SOUTH, TOP;

        /**
         * The direction that the flame is pointing.
         */
        public BlockFace getTorchDirection() {
            switch (this) {
                default:
                case UNKNOWN:
                case TOP:
                    return BlockFace.UP;
                case EAST:
                    return BlockFace.WEST;
                case WEST:
                    return BlockFace.EAST;
                case SOUTH:
                    return BlockFace.NORTH;
                case NORTH:
                    return BlockFace.SOUTH;
            }
        }
        
        @Nullable
        public static TorchAttachment getByTorchDirection(@Nonnull BlockFace face) {
            switch (face) {
                default:
                case DOWN:
                    return null;
                case UP:
                    return TOP;
                case EAST:
                    return WEST;
                case WEST:
                    return EAST;
                case SOUTH:
                    return NORTH;
                case NORTH:
                    return SOUTH;
            }
        }

        /**
         * The direction that is touching the attached block.
         */
        @Nonnull
        public BlockFace getAttachedFace() {
            switch (this) {
                default:
                case UNKNOWN:
                case TOP:
                    return BlockFace.DOWN;
                case EAST:
                    return BlockFace.EAST;
                case WEST:
                    return BlockFace.WEST;
                case SOUTH:
                    return BlockFace.SOUTH;
                case NORTH:
                    return BlockFace.NORTH;
            }
        }
        
        @Nullable
        public static TorchAttachment getByAttachedFace(@Nonnull BlockFace face) {
            switch (face) {
                default:
                case UP:
                    return null;
                case DOWN:
                    return TorchAttachment.TOP;
                case SOUTH:
                    return TorchAttachment.SOUTH;
                case NORTH:
                    return TorchAttachment.NORTH;
                case EAST:
                    return TorchAttachment.EAST;
                case WEST:
                    return TorchAttachment.WEST;
            }
        }
    }
}
