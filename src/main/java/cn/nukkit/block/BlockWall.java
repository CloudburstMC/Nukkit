package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;
import lombok.extern.log4j.Log4j2;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import static cn.nukkit.utils.BlockColor.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@PowerNukkitDifference(info = "Extends BlockTransparentHyperMeta instead of BlockTransparentMeta", since = "1.3.0.0-PN")
@Log4j2
public class BlockWall extends BlockTransparentHyperMeta {
    @Deprecated
    @DeprecationDetails(reason = "No longer matches the meta directly", replaceWith = "WallType.COBBLESTONE", since = "1.3.0.0-PN")
    public static final int NONE_MOSSY_WALL = 0;
    
    @Deprecated
    @DeprecationDetails(reason = "No longer matches the meta directly", replaceWith = "WallType.MOSSY_COBBLESTONE", since = "1.3.0.0-PN")
    public static final int MOSSY_WALL = 1;

    public BlockWall() {
        this(0);
    }

    public BlockWall(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONE_WALL;
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

    private boolean shouldBePostBasedOnAbove(Block above) {
        switch (above.getId()) {
            case COBBLE_WALL:
                return ((BlockWall) above).isWallPost();
            case GLASS_PANE:
                return !((BlockGlassPane) above).isStraight();
            case FLOWER_POT_BLOCK:
                return true;
            default:
                return false;
        }
    }

    private boolean shouldBeTall(Block above, BlockFace face) {
        switch (above.getId()) {
            case COBBLE_WALL:
                return ((BlockWall) above).getConnectionType(face) != WallConnectionType.NONE;
            case GLASS_PANE:
                return ((BlockGlassPane) above).isConnected(face);
            case STAINED_GLASS_PANE:
                return true;
            default:
                return shouldBeTallBasedOnBoundingBox(above, face);
        }
    }
    
    private boolean shouldBeTallBasedOnBoundingBox(Block above, BlockFace face) {
        AxisAlignedBB boundingBox = above.getBoundingBox();
        if (boundingBox == null) {
            return false;
        }
        boundingBox = boundingBox.getOffsetBoundingBox(above.x, above.y, above.z);
        if (boundingBox.getMinY() > 0) {
            return false;
        }
        int offset = face.getXOffset();
        if (offset < 0) {
            return boundingBox.getMinX() == 0 && boundingBox.getMaxX() <= 0.5
                    && boundingBox.getMinZ() < 0.25 && boundingBox.getMaxZ() > 0.75
                    ;
        } else if (offset > 0) {
            return boundingBox.getMaxX() == 0 && boundingBox.getMinX() <= 0.5
                    && boundingBox.getMinZ() < 0.25 && boundingBox.getMaxZ() > 0.75
                    ;
        } else {
            offset = face.getZOffset();
            if (offset < 0) {
                return boundingBox.getMinZ() == 0 && boundingBox.getMaxZ() <= 0.5
                        && boundingBox.getMinX() < 0.25 && boundingBox.getMaxX() > 0.75
                        ;
            } else if (offset > 0) {
                return boundingBox.getMaxZ() == 0 && boundingBox.getMinZ() <= 0.5
                        && boundingBox.getMinX() < 0.25 && boundingBox.getMaxX() > 0.75
                        ;
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
        boolean forcePost = shouldBePostBasedOnAbove(above);

        for (BlockFace blockFace : BlockFace.Plane.HORIZONTAL) {
            Block side = getSideAtLayer(0, blockFace);
            if (canConnect(side)) {
                try {
                    connect(blockFace, above, forcePost);
                } catch (RuntimeException e) {
                    log.error("Failed to connect the block "+this+" at "+getLocation()+" to "+blockFace+" which is "+side+" at "+side.getLocation());
                    throw e;
                }
            }
        }
        
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
        setDamage((getDamage() & ~0xF) | type.ordinal());
    }
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public void clearConnections() {
        setDamage(getDamage() & ~0xFF0);
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public Map<BlockFace, WallConnectionType> getConnections() {
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
        int horizontalIndex = blockFace.getHorizontalIndex();
        if (horizontalIndex < 0) {
            return WallConnectionType.NONE;
        }

        int bitIndex = 4 + horizontalIndex * 2;
        int typeOrdinal = getDamage() >> bitIndex & 0x3;
        return WallConnectionType.VALUES[typeOrdinal];
    }


    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean setConnection(BlockFace blockFace, WallConnectionType type) {
        return setConnection(blockFace, type, shouldBePostBasedOnAbove(up(1, 0)));
    }
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean setConnection(BlockFace blockFace, WallConnectionType type, boolean forcePost) {
        int horizontalIndex = blockFace.getHorizontalIndex();
        if (horizontalIndex < 0) {
            return false;
        }
        
        // Locate the 2 bit position
        int bitIndex = 4 + horizontalIndex * 2;
        
        // Clear the 2 bits
        int damage = getDamage() & ~(0x3 << bitIndex);
        
        // Set the 2 bits values based on the connection type
        damage |= type.ordinal() << bitIndex;
        
        // If nothing is connected
        int connections = damage & 0xFF0;
        if (forcePost || connections == 0x000) {
            // Makes it become a post
            damage |= 0x1000;
        }
        
        // Save in memory
        setDamage(damage);

        // If the wall is straight, remove the post bit
        if (!forcePost && isSameHeightStraight()) {
            setWallPost(false);
        }
        
        return true;
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean isSameHeightStraight() {
        Map<BlockFace, WallConnectionType> connections = getConnections();
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
    public boolean isAnyHeightStraight() {
        Map<BlockFace, WallConnectionType> connections = getConnections();
        if (connections.size() != 2) {
            return false;
        }

        Iterator<BlockFace> iterator = connections.keySet().iterator();
        BlockFace a = iterator.next();
        BlockFace b = iterator.next();
        return a.getOpposite() == b;
    }
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean connect(BlockFace blockFace) {
        if (blockFace.getHorizontalIndex() < 0) {
            return false;
        }

        Block above = getSideAtLayer(0, BlockFace.UP);
        return connect(blockFace, above, shouldBePostBasedOnAbove(above));
    }

    private boolean connect(BlockFace blockFace, Block above, boolean forcePost) {
        WallConnectionType type = shouldBeTall(above, blockFace)? WallConnectionType.TALL : WallConnectionType.SHORT;
        return setConnection(blockFace, type, forcePost);
    }
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean disconnect(BlockFace blockFace) {
        if (blockFace.getHorizontalIndex() < 0) {
            return false;
        }
        
        return setConnection(blockFace, WallConnectionType.NONE);
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

    @PowerNukkitDifference(info = "Will connect to glass panes", since = "1.3.0.0-PN")
    public boolean canConnect(Block block) {
        switch (block.getId()) {
            case COBBLE_WALL:
            case FENCE_GATE:
            case GLASS_PANE:
            case STAINED_GLASS_PANE:
                return true;
            default:
                return block.isSolid() && !block.isTransparent();
        }
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
