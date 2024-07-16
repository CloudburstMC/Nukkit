package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Rail;

/**
 * @author Nukkit Project Team
 */
public class BlockRailActivator extends BlockRail {

    public BlockRailActivator(int meta) {
        super(meta);
    }

    public BlockRailActivator() {
        this(0);
        canBePowered = true;
    }

    @Override
    public String getName() {
        return "Activator Rail";
    }

    @Override
    public int getId() {
        return ACTIVATOR_RAIL;
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
                level.updateAround(getSideVec(BlockFace.DOWN));
                if (getOrientation().isAscending()) {
                    level.updateAround(getSideVec(BlockFace.UP));
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
        if (power >= 8) {
            return false;
        }
        int dx = pos.getFloorX();
        int dy = pos.getFloorY();
        int dz = pos.getFloorZ();

        BlockRail block;
        Block block2 = level.getBlock(dx, dy, dz);

        if (Rail.isRailBlock(block2)) {
            block = (BlockRail) block2;
        } else {
            return false;
        }

        Rail.Orientation base = null;
        boolean onStraight = true;

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
                return false;
        }

        return canPowered(dx, dy, dz, base, power, relative)
                || onStraight && canPowered(dx, dy - 1, dz, base, power, relative);
    }

    protected boolean canPowered(int x, int y, int z, Rail.Orientation state, int power, boolean relative) {
        Block block = level.getBlock(x, y, z);

        if (!(block instanceof BlockRailActivator)) {
            return false;
        }

        Rail.Orientation base = ((BlockRailActivator) block).getOrientation();

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

    @Override
    public double getHardness() {
        return 0.5; // 0.7
    }
}
