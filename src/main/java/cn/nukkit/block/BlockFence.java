package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/7 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFence extends BlockTransparentMeta implements ConnectionBlock {

    public static final int FENCE_OAK = 0;
    public static final int FENCE_SPRUCE = 1;
    public static final int FENCE_BIRCH = 2;
    public static final int FENCE_JUNGLE = 3;
    public static final int FENCE_ACACIA = 4;
    public static final int FENCE_DARK_OAK = 5;

    public BlockFence() {
        this(0);
    }

    public BlockFence(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FENCE;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    private static final String[] NAMES = {
            "Oak Fence",
            "Spruce Fence",
            "Birch Fence",
            "Jungle Fence",
            "Acacia Fence",
            "Dark Oak Fence",
            "",
            ""
    };

    @Override
    public String getName() {
        return NAMES[this.getDamage() & 0x07];
    }

    protected AxisAlignedBB recalculateBoundingBox() {
        boolean north = this.canConnect(this.north(), BlockFace.NORTH);
        boolean south = this.canConnect(this.south(), BlockFace.SOUTH);
        boolean west = this.canConnect(this.west(), BlockFace.WEST);
        boolean east = this.canConnect(this.east(), BlockFace.EAST);
        double n = north ? 0 : 0.375;
        double s = south ? 1 : 0.625;
        double w = west ? 0 : 0.375;
        double e = east ? 1 : 0.625;
        return new SimpleAxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1.5,
                this.z + s
        );
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    /**
     * @return true if the block is a fence this fence connects to
     */
    protected boolean isSameKind(Block block) {
        return block instanceof BlockFence && !(block instanceof BlockFenceNetherBrick);
    }

    @Override
    public boolean canConnect(Block block) {
        return this.isSameKind(block) || block instanceof BlockFenceGate || !(block instanceof BlockStairs) && block.isSolid() && !block.isTransparent();
    }

    @Override
    public boolean canConnect(Block block, BlockFace face) {
        if (block instanceof BlockStairs) {
            return ConnectionBlock.connectsToStairs(block, face);
        }
        return this.isSameKind(block) || ConnectionBlock.connectsToGate(block, face) || block.isSolid() && !block.isTransparent();
    }

    @Override
    public int getConnectionShift() {
        return 3;
    }

    @Override
    public int getConnectionTypeCount() {
        return this.getId() == FENCE ? 6 : 1;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.updateConnections();
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.updateConnections()) {
                this.level.setBlock(this, this, true, false);
            }
            return type;
        }
        return 0;
    }

    @Override
    public BlockColor getColor() {
        switch (this.getDamage() & 0x07) {
            default:
            case FENCE_OAK: //OAK
                return BlockColor.WOOD_BLOCK_COLOR;
            case FENCE_SPRUCE: //SPRUCE
                return BlockColor.SPRUCE_BLOCK_COLOR;
            case FENCE_BIRCH: //BIRCH
                return BlockColor.SAND_BLOCK_COLOR;
            case FENCE_JUNGLE: //JUNGLE
                return BlockColor.DIRT_BLOCK_COLOR;
            case FENCE_ACACIA: //ACACIA
                return BlockColor.ORANGE_BLOCK_COLOR;
            case FENCE_DARK_OAK: //DARK OAK
                return BlockColor.BROWN_BLOCK_COLOR;
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, this.getTypeDamage());
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }
}
