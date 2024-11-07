package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * @author Nukkit Project Team
 */
public class BlockLever extends BlockFlowable implements Faceable {

    public BlockLever() {
        this(0);
    }

    public BlockLever(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Lever";
    }

    @Override
    public int getId() {
        return LEVER;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0.5d;
    }

    @Override
    public double getResistance() {
        return 2.5d;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{toItem()};
    }

    public boolean isPowerOn() {
        return (this.getDamage() & 0x08) > 0;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, isPowerOn() ? 15 : 0, isPowerOn() ? 0 : 15));
        this.setDamage(this.getDamage() ^ 0x08);

        this.getLevel().setBlock(this, this, false, true);
        if (this.isPowerOn()) {
            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_POWER_ON);
        } else {
            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_POWER_OFF);
        }

        LeverOrientation orientation = LeverOrientation.byMetadata(this.isPowerOn() ? this.getDamage() ^ 0x08 : this.getDamage());
        BlockFace face = orientation.getFacing();
        level.updateAroundRedstone(this, null);
        this.level.updateAroundRedstone(this.getSideVec(face.getOpposite()), isPowerOn() ? face : null);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            int face = this.isPowerOn() ? this.getDamage() ^ 0x08 : this.getDamage();
            BlockFace faces = LeverOrientation.byMetadata(face).getFacing().getOpposite();
            if (!isSupportValid(this.getSide(faces))) {
                this.level.useBreakOn(this);
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        LeverOrientation faces = LeverOrientation.forFacings(face, player.getHorizontalFacing());
        this.setDamage(faces.getMetadata());
        if (!isSupportValid(this.getSide(faces.facing.getOpposite()))) {
            return false;
        }
        return this.getLevel().setBlock(block, this, true, true);
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);

        if (isPowerOn()) {
            BlockFace face = LeverOrientation.byMetadata(this.getDamage() ^ 0x08).getFacing();
            this.level.updateAround(this.getSideVec(face.getOpposite()));
        }
        return true;
    }

    private boolean isSupportValid(Block block) {
        if (!block.isTransparent()) {
            return true;
        }
        if (BlockFace.fromIndex(isPowerOn() ? getDamage() ^ 0x08 : getDamage()) == BlockFace.DOWN) {
            return Block.canStayOnFullSolid(block);
        }
        return Block.canConnectToFullSolid(block);
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return isPowerOn() ? 15 : 0;
    }

    public int getStrongPower(BlockFace side) {
        if (!isPowerOn()) {
            return 0;
        } else {
            return LeverOrientation.byMetadata(this.getDamage() ^ 0x08).getFacing() == side ? 15 : 0;
        }
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    public enum LeverOrientation {
        DOWN_X(0, "down_x", BlockFace.DOWN),
        EAST(1, "east", BlockFace.EAST),
        WEST(2, "west", BlockFace.WEST),
        SOUTH(3, "south", BlockFace.SOUTH),
        NORTH(4, "north", BlockFace.NORTH),
        UP_Z(5, "up_z", BlockFace.UP),
        UP_X(6, "up_x", BlockFace.UP),
        DOWN_Z(7, "down_z", BlockFace.DOWN);

        private static final LeverOrientation[] META_LOOKUP = new LeverOrientation[values().length];
        private final int meta;
        private final String name;
        private final BlockFace facing;

        LeverOrientation(int meta, String name, BlockFace face) {
            this.meta = meta;
            this.name = name;
            this.facing = face;
        }

        public int getMetadata() {
            return this.meta;
        }

        public BlockFace getFacing() {
            return this.facing;
        }

        public String toString() {
            return this.name;
        }

        public static LeverOrientation byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public static LeverOrientation forFacings(BlockFace clickedSide, BlockFace playerDirection) {
            switch (clickedSide) {
                case DOWN:
                    switch (playerDirection.getAxis()) {
                        case X:
                            return DOWN_X;

                        case Z:
                            return DOWN_Z;

                        default:
                            throw new IllegalArgumentException("Invalid entityFacing " + playerDirection + " for facing " + clickedSide);
                    }

                case UP:
                    switch (playerDirection.getAxis()) {
                        case X:
                            return UP_X;

                        case Z:
                            return UP_Z;

                        default:
                            throw new IllegalArgumentException("Invalid entityFacing " + playerDirection + " for facing " + clickedSide);
                    }

                case NORTH:
                    return NORTH;

                case SOUTH:
                    return SOUTH;

                case WEST:
                    return WEST;

                case EAST:
                    return EAST;

                default:
                    throw new IllegalArgumentException("Invalid facing: " + clickedSide);
            }
        }

        public String getName() {
            return this.name;
        }

        static {
            for (LeverOrientation face : values()) {
                META_LOOKUP[face.meta] = face;
            }
        }
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.FLOW_INTO_BLOCK;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }
}
