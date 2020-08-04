package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Axis;
import cn.nukkit.math.BlockFace.AxisDirection;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.InvalidBlockDamageException;
import lombok.extern.log4j.Log4j2;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import static cn.nukkit.utils.BlockColor.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 * @apiNote Implements BlockConnectable only on PowerNukkit
 */
@PowerNukkitDifference(info = "Extends BlockTransparentHyperMeta instead of BlockTransparentMeta, implements BlockConnectable only on PowerNukkit", since = "1.3.0.0-PN")
@Log4j2
public class BlockWall extends BlockTransparentHyperMeta implements BlockConnectable {
    private static final boolean SHOULD_FAIL = false; 
    private static final boolean SHOULD_VALIDATE_META = true;
    private static final double MIN_POST_BB =  5.0/16;
    private static final double MAX_POST_BB = 11.0/16;
    
    @Deprecated
    @DeprecationDetails(reason = "No longer matches the meta directly", replaceWith = "WallType.COBBLESTONE", since = "1.3.0.0-PN")
    public static final int NONE_MOSSY_WALL = 0;
    
    @Deprecated
    @DeprecationDetails(reason = "No longer matches the meta directly", replaceWith = "WallType.MOSSY_COBBLESTONE", since = "1.3.0.0-PN")
    public static final int MOSSY_WALL = 1;
    
    private static final int[] INVALID_META_COMBINATIONS = {
            0b0_0000_0011_0000,
            0b0_0000_1100_0000,
            0b0_0011_0000_0000,
            0b0_1100_0000_0000,
            0b0_0000_0000_1110,
            0b0_0000_0000_1111,
    };

    public BlockWall() {
        this(0);
    }

    @PowerNukkitDifference(since = "1.3.0.0-PN", info = "If an invalid metadata is given, it will remove the invalid bits automatically")
    public BlockWall(int meta) {
        setDamage(meta);
    }

    @Override
    public int getId() {
        return STONE_WALL;
    }

    @PowerNukkitDifference(since = "1.3.0.0-PN", info = "If an invalid metadata is given, it will remove the invalid bits automatically")
    @Override
    public void setDamage(int meta) {
        if (!SHOULD_VALIDATE_META) {
            super.setDamage(meta);
        }
        
        if (getDamage() == meta) {
            return;
        }
        
        for (int invalidMetaCombination : INVALID_META_COMBINATIONS) {
            if ((meta & invalidMetaCombination) != invalidMetaCombination) {
                continue;
            }
            
            InvalidBlockDamageException exception = new InvalidBlockDamageException(getId(), meta, getDamage());
            if (SHOULD_FAIL) {
                throw exception;
            }
            
            if (!isInitializing()) {
                log.warn("Tried to set an invalid wall meta, the bits " + invalidMetaCombination + " were removed." + (level != null ? " " + getLocation() : ""), exception);
            }
            
            meta = meta & ~invalidMetaCombination;
        }
        
        super.setDamage(meta);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, getDamage() & 0xF);
    }

    @Override
    public boolean isSolid() {
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

    @PowerNukkitDifference(since = "1.3.0.0-PN", info = "Return the actual material color instead of transparent")
    @Override
    public BlockColor getColor() {
        return getWallType().color;
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
            case COBBLE_WALL:
                return ((BlockWall) above).getConnectionType(face) != WallConnectionType.NONE;
            default:
                if (above instanceof BlockConnectable) {
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
            return boundingBox.getMinX() < MIN_POST_BB /*&& MIN_POST_BB <= boundingBox.getMaxX()*/
                    && boundingBox.getMinZ() < MIN_POST_BB && MAX_POST_BB < boundingBox.getMaxZ();
        } else if (offset > 0) {
            return /*boundingBox.getMinX() <= MAX_POST_BB &&*/ MAX_POST_BB < boundingBox.getMaxX()
                    && MAX_POST_BB < boundingBox.getMaxZ() && boundingBox.getMinZ() < MAX_POST_BB; 
        } else {
            offset = face.getZOffset();
            if (offset < 0) {
                return boundingBox.getMinZ() < MIN_POST_BB /*&& MIN_POST_BB <= boundingBox.getMaxZ()*/
                        && boundingBox.getMinX() < MIN_POST_BB && MIN_POST_BB < boundingBox.getMaxX();
            } else if (offset > 0) {
                return /*boundingBox.getMinZ() <= MAX_POST_BB &&*/ MAX_POST_BB < boundingBox.getMaxZ()
                        && MAX_POST_BB < boundingBox.getMaxX() && boundingBox.getMinX() < MAX_POST_BB;
            }
        }
        return false;
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean autoConfigureState() {
        final int previousMeta = getDamage();

        setWallPost(true);
        
        Block above = up(1, 0);

        for (BlockFace blockFace : BlockFace.Plane.HORIZONTAL) {
            Block side = getSideAtLayer(0, blockFace);
            if (canConnect(side)) {
                try {
                    connect(blockFace, above, false);
                } catch (RuntimeException e) {
                    log.error("Failed to connect the block "+this+" at "+getLocation()+" to "+blockFace+" which is "+side+" at "+side.getLocation());
                    throw e;
                }
            } else {
                disconnect(blockFace);
            }
        }
        
        recheckPostConditions(above);
        
        return getDamage() != previousMeta;
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
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        autoConfigureState();
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean isWallPost() {
        return (getDamage() & 0x1000) == 0x1000;
    }
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public void setWallPost(boolean wallPost) {
        setDamage(getDamage() & ~0x1000 | (wallPost? 0x1000 : 0x0000));
    }
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public WallType getWallType() {
        int type = getDamage() & 0xF;
        if (type > WallType.VALUES.length) {
            type = 0;
        }
        return WallType.VALUES[type];
    }
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public void setWallType(WallType type) {
        setDamage((getDamage() & ~0xF) | (type.ordinal() & 0xF));
    }
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public void clearConnections() {
        setDamage(getDamage() & ~0xFF0);
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
    
    private int getBitIndex(BlockFace face) {
        assert face.getHorizontalIndex() >= 0;
        return 4 + face.getHorizontalIndex() * 2;
    }
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public WallConnectionType getConnectionType(BlockFace blockFace) {
        if (blockFace.getHorizontalIndex() < 0) {
            return WallConnectionType.NONE;
        }

        int bitIndex = getBitIndex(blockFace);
        int typeOrdinal = getDamage() >> bitIndex & 0x3;
        return WallConnectionType.VALUES[typeOrdinal];
    }


    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean setConnection(BlockFace blockFace, WallConnectionType type) {
        if (blockFace.getHorizontalIndex() < 0) {
            return false;
        }
        
        // Locate the 2 bit position
        int bitIndex = getBitIndex(blockFace);
        
        // Clear the 2 bits
        int damage = getDamage() & ~(0x3 << bitIndex);
        
        // Set the 2 bits values based on the connection type
        damage |= type.ordinal() << bitIndex;
        
        // Save in memory
        setDamage(damage);

        return true;
    }

    /**
     * @return true if it should be a post
     */
    @PowerNukkitDifference
    @Since("1.3.0.0-PN")
    public void autoUpdatePostFlag() {
        setWallPost(recheckPostConditions(up(1, 0)));
    }
    
    private boolean recheckPostConditions(Block above) {
        // If nothing is connected, it should be a post
        if ((getDamage() & 0xFF0) == 0x000) {
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

        Axis axis = entryA.getKey().getAxis(); 
        
        switch (above.getId()) {
            // These special blocks forces walls to become a post
            case FLOWER_POT_BLOCK:
            case SKULL_BLOCK:
            case CONDUIT:
            case STANDING_BANNER:
                return true;
                
            // End rods make it become a post if it's placed on the wall
            case END_ROD:
                if (((Faceable) above).getBlockFace() == BlockFace.UP) {
                    return true;
                }
                break;

            // If the wall above is a post, it should also be a post
            case COBBLE_WALL:
                if (((BlockWall) above).isWallPost()) {
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
                if (above instanceof BlockLantern) {
                    // Lanterns makes this become a post if they are not hanging

                    if (above.getDamage() == 0) { // Not hanging
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
    public String getName() {
        if (this.getDamage() == 0x01) {
            return "Mossy Cobblestone Wall";
        }

        return "Cobblestone Wall";
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
            case COBBLE_WALL:
            case GLASS_PANE:
            case STAINED_GLASS_PANE:
            case IRON_BARS:
                return true;
            default:
                if (block instanceof BlockFenceGate) {
                    BlockFenceGate fenceGate = (BlockFenceGate) block;
                    return fenceGate.getBlockFace().getAxis() != calculateAxis(block);
                } else if (block instanceof BlockStairs) {
                    return ((BlockStairs) block).getBlockFace() == calculateFace(block);
                }
                return block.isSolid() && !block.isTransparent();
        }
    }
    
    private Axis calculateAxis(Block side) {
        Position vector = side.subtract(this);
        return vector.x != 0? Axis.X : vector.z != 0? Axis.Z : Axis.Y;
    }
    
    private BlockFace calculateFace(Block side) {
        Position vector = side.subtract(this);
        Axis axis = vector.x != 0? Axis.X : vector.z != 0? Axis.Z : Axis.Y;
        double direction = axis == Axis.X? vector.x : axis == Axis.Y? vector.y : vector.z;
        return BlockFace.fromAxis(direction < 0? AxisDirection.NEGATIVE : AxisDirection.POSITIVE, axis);
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
    
    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    public enum WallConnectionType {
        NONE, SHORT, TALL;
        
        private static final WallConnectionType[] VALUES = values();
    }
    
    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    public enum WallType {
        COBBLESTONE, MOSSY_COBBLESTONE, GRANITE(DIRT_BLOCK_COLOR), DIORITE(QUARTZ_BLOCK_COLOR), ANDESITE, SANDSTONE(SAND_BLOCK_COLOR),
        BRICK(RED_BLOCK_COLOR), STONE_BRICK, MOSSY_STONE_BRICK, NETHER_BRICK(NETHERRACK_BLOCK_COLOR), END_STONE_BRICK(SAND_BLOCK_COLOR),
        PRISMARINE(CYAN_BLOCK_COLOR), RED_SANDSTONE(ORANGE_BLOCK_COLOR), RED_NETHER_BRICK(NETHERRACK_BLOCK_COLOR);
        
        private static final WallType[] VALUES = values();
        
        private final BlockColor color;
        
        WallType(BlockColor color) {
            this.color = color;
        }

        WallType() {
            this(STONE_BLOCK_COLOR);
        }

        @Since("1.3.0.0-PN")
        public BlockColor getColor() {
            return color;
        }
    }
}
