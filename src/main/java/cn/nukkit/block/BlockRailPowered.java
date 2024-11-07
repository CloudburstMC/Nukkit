package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Rail;

/**
 * Created by Snake1999 on 2016/1/11.
 * Contributed by: larryTheCoder on 2017/7/18.
 * <p>
 * Nukkit Project,
 * Minecart and Riding Project,
 * Package cn.nukkit.block in project Nukkit.
 */
public class BlockRailPowered extends BlockRail {

    public BlockRailPowered() {
        this(0);
        canBePowered = true;
    }

    public BlockRailPowered(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POWERED_RAIL;
    }

    @Override
    public String getName() {
        return "Powered Rail";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE || type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (super.onUpdate(type) == Level.BLOCK_UPDATE_NORMAL) {
                return 0; // Already broken
            }

            boolean isPowered = level.isBlockPowered(this)
                    || checkSurrounding(this, true, 0)
                    || checkSurrounding(this, false, 0);

            if (isActive() != isPowered) {
                setActive(isPowered);
                level.updateAround(this.getSideVec(BlockFace.DOWN));
                if (getOrientation().isAscending()) {
                    level.updateAround(this.getSideVec(BlockFace.UP));
                }
            }
            return type;
        }
        return 0;
    }

    /**
     * Check the surrounding of the rail
     *
     * @param pos      The rail position
     * @param relative The relative of the rail that will be checked
     * @param power    The count of the rail that had been counted
     * @return Boolean of the surrounding area. Where the powered rail on!
     */
    protected boolean checkSurrounding(Vector3 pos, boolean relative, int power) {
        // The powered rail can power up to 8 blocks only
        if (power >= 8) {
            return false;
        }
        // The position of the floor numbers
        int dx = pos.getFloorX();
        int dy = pos.getFloorY();
        int dz = pos.getFloorZ();
        // First: get the base block
        BlockRail block;
        Block block2 = level.getBlock(dx, dy, dz);

        // Second: check if the rail is Powered rail
        if (Rail.isRailBlock(block2)) {
            block = (BlockRail) block2;
        } else {
            return false;
        }

        // Used to check if the next ascending rail should be what
        Rail.Orientation base = null;
        boolean onStraight = true;
        // Third: Recalculate the base position
        switch (block.getOrientation()) {
            case STRAIGHT_NORTH_SOUTH:
                if (relative) {
                    dz++;
                } else {
                    dz--;
                }
                break;
            case STRAIGHT_EAST_WEST:
                if (relative) {
                    dx--;
                } else {
                    dx++;
                }
                break;
            case ASCENDING_EAST:
                if (relative) {
                    dx--;
                } else {
                    dx++;
                    dy++;
                    onStraight = false;
                }
                base = Rail.Orientation.STRAIGHT_EAST_WEST;
                break;
            case ASCENDING_WEST:
                if (relative) {
                    dx--;
                    dy++;
                    onStraight = false;
                } else {
                    dx++;
                }
                base = Rail.Orientation.STRAIGHT_EAST_WEST;
                break;
            case ASCENDING_NORTH:
                if (relative) {
                    dz++;
                } else {
                    dz--;
                    dy++;
                    onStraight = false;
                }
                base = Rail.Orientation.STRAIGHT_NORTH_SOUTH;
                break;
            case ASCENDING_SOUTH:
                if (relative) {
                    dz++;
                    dy++;
                    onStraight = false;
                } else {
                    dz--;
                }
                base = Rail.Orientation.STRAIGHT_NORTH_SOUTH;
                break;
            default:
                // Unable to determinate the rail orientation
                // Wrong rail?
                return false;
        }
        // Next check the if rail is on power state
        return canPowered(dx, dy, dz, base, power, relative)
                || onStraight && canPowered(dx, dy - 1, dz, base, power, relative);
    }

    protected boolean canPowered(int x, int y, int z, Rail.Orientation state, int power, boolean relative) {
        Block block = level.getBlock(x, y, z);
        // What! My block is air??!! Impossible! XD
        if (!(block instanceof BlockRailPowered)) {
            return false;
        }

        // Sometimes the rails are diffrent orientation
        Rail.Orientation base = ((BlockRailPowered) block).getOrientation();

        // Possible way how to know when the rail is activated is rail were directly powered
        // OR recheck the surrounding... Which will returns here =w=        
        return (state != Rail.Orientation.STRAIGHT_EAST_WEST
                || base != Rail.Orientation.STRAIGHT_NORTH_SOUTH
                && base != Rail.Orientation.ASCENDING_NORTH
                && base != Rail.Orientation.ASCENDING_SOUTH)
                && (state != Rail.Orientation.STRAIGHT_NORTH_SOUTH
                || base != Rail.Orientation.STRAIGHT_EAST_WEST
                && base != Rail.Orientation.ASCENDING_EAST
                && base != Rail.Orientation.ASCENDING_WEST)
                && (level.isBlockPowered(block) || checkSurrounding(block, relative, power + 1));
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                toItem()
        };
    }
}
