package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockWall.WallConnectionType;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Faceable;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import static cn.nukkit.math.VectorMath.calculateAxis;
import static cn.nukkit.math.VectorMath.calculateFace;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@Log4j2
public abstract class BlockWallBase extends BlockTransparentMeta implements BlockConnectable {
    public static final BlockProperty<WallConnectionType> WALL_CONNECTION_TYPE_SOUTH = new ArrayBlockProperty<>("wall_connection_type_south", false, WallConnectionType .class);
    public static final BlockProperty<WallConnectionType> WALL_CONNECTION_TYPE_WEST = new ArrayBlockProperty<>("wall_connection_type_west", false, WallConnectionType .class);
    public static final BlockProperty<WallConnectionType> WALL_CONNECTION_TYPE_NORTH = new ArrayBlockProperty<>("wall_connection_type_north", false, WallConnectionType .class);
    public static final BlockProperty<WallConnectionType> WALL_CONNECTION_TYPE_EAST = new ArrayBlockProperty<>("wall_connection_type_east", false, WallConnectionType .class);
    public static final BooleanBlockProperty WALL_POST_BIT = new BooleanBlockProperty("wall_post_bit", false);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(
            WALL_CONNECTION_TYPE_SOUTH,
            WALL_CONNECTION_TYPE_WEST,
            WALL_CONNECTION_TYPE_NORTH,
            WALL_CONNECTION_TYPE_EAST,
            WALL_POST_BIT
    );

    private static final double MIN_POST_BB =  5.0/16;
    private static final double MAX_POST_BB = 11.0/16;

    public BlockWallBase(int meta) {
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
    public boolean isSolid() {
        return false;
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    private boolean shouldBeTall(Block above, BlockFace face) {
        switch (above.getId()) {
            case AIR:
            case SKULL_BLOCK:
                return false;

            // If the bell is standing and follow the path, make it tall
            case BELL:
                BlockBell bell = (BlockBell) above;
                return bell.getAttachmentType() == BlockBell.TYPE_ATTACHMENT_STANDING
                        && bell.getBlockFace().getAxis() != face.getAxis();
            default:
                if (above instanceof BlockWallBase) {
                    return ((BlockWallBase) above).getConnectionType(face) != WallConnectionType.NONE;
                } else if (above instanceof BlockConnectable) {
                    return ((BlockConnectable) above).isConnected(face);
                } else if (above instanceof BlockPressurePlateBase || above instanceof BlockStairs) {
                    return true;
                }
                return above.isSolid() && !above.isTransparent() || shouldBeTallBasedOnBoundingBox(above, face);
        }
    }

    private boolean shouldBeTallBasedOnBoundingBox(Block above, BlockFace face) {
        AxisAlignedBB boundingBox = above.getBoundingBox();
        if (boundingBox == null) {
            return false;
        }
        boundingBox = boundingBox.getOffsetBoundingBox(-above.x, -above.y, -above.z);
        if (boundingBox.getMinY() > 0) {
            return false;
        }
        int offset = face.getXOffset();
        if (offset < 0) {
            return boundingBox.getMinX() < MIN_POST_BB
                    && boundingBox.getMinZ() < MIN_POST_BB && MAX_POST_BB < boundingBox.getMaxZ();
        } else if (offset > 0) {
            return MAX_POST_BB < boundingBox.getMaxX()
                    && MAX_POST_BB < boundingBox.getMaxZ() && boundingBox.getMinZ() < MAX_POST_BB;
        } else {
            offset = face.getZOffset();
            if (offset < 0) {
                return boundingBox.getMinZ() < MIN_POST_BB
                        && boundingBox.getMinX() < MIN_POST_BB && MIN_POST_BB < boundingBox.getMaxX();
            } else if (offset > 0) {
                return MAX_POST_BB < boundingBox.getMaxZ()
                        && MAX_POST_BB < boundingBox.getMaxX() && boundingBox.getMinX() < MAX_POST_BB;
            }
        }
        return false;
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean autoConfigureState() {
        final Number previousMeta = getDataStorage();

        setWallPost(true);

        Block above = up(1, 0);

        for (BlockFace blockFace : BlockFace.Plane.HORIZONTAL) {
            Block side = getSideAtLayer(0, blockFace);
            if (canConnect(side)) {
                try {
                    connect(blockFace, above, false);
                } catch (RuntimeException e) {
                    log.error("Failed to connect the block {} at {} to {} which is {} at {}", 
                            this, getLocation(), blockFace, side, side.getLocation(), e);
                    throw e;
                }
            } else {
                disconnect(blockFace);
            }
        }

        recheckPostConditions(above);

        return !getDataStorage().equals(previousMeta);
    }

    @PowerNukkitDifference(info = "Will connect as expected", since = "1.3.0.0-PN")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if(autoConfigureState()) {
                level.setBlock(this, this, true);
            }
            return type;
        }
        return 0;
    }

    @PowerNukkitDifference(info = "Will be placed on the right state", since = "1.3.0.0-PN")
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        autoConfigureState();
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean isWallPost() {
        return getBooleanValue(WALL_POST_BIT);
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public void setWallPost(boolean wallPost) {
        setBooleanValue(WALL_POST_BIT, wallPost);
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public void clearConnections() {
        setPropertyValue(WALL_CONNECTION_TYPE_EAST, WallConnectionType.NONE);
        setPropertyValue(WALL_CONNECTION_TYPE_WEST, WallConnectionType.NONE);
        setPropertyValue(WALL_CONNECTION_TYPE_NORTH, WallConnectionType.NONE);
        setPropertyValue(WALL_CONNECTION_TYPE_SOUTH, WallConnectionType.NONE);
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public Map<BlockFace, WallConnectionType> getWallConnections() {
        EnumMap<BlockFace, WallConnectionType> connections = new EnumMap<>(BlockFace.class);
        for (BlockFace blockFace : BlockFace.Plane.HORIZONTAL) {
            WallConnectionType connectionType = getConnectionType(blockFace);
            if (connectionType != WallConnectionType.NONE) {
                connections.put(blockFace, connectionType);
            }
        }
        return connections;
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public WallConnectionType getConnectionType(BlockFace blockFace) {
        switch (blockFace) {
            case NORTH:
                return getPropertyValue(WALL_CONNECTION_TYPE_NORTH);
            case SOUTH:
                return getPropertyValue(WALL_CONNECTION_TYPE_SOUTH);
            case WEST:
                return getPropertyValue(WALL_CONNECTION_TYPE_WEST);
            case EAST:
                return getPropertyValue(WALL_CONNECTION_TYPE_EAST);
            default:
                return WallConnectionType.NONE;
        }
    }


    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean setConnection(BlockFace blockFace, WallConnectionType type) {
        switch (blockFace) {
            case NORTH:
                setPropertyValue(WALL_CONNECTION_TYPE_NORTH, type);
                return true;
            case SOUTH:
                setPropertyValue(WALL_CONNECTION_TYPE_SOUTH, type);
                return true;
            case WEST:
                setPropertyValue(WALL_CONNECTION_TYPE_WEST, type);
                return true;
            case EAST:
                setPropertyValue(WALL_CONNECTION_TYPE_EAST, type);
                return true;
            default:
                return false;
        }
    }

    /**
     * @return true if it should be a post
     */
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public void autoUpdatePostFlag() {
        setWallPost(recheckPostConditions(up(1, 0)));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean hasConnections() {
        return getPropertyValue(WALL_CONNECTION_TYPE_EAST) != WallConnectionType.NONE 
                || getPropertyValue(WALL_CONNECTION_TYPE_WEST) != WallConnectionType.NONE
                || getPropertyValue(WALL_CONNECTION_TYPE_NORTH) != WallConnectionType.NONE 
                || getPropertyValue(WALL_CONNECTION_TYPE_SOUTH) != WallConnectionType.NONE;
    }

    private boolean recheckPostConditions(Block above) {
        // If nothing is connected, it should be a post
        if (!hasConnections()) {
            return true;
        }

        // If it's not straight, it should be a post
        Map<BlockFace, WallConnectionType> connections = getWallConnections();
        if (connections.size() != 2) {
            return true;
        }

        Iterator<Map.Entry<BlockFace, WallConnectionType>> iterator = connections.entrySet().iterator();
        Map.Entry<BlockFace, WallConnectionType> entryA = iterator.next();
        Map.Entry<BlockFace, WallConnectionType> entryB = iterator.next();
        if (entryA.getValue() != entryB.getValue() || entryA.getKey().getOpposite() != entryB.getKey()) {
            return true;
        }

        BlockFace.Axis axis = entryA.getKey().getAxis();

        switch (above.getId()) {
            // These special blocks forces walls to become a post
            case FLOWER_POT_BLOCK:
            case SKULL_BLOCK:
            case CONDUIT:
            case STANDING_BANNER:
            case TURTLE_EGG:
                return true;

            // End rods make it become a post if it's placed on the wall
            case END_ROD:
                if (((Faceable) above).getBlockFace() == BlockFace.UP) {
                    return true;
                }
                break;

            // If the bell is standing and don't follow the path, make it a post
            case BELL:
                BlockBell bell = (BlockBell) above;
                if (bell.getAttachmentType() == BlockBell.TYPE_ATTACHMENT_STANDING
                        && bell.getBlockFace().getAxis() == axis) {
                    return true;
                }

                break;

            default:
                if (above instanceof BlockWallBase) {
                    // If the wall above is a post, it should also be a post
                    
                    if (((BlockWallBase) above).isWallPost()) {
                        return true;
                    }
                    
                } else if (above instanceof BlockLantern) {
                    // Lanterns makes this become a post if they are not hanging

                    if (!((BlockLantern) above).isHanging()) {
                        return true;
                    }

                } else if (above.getId() == LEVER || above instanceof BlockTorch || above instanceof BlockButton) {
                    // These blocks make this become a post if they are placed down (facing up)

                    if (((Faceable) above).getBlockFace() == BlockFace.UP) {
                        return true;
                    }

                } else if (above instanceof BlockFenceGate) {
                    // If the gate don't follow the path, make it a post

                    if (((Faceable) above).getBlockFace().getAxis() == axis) {
                        return true;
                    }

                } else if (above instanceof BlockConnectable) {
                    // If the connectable block above don't share 2 equal connections, then this should be a post

                    int shared = 0;
                    for (BlockFace connection : ((BlockConnectable) above).getConnections()) {
                        if (connections.containsKey(connection) && ++shared == 2) {
                            break;
                        }
                    }

                    if (shared < 2) {
                        return true;
                    }

                }
        }

        // Sign posts always makes the wall become a post
        return above instanceof BlockSignPost;
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean isSameHeightStraight() {
        Map<BlockFace, WallConnectionType> connections = getWallConnections();
        if (connections.size() != 2) {
            return false;
        }

        Iterator<Map.Entry<BlockFace, WallConnectionType>> iterator = connections.entrySet().iterator();
        Map.Entry<BlockFace, WallConnectionType> a = iterator.next();
        Map.Entry<BlockFace, WallConnectionType> b = iterator.next();
        return a.getValue() == b.getValue() && a.getKey().getOpposite() == b.getKey();
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean connect(BlockFace blockFace) {
        return connect(blockFace, true);
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean connect(BlockFace blockFace, boolean recheckPost) {
        if (blockFace.getHorizontalIndex() < 0) {
            return false;
        }

        Block above = getSideAtLayer(0, BlockFace.UP);
        return connect(blockFace, above, recheckPost);
    }

    private boolean connect(BlockFace blockFace, Block above, boolean recheckPost) {
        WallConnectionType type = shouldBeTall(above, blockFace)? WallConnectionType.TALL : WallConnectionType.SHORT;
        if (setConnection(blockFace, type)) {
            if (recheckPost) {
                recheckPostConditions(above);
            }
            return true;
        }
        return false;
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean disconnect(BlockFace blockFace) {
        if (blockFace.getHorizontalIndex() < 0) {
            return false;
        }

        if (setConnection(blockFace, WallConnectionType.NONE)) {
            autoUpdatePostFlag();
            return true;
        }
        return false;
    }
    
    @Override
    protected AxisAlignedBB recalculateBoundingBox() {

        boolean north = this.canConnect(this.getSide(BlockFace.NORTH));
        boolean south = this.canConnect(this.getSide(BlockFace.SOUTH));
        boolean west = this.canConnect(this.getSide(BlockFace.WEST));
        boolean east = this.canConnect(this.getSide(BlockFace.EAST));

        double n = north ? 0 : 0.25;
        double s = south ? 1 : 0.75;
        double w = west ? 0 : 0.25;
        double e = east ? 1 : 0.75;

        if (north && south && !west && !east) {
            w = 0.3125;
            e = 0.6875;
        } else if (!north && !south && west && east) {
            n = 0.3125;
            s = 0.6875;
        }

        return new SimpleAxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1.5,
                this.z + s
        );
    }

    @PowerNukkitDifference(info = "Will connect to glass panes, iron bars and fence gates", since = "1.3.0.0-PN")
    @Override
    public boolean canConnect(Block block) {
        switch (block.getId()) {
            case GLASS_PANE:
            case STAINED_GLASS_PANE:
            case IRON_BARS:
            case GLASS:
            case STAINED_GLASS:
                return true;
            default:
                if (block instanceof BlockWallBase) {
                    return true;
                } 
                if (block instanceof BlockFenceGate) {
                    BlockFenceGate fenceGate = (BlockFenceGate) block;
                    return fenceGate.getBlockFace().getAxis() != calculateAxis(this, block);
                } 
                if (block instanceof BlockStairs) {
                    return ((BlockStairs) block).getBlockFace().getOpposite() == calculateFace(this, block);
                }
                if (block instanceof BlockTrapdoor) {
                    BlockTrapdoor trapdoor = (BlockTrapdoor) block;
                    return trapdoor.isOpen() && trapdoor.getBlockFace() == calculateFace(this, trapdoor);
                }
                return block.isSolid() && !block.isTransparent();
        }
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    @Override
    public boolean isConnected(BlockFace face) {
        return getConnectionType(face) != WallConnectionType.NONE;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
