package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;

import static cn.nukkit.blockproperty.CommonBlockProperties.DIRECTION;
import static cn.nukkit.blockproperty.CommonBlockProperties.OPEN;

/**
 * @author xtypr
 * @since 2015/11/23
 */
public class BlockFenceGate extends BlockTransparentMeta implements Faceable {
    public static final BooleanBlockProperty IN_WALL = new BooleanBlockProperty("in_wall_bit", false);
    public static final BlockProperties PROPERTIES = new BlockProperties(DIRECTION, OPEN, IN_WALL);

    public BlockFenceGate() {
        this(0);
    }

    public BlockFenceGate(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FENCE_GATE_OAK;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Oak Fence Gate";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    private static final double[] offMinX = new double[2];
    private static final double[] offMinZ = new double[2];
    private static final double[] offMaxX = new double[2];
    private static final double[] offMaxZ = new double[2];

    static {
        offMinX[0] = 0;
        offMinZ[0] = 0.375;
        offMaxX[0] = 1;
        offMaxZ[0] = 0.625;

        offMinX[1] = 0.375;
        offMinZ[1] = 0;
        offMaxX[1] = 0.625;
        offMaxZ[1] = 1;
    }

    private int getOffsetIndex() {
        switch (getBlockFace()) {
            case SOUTH:
            case NORTH:
                return 0;
            default:
                return 1;
        }
    }

    @Override
    public double getMinX() {
        return this.x + offMinX[getOffsetIndex()];
    }

    @Override
    public double getMinZ() {
        return this.z + offMinZ[getOffsetIndex()];
    }

    @Override
    public double getMaxX() {
        return this.x + offMaxX[getOffsetIndex()];
    }

    @Override
    public double getMaxZ() {
        return this.z + offMaxZ[getOffsetIndex()];
    }

    @PowerNukkitDifference(info = "InWall property is now properly set, returns false if setBlock fails", since = "1.4.0.0-PN")
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        BlockFace direction = player.getDirection();
        setBlockFace(direction);
        
        if (getSide(direction.rotateY()) instanceof BlockWallBase
                || getSide(direction.rotateYCCW()) instanceof BlockWallBase) {
            setInWall(true);
        }
        
        return this.getLevel().setBlock(block, this, true, true);
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (player == null) {
            return false;
        }

        if (!this.toggle(player)) {
            return false;
        }

        this.level.addSound(this, isOpen() ? Sound.RANDOM_DOOR_OPEN : Sound.RANDOM_DOOR_CLOSE);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    public boolean toggle(Player player) {
        DoorToggleEvent event = new DoorToggleEvent(this, player);
        this.getLevel().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        player = event.getPlayer();

        BlockFace direction;


        BlockFace originDirection = getBlockFace();
        
        if (player != null) {
            double yaw = player.yaw;
            double rotation = (yaw - 90) % 360;

            if (rotation < 0) {
                rotation += 360.0;
            }

            if (originDirection.getAxis() == BlockFace.Axis.Z) {
                if (rotation >= 0 && rotation < 180) {
                    direction = BlockFace.NORTH;
                } else {
                    direction = BlockFace.SOUTH;
                }
            } else {
                if (rotation >= 90 && rotation < 270) {
                    direction = BlockFace.EAST;
                } else {
                    direction = BlockFace.WEST;
                }
            }
        } else {
            if (originDirection.getAxis() == BlockFace.Axis.Z) {
                direction = BlockFace.SOUTH;
            } else {
                direction = BlockFace.WEST;
            }
        }
        
        setBlockFace(direction);
        toggleBooleanProperty(OPEN);
        this.level.setBlock(this, this, false, false);
        return true;
    }

    public boolean isOpen() {
        return getBooleanValue(OPEN);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setOpen(boolean open) {
        setBooleanValue(OPEN, open);
    }

    @PowerNukkitDifference(info = "Will connect to walls correctly", since = "1.4.0.0-PN")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            BlockFace face = getBlockFace();
            boolean touchingWall = getSide(face.rotateY()) instanceof BlockWallBase || getSide(face.rotateYCCW()) instanceof BlockWallBase;
            if (touchingWall != isInWall()) {
                setInWall(touchingWall);
                level.setBlock(this, this, true);
                return type;
            }
        } else if (type == Level.BLOCK_UPDATE_REDSTONE && this.level.getServer().isRedstoneEnabled()) {
            boolean isPowered = level.isBlockPowered(this.getLocation());
            
            if (isOpen() != isPowered) {
                this.toggle(null);
                return type;
            }
        }

        return 0;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isInWall() {
        return getBooleanValue(IN_WALL);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setInWall(boolean inWall) {
        setBooleanValue(IN_WALL, inWall);
    }
    
    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(DIRECTION);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(DIRECTION, face);
    }
}
