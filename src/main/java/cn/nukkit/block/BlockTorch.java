package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
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

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockTorch extends BlockFlowable implements Faceable {
    public static final BlockProperty<TorchFace> TORCH_FACING_DIRECTION = new ArrayBlockProperty<>("torch_facing_direction", false, TorchFace.class);
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

    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getLightLevel() {
        return 14;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block below = this.down();
            BlockFace side = getBlockFace();

            if (this.getSide(side).isTransparent() && !(side == BlockFace.DOWN && (below instanceof BlockFence || below.getId() == COBBLE_WALL))) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block below = this.down();

        if (!target.isTransparent() && face != BlockFace.DOWN) {
            this.setBlockFace(face.getOpposite());
            this.getLevel().setBlock(block, this, true, true);

            return true;
        } else if (!below.isTransparent() || below instanceof BlockFence || below.getId() == COBBLE_WALL) {
            this.setTorchFace(TorchFace.TOP);
            this.getLevel().setBlock(block, this, true, true);

            return true;
        }
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public BlockFace getBlockFace() {
        return getTorchFace().getBlockFace();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public void setBlockFace(BlockFace face) {
        TorchFace torchFace;
        switch (face) {
            default:
            case DOWN:
                throw new InvalidBlockPropertyValueException(TORCH_FACING_DIRECTION, getTorchFace(), face, "The give BlockFace can't be mapped to TorchFace");
            case UP:
                torchFace = TorchFace.TOP;
                break;
            case NORTH:
                torchFace = TorchFace.NORTH;
                break;
            case SOUTH:
                torchFace = TorchFace.SOUTH;
                break;
            case WEST:
                torchFace = TorchFace.WEST;
                break;
            case EAST:
                torchFace = TorchFace.EAST;
                break;
        }
        setTorchFace(torchFace);
    }

    @Deprecated
    @DeprecationDetails(reason = "Using magic value", replaceWith = "getBlockFace()", since = "1.4.0.0-PN")
    public BlockFace getBlockFace(int meta) {
        return TORCH_FACING_DIRECTION.getValueForMeta(meta).getBlockFace();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public TorchFace getTorchFace() {
        return getPropertyValue(TORCH_FACING_DIRECTION);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setTorchFace(TorchFace face) {
        setPropertyValue(TORCH_FACING_DIRECTION, face);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Getter
    public enum TorchFace {
        UNKNOWN, WEST, EAST, NORTH, SOUTH, TOP;
        
        @Nonnull
        public BlockFace getBlockFace() {
            switch (this) {
                default:
                case UNKNOWN:
                case TOP:
                    return BlockFace.UP;
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
    }
}
