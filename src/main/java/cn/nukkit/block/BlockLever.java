package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;

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
        return new ItemBlock(this, 0);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{toItem()};
    }

    public boolean isPowerOn() {
        return (this.getDamage() & 0x08) > 0;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, isPowerOn() ? 15 : 0, isPowerOn() ? 0 : 15));
        this.setDamage(this.getDamage() ^ 0x08);

        boolean redstone = this.level.getServer().isRedstoneEnabled();

        this.getLevel().setBlock(this, this, false, !redstone);
        this.getLevel().addSound(this, Sound.RANDOM_CLICK, 0.8f, isPowerOn() ? 0.58f : 0.5f );

        LeverOrientation orientation = LeverOrientation.byMetadata(this.isPowerOn() ? this.getDamage() ^ 0x08 : this.getDamage());
        BlockFace face = orientation.getFacing();

        if (redstone) {
            Block target = this.getSide(face.getOpposite());
            target.onUpdate(Level.BLOCK_UPDATE_REDSTONE);

            this.level.updateAroundRedstone(this.getLocation(), isPowerOn() ? face.getOpposite() : null);
            this.level.updateAroundRedstone(target.getLocation(), isPowerOn() ? face : null);
        }
        return true;
    }

    @PowerNukkitDifference(info = "Now, can be placed on solid blocks", since= "1.4.0.0-PN")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            int face = this.isPowerOn() ? this.getDamage() ^ 0x08 : this.getDamage();
            BlockFace blockFace = LeverOrientation.byMetadata(face).getFacing().getOpposite();
            Block side = this.getSide(blockFace);
            if (!isSupportValid(side, blockFace.getOpposite())) {
                this.level.useBreakOn(this);
            }
        }
        return 0;
    }

    @PowerNukkitDifference(info = "Allows to be placed on walls", since = "1.3.0.0-PN")
    @PowerNukkitDifference(info = "Now, can be placed on solid blocks and always returns false if the placement fails", since= "1.4.0.0-PN")
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (face == BlockFace.DOWN) {
            return false;
        }
        
        if (!isSupportValid(target, face)) {
            return false;
        }
        
        this.setDamage(LeverOrientation.forFacings(face, player.getHorizontalFacing()).getMetadata());
        return this.getLevel().setBlock(block, this, true, true);
    }

    /**
     * Check if the given block and its block face is a valid support for a lever
     * @param support The block that the lever is being placed against
     * @param face The face that the torch will be touching the block
     * @return If the support and face combinations can hold the lever
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static boolean isSupportValid(Block support, BlockFace face) {
        if (support.isSolid(face)) {
            return true;
        }

        if (support instanceof BlockWallBase || support instanceof BlockFence) {
            return face == BlockFace.UP;
        }
        
        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);

        if (isPowerOn()) {
            BlockFace face = LeverOrientation.byMetadata(this.isPowerOn() ? this.getDamage() ^ 0x08 : this.getDamage()).getFacing();
            this.level.updateAround(this.getLocation().getSide(face.getOpposite()));
        }
        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return isPowerOn() ? 15 : 0;
    }

    public int getStrongPower(BlockFace side) {
        return !isPowerOn() ? 0 : LeverOrientation.byMetadata(this.isPowerOn() ? this.getDamage() ^ 0x08 : this.getDamage()).getFacing() == side ? 15 : 0;
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

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @PowerNukkitDifference(info = "Fixed the directions", since = "1.3.0.0-PN")
    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & 0x07).getOpposite();
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }
}
