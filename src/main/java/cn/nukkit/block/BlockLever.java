package cn.nukkit.block;

import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

/**
 * @author Nukkit Project Team
 */
public class BlockLever extends FloodableBlock implements Faceable {

    public BlockLever(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public float getHardness() {
        return 0.5f;
    }

    @Override
    public float getResistance() {
        return 2.5f;
    }

    @Override
    public Item toItem() {
        return Item.get(id, 0);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{toItem()};
    }

    public boolean isPowerOn() {
        return (this.getMeta() & 0x08) > 0;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, isPowerOn() ? 15 : 0, isPowerOn() ? 0 : 15));
        this.setMeta(this.getMeta() ^ 0x08);

        this.getLevel().setBlock(this.getPosition(), this, false, true);
        this.getLevel().addSound(this.getPosition(), Sound.RANDOM_CLICK, 0.8f, isPowerOn() ? 0.58f : 0.5f);

        LeverOrientation orientation = LeverOrientation.byMetadata(this.isPowerOn() ? this.getMeta() ^ 0x08 : this.getMeta());
        BlockFace face = orientation.getFacing();
        //this.level.updateAroundRedstone(this.getPosition(), null);
        this.level.updateAroundRedstone(face.getOpposite().getOffset(this.getPosition()), isPowerOn() ? face : null);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            int face = this.isPowerOn() ? this.getMeta() ^ 0x08 : this.getMeta();
            BlockFace faces = LeverOrientation.byMetadata(face).getFacing().getOpposite();
            if (!this.getSide(faces).isSolid()) {
                this.level.useBreakOn(this.getPosition());
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (target.isNormalBlock()) {
            this.setMeta(LeverOrientation.forFacings(face, player.getHorizontalFacing()).getMetadata());
            this.getLevel().setBlock(block.getPosition(), this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        super.onBreak(item);

        if (isPowerOn()) {
            BlockFace face = LeverOrientation.byMetadata(this.isPowerOn() ? this.getMeta() ^ 0x08 : this.getMeta()).getFacing();
            this.level.updateAround(face.getOpposite().getOffset(this.getPosition()));
        }
        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return isPowerOn() ? 15 : 0;
    }

    public int getStrongPower(BlockFace side) {
        return !isPowerOn() ? 0 : LeverOrientation.byMetadata(this.isPowerOn() ? this.getMeta() ^ 0x08 : this.getMeta()).getFacing() == side ? 15 : 0;
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
                META_LOOKUP[face.getMetadata()] = face;
            }
        }
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x07);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }

    @Override
    public boolean canWaterlogFlowing() {
        return true;
    }
}
