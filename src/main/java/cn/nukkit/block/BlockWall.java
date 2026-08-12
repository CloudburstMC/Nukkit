package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.custom.properties.BlockProperties;
import cn.nukkit.block.custom.properties.BlockProperty;
import cn.nukkit.block.custom.properties.EnumBlockProperty;
import cn.nukkit.block.properties.BlockPropertiesHelper;
import cn.nukkit.block.properties.VanillaProperties;
import cn.nukkit.block.properties.WallConnectionType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockWall extends BlockTransparentMeta implements BlockPropertiesHelper {

    private static final double MIN_POST_BB = 5.0 / 16;
    private static final double MAX_POST_BB = 11.0 / 16;

    public static final BlockProperty<WallType> WALL_TYPE = new EnumBlockProperty<>("wall_type", false, WallType.values());

    protected static final BlockProperties PROPERTIES = new BlockProperties(
            WALL_TYPE,
            VanillaProperties.WALL_CONNECTION_TYPE_EAST,
            VanillaProperties.WALL_CONNECTION_TYPE_NORTH,
            VanillaProperties.WALL_CONNECTION_TYPE_SOUTH,
            VanillaProperties.WALL_CONNECTION_TYPE_WEST,
            VanillaProperties.WALL_POST
    );

    private static final BlockFace[] HORIZONTAL_FACES = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};

    @SuppressWarnings("unchecked")
    private static final BlockProperty<WallConnectionType>[] CONNECTIONS = new BlockProperty[]{
            VanillaProperties.WALL_CONNECTION_TYPE_SOUTH,
            VanillaProperties.WALL_CONNECTION_TYPE_WEST,
            VanillaProperties.WALL_CONNECTION_TYPE_NORTH,
            VanillaProperties.WALL_CONNECTION_TYPE_EAST
    };

    public static final int NONE_MOSSY_WALL = 0;
    public static final int MOSSY_WALL = 1;

    public BlockWall() {
        this(0);
    }

    public BlockWall(int meta) {
        super(meta);
    }

    @Override
    public BlockProperties getBlockProperties() {
        return PROPERTIES;
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

    @Override
    public String getName() {
        return this.getWallType().getTypeName();
    }

    public WallType getWallType() {
        return WallType.fromMeta(this.getDamage());
    }

    public void setWallType(WallType wallType) {
        this.setPropertyValue(WALL_TYPE, wallType);
    }

    public boolean isWallPost() {
        return this.getBooleanValue(VanillaProperties.WALL_POST);
    }

    public void setWallPost(boolean wallPost) {
        this.setBooleanValue(VanillaProperties.WALL_POST, wallPost);
    }

    public WallConnectionType getConnectionType(BlockFace blockFace) {
        int index = blockFace.getHorizontalIndex();
        if (index < 0) {
            return WallConnectionType.NONE;
        }
        try {
            return this.getPropertyValue(CONNECTIONS[index]);
        } catch (RuntimeException e) {
            return WallConnectionType.NONE;
        }
    }

    public boolean setConnection(BlockFace blockFace, WallConnectionType type) {
        int index = blockFace.getHorizontalIndex();
        if (index < 0) {
            return false;
        }
        this.setPropertyValue(CONNECTIONS[index], type);
        return true;
    }

    public boolean isConnected(BlockFace face) {
        return this.getConnectionType(face) != WallConnectionType.NONE;
    }

    public boolean hasConnections() {
        for (BlockFace face : HORIZONTAL_FACES) {
            if (this.isConnected(face)) {
                return true;
            }
        }
        return false;
    }

    public Map<BlockFace, WallConnectionType> getWallConnections() {
        EnumMap<BlockFace, WallConnectionType> connections = new EnumMap<>(BlockFace.class);
        for (BlockFace blockFace : HORIZONTAL_FACES) {
            WallConnectionType connectionType = this.getConnectionType(blockFace);
            if (connectionType != WallConnectionType.NONE) {
                connections.put(blockFace, connectionType);
            }
        }
        return connections;
    }

    public void clearConnections() {
        for (BlockFace face : HORIZONTAL_FACES) {
            this.setConnection(face, WallConnectionType.NONE);
        }
    }

    public boolean connect(BlockFace blockFace) {
        return this.connect(blockFace, true);
    }

    public boolean connect(BlockFace blockFace, boolean recheckPost) {
        if (blockFace.getHorizontalIndex() < 0) {
            return false;
        }
        return this.connect(blockFace, this.up(), recheckPost);
    }

    private boolean connect(BlockFace blockFace, Block above, boolean recheckPost) {
        WallConnectionType type = this.shouldBeTall(above, blockFace) ? WallConnectionType.TALL : WallConnectionType.SHORT;
        if (this.setConnection(blockFace, type)) {
            if (recheckPost) {
                this.setWallPost(this.recheckPostConditions(above));
            }
            return true;
        }
        return false;
    }

    public boolean disconnect(BlockFace blockFace) {
        if (blockFace.getHorizontalIndex() < 0) {
            return false;
        }

        if (this.setConnection(blockFace, WallConnectionType.NONE)) {
            this.autoUpdatePostFlag();
            return true;
        }
        return false;
    }

    public void autoUpdatePostFlag() {
        this.setWallPost(this.recheckPostConditions(this.up()));
    }

    public boolean autoConfigureState() {
        if (!this.canStoreConnections()) {
            return false;
        }

        int previousMeta = this.getDamage();

        this.setWallPost(true);

        Block above = this.up();

        for (BlockFace blockFace : HORIZONTAL_FACES) {
            Block side = this.getSide(blockFace);
            if (this.canConnect(side)) {
                this.connect(blockFace, above, false);
            } else {
                this.disconnect(blockFace);
            }
        }

        this.setWallPost(this.recheckPostConditions(above));
        return this.getDamage() != previousMeta;
    }

    protected boolean canStoreConnections() {
        return this.level == null || !(this.level.getProvider() instanceof Anvil);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.autoConfigureState();
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.autoConfigureState()) {
                this.level.setBlock(this, this, true, true);
            }
            return type;
        }

        return 0;
    }

    protected boolean shouldBeTall(Block above, BlockFace face) {
        switch (above.getId()) {
            case AIR:
                return false;
            case BELL:
                BlockBell bell = (BlockBell) above;
                return bell.getAttachmentType() == BlockBell.TYPE_ATTACHMENT_STANDING
                        && bell.getBlockFace().getAxis() != face.getAxis();
            default:
                if (above instanceof BlockWall) {
                    return ((BlockWall) above).getConnectionType(face) != WallConnectionType.NONE;
                }
                if (above instanceof BlockPressurePlateBase || above instanceof BlockStairs) {
                    return true;
                }
                return above.isSolid() && !above.isTransparent() || this.shouldBeTallBasedOnBoundingBox(above, face);
        }
    }

    protected boolean shouldBeTallBasedOnBoundingBox(Block above, BlockFace face) {
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

    private boolean recheckPostConditions(Block above) {
        BlockFace straight = this.straightConnectionFace();
        if (straight == null) {
            return true;
        }

        BlockFace.Axis axis = straight.getAxis();

        switch (above.getId()) {
            case CONDUIT:
            case STANDING_BANNER:
            case TURTLE_EGG:
                return true;

            case END_ROD:
                if (((Faceable) above).getBlockFace() == BlockFace.UP) {
                    return true;
                }
                break;

            case BELL:
                BlockBell bell = (BlockBell) above;
                if (bell.getAttachmentType() == BlockBell.TYPE_ATTACHMENT_STANDING
                        && bell.getBlockFace().getAxis() == axis) {
                    return true;
                }
                break;

            default:
                if (above instanceof BlockWall) {
                    if (((BlockWall) above).isWallPost()) {
                        return true;
                    }
                } else if (above instanceof BlockLantern) {
                    if ((above.getDamage() & 0x1) == 0) {
                        return true;
                    }
                } else if (above.getId() == LEVER || above instanceof BlockTorch || above instanceof BlockButton) {
                    if (((Faceable) above).getBlockFace() == BlockFace.UP) {
                        return true;
                    }
                } else if (above instanceof BlockFenceGate) {
                    if (((Faceable) above).getBlockFace().getAxis() == axis) {
                        return true;
                    }
                }
                break;
        }

        return above instanceof BlockSignPost;
    }

    public boolean isSameHeightStraight() {
        return this.straightConnectionFace() != null;
    }

    private BlockFace straightConnectionFace() {
        BlockFace first = null;
        int count = 0;
        for (BlockFace face : HORIZONTAL_FACES) {
            if (this.isConnected(face)) {
                count++;
                if (first == null) {
                    first = face;
                }
            }
        }

        if (count != 2) {
            return null;
        }
        return this.getConnectionType(first.getOpposite()) == this.getConnectionType(first) ? first : null;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        boolean configured = this.isWallPost() || this.hasConnections();

        boolean north = configured ? this.isConnected(BlockFace.NORTH) : this.canConnect(this.getSide(BlockFace.NORTH));
        boolean south = configured ? this.isConnected(BlockFace.SOUTH) : this.canConnect(this.getSide(BlockFace.SOUTH));
        boolean west = configured ? this.isConnected(BlockFace.WEST) : this.canConnect(this.getSide(BlockFace.WEST));
        boolean east = configured ? this.isConnected(BlockFace.EAST) : this.canConnect(this.getSide(BlockFace.EAST));

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

    public boolean canConnect(Block block) {
        if (block.getId() == GLASS) {
            return true;
        }
        if (block instanceof BlockThin || block instanceof BlockGlassStained || block instanceof BlockWall) {
            return true;
        }
        if (block instanceof BlockFenceGate) {
            return ((BlockFenceGate) block).getBlockFace().getAxis() != calculateAxis(this, block);
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

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return this.getWallType().getColor();
    }

    @Override
    public Item toItem() {
        int type = this.getWallType().ordinal();
        return new ItemBlock(Block.get(this.getId(), type), type, 1);
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }

    public static BlockFace.Axis calculateAxis(Vector3 base, Vector3 side) {
        Vector3 vector = side.subtract(base);
        return vector.x != 0 ? BlockFace.Axis.X : vector.z != 0 ? BlockFace.Axis.Z : BlockFace.Axis.Y;
    }

    public static BlockFace calculateFace(Vector3 base, Vector3 side) {
        Vector3 vector = side.subtract(base);
        BlockFace.Axis axis = vector.x != 0 ? BlockFace.Axis.X : vector.z != 0 ? BlockFace.Axis.Z : BlockFace.Axis.Y;
        double direction = axis == BlockFace.Axis.X ? vector.x : axis == BlockFace.Axis.Y ? vector.y : vector.z;
        return BlockFace.fromAxis(direction < 0 ? BlockFace.AxisDirection.NEGATIVE : BlockFace.AxisDirection.POSITIVE, axis);
    }

    public enum WallType {
        COBBLESTONE("Cobblestone", BlockColor.STONE_BLOCK_COLOR),
        MOSSY_COBBLESTONE("Mossy Cobblestone", BlockColor.STONE_BLOCK_COLOR),
        GRANITE("Granite", BlockColor.DIRT_BLOCK_COLOR),
        DIORITE("Diorite", BlockColor.QUARTZ_BLOCK_COLOR),
        ANDESITE("Andesite", BlockColor.STONE_BLOCK_COLOR),
        SANDSTONE("Sandstone", BlockColor.SAND_BLOCK_COLOR),
        BRICK("Brick", BlockColor.RED_BLOCK_COLOR),
        STONE_BRICK("Stone Brick", BlockColor.STONE_BLOCK_COLOR),
        MOSSY_STONE_BRICK("Mossy Stone Brick", BlockColor.STONE_BLOCK_COLOR),
        NETHER_BRICK("Nether Brick", BlockColor.NETHERRACK_BLOCK_COLOR),
        END_BRICK("End Stone Brick", BlockColor.SAND_BLOCK_COLOR),
        PRISMARINE("Prismarine", BlockColor.CYAN_BLOCK_COLOR),
        RED_SANDSTONE("Red Sandstone", BlockColor.ORANGE_BLOCK_COLOR),
        RED_NETHER_BRICK("Red Nether Brick", BlockColor.NETHERRACK_BLOCK_COLOR);

        private static final WallType[] VALUES = values();

        private final BlockColor color;
        private final String typeName;
        private final String identifier;

        WallType(String name, BlockColor color) {
            this.color = color;
            this.typeName = name + " Wall";
            this.identifier = "minecraft:" + name.toLowerCase(Locale.ROOT).replace(' ', '_') + "_wall";
        }

        public static WallType fromMeta(int meta) {
            int type = meta & 0xF;
            return type < VALUES.length ? VALUES[type] : COBBLESTONE;
        }

        public String getIdentifier() {
            return this.identifier;
        }

        public BlockColor getColor() {
            return this.color;
        }

        public String getTypeName() {
            return this.typeName;
        }
    }
}