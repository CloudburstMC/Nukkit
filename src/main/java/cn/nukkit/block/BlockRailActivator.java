package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Rail;
import cn.nukkit.utils.RedstoneComponent;

/**
 * @author Nukkit Project Team
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
public class BlockRailActivator extends BlockRail implements RedstoneComponent {

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

    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE || type == Level.BLOCK_UPDATE_SCHEDULED) {
            super.onUpdate(type);
            boolean wasPowered = isActive();
            boolean isPowered = this.isGettingPower()
                    || checkSurrounding(this, true, 0)
                    || checkSurrounding(this, false, 0);
            boolean hasUpdate = false;

            if (wasPowered != isPowered) {
                setActive(isPowered);
                hasUpdate = true;
            }

            if (hasUpdate) {
                level.updateAround(down());
                if (getOrientation().isAscending()) {
                    level.updateAround(up());
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
        Block block2 = level.getBlock(new Vector3(dx, dy, dz));

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

        return canPowered(new Vector3(dx, dy, dz), base, power, relative)
                || onStraight && canPowered(new Vector3(dx, dy - 1, dz), base, power, relative);
    }

    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    protected boolean canPowered(Vector3 pos, Rail.Orientation state, int power, boolean relative) {
        Block block = level.getBlock(pos);

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
                && (this.isGettingPower() || checkSurrounding(pos, relative, power + 1));
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(Item.ACTIVATOR_RAIL, 0, 1)
        };
    }

}
