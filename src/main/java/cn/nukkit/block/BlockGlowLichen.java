package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.properties.BlockPropertiesHelper;
import cn.nukkit.block.custom.properties.BlockProperties;
import cn.nukkit.block.custom.properties.BooleanBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.EnumSet;
import java.util.Set;

public class BlockGlowLichen extends BlockTransparentMeta implements BlockPropertiesHelper {

    // Currently multi_face_direction_bits: 0x01 - down, 0x02 - up, 0x04 - north, 0x08 - south, 0x10 - west, 0x20 - east
    private static final BooleanBlockProperty CONNECTION_DOWN = new BooleanBlockProperty("connection_down", false);
    private static final BooleanBlockProperty CONNECTION_UP = new BooleanBlockProperty("connection_up", false);
    private static final BooleanBlockProperty CONNECTION_NORTH = new BooleanBlockProperty("connection_north", false);
    private static final BooleanBlockProperty CONNECTION_SOUTH = new BooleanBlockProperty("connection_south", false);
    private static final BooleanBlockProperty CONNECTION_WEST = new BooleanBlockProperty("connection_west", false);
    private static final BooleanBlockProperty CONNECTION_EAST = new BooleanBlockProperty("connection_east", false);

    private static final BlockProperties PROPERTIES = new BlockProperties(CONNECTION_DOWN, CONNECTION_UP, CONNECTION_NORTH, CONNECTION_SOUTH, CONNECTION_WEST, CONNECTION_EAST);

    public BlockGlowLichen() {
        this(0);
    }

    public BlockGlowLichen(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return GLOW_LICHEN;
    }

    @Override
    public String getName() {
        return "Glow Lichen";
    }

    @Override
    public BlockProperties getBlockProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!this.canPlaceOn(block.down(), target) || !target.isSolid()) {
            return false;
        }

        if (block.getId() == GLOW_LICHEN) {
            this.setDamage(block.getDamage());
        } else {
            this.setDamage(0);
        }

        this.setBlockFace(face.getOpposite(), true);
        this.getLevel().setBlock(this, this, false, true);
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[] { this.toItem() };
        }
        return new Item[0];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.getLevel().useBreakOn(this, null, null, true);
        } else if (type != Level.BLOCK_UPDATE_NORMAL) {
           return type;
        }

        boolean update = false;
        boolean support = false;

        Set<BlockFace> faces = this.getSupportedFaces();
        for (BlockFace face : faces) {
            Block block = this.getLevel().getBlock(this.getSide(face));
            if (block.isSolid()) {
                support = true;
            } else {
                update = true;
                this.setBlockFace(face, false);
            }
        }

        if (!support) {
            this.getLevel().scheduleUpdate(this, 1);
        } else if (update) {
            this.getLevel().setBlock(this, this, false, true);
        }
        return type;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId()), 0, 1);
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    public void setBlockFace(BlockFace face, boolean value) {
        switch (face) {
            case UP:
                this.setBooleanValue(CONNECTION_UP, value);
                break;
            case DOWN:
                this.setBooleanValue(CONNECTION_DOWN, value);
                break;
            case NORTH:
                this.setBooleanValue(CONNECTION_NORTH, value);
                break;
            case SOUTH:
                this.setBooleanValue(CONNECTION_SOUTH, value);
                break;
            case WEST:
                this.setBooleanValue(CONNECTION_WEST, value);
                break;
            case EAST:
                this.setBooleanValue(CONNECTION_EAST, value);
                break;

        }
    }

    public boolean hasBlockFace(BlockFace face) {
        switch (face) {
            case UP:
                return this.getBooleanValue(CONNECTION_UP);
            case DOWN:
                return this.getBooleanValue(CONNECTION_DOWN);
            case NORTH:
                return this.getBooleanValue(CONNECTION_NORTH);
            case SOUTH:
                return this.getBooleanValue(CONNECTION_SOUTH);
            case WEST:
                return this.getBooleanValue(CONNECTION_WEST);
            case EAST:
                return this.getBooleanValue(CONNECTION_EAST);

        }
        return false;
    }

    public Set<BlockFace> getSupportedFaces() {
        EnumSet<BlockFace> faces = EnumSet.noneOf(BlockFace.class);
        for (BlockFace face : BlockFace.values()) {
            if (this.hasBlockFace(face)) {
                faces.add(face);
            }
        }
        return faces;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }
}
