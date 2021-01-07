package cn.nukkit.math;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * Represents a 16 direction compass rose.
 * <p>https://en.wikipedia.org/wiki/Compass_rose#/media/File:Brosen_windrose.svg
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public enum CompassRoseDirection {
    NORTH(0, -1, BlockFace.NORTH),
    EAST(1, 0, BlockFace.EAST),
    SOUTH(0, 1, BlockFace.SOUTH),
    WEST(-1, 0, BlockFace.WEST),
    NORTH_EAST(NORTH, EAST, BlockFace.NORTH),
    NORTH_WEST(NORTH, WEST, BlockFace.WEST),
    SOUTH_EAST(SOUTH, EAST, BlockFace.EAST),
    SOUTH_WEST(SOUTH, WEST, BlockFace.SOUTH),
    WEST_NORTH_WEST(WEST, NORTH_WEST, BlockFace.WEST),
    NORTH_NORTH_WEST(NORTH, NORTH_WEST, BlockFace.NORTH),
    NORTH_NORTH_EAST(NORTH, NORTH_EAST, BlockFace.NORTH),
    EAST_NORTH_EAST(EAST, NORTH_EAST, BlockFace.EAST),
    EAST_SOUTH_EAST(EAST, SOUTH_EAST, BlockFace.EAST),
    SOUTH_SOUTH_EAST(SOUTH, SOUTH_EAST, BlockFace.SOUTH),
    SOUTH_SOUTH_WEST(SOUTH, SOUTH_WEST, BlockFace.SOUTH),
    WEST_SOUTH_WEST(WEST, SOUTH_WEST, BlockFace.WEST);

    private final int modX;
    private final int modZ;
    private final BlockFace closestBlockFace;

    CompassRoseDirection(int modX, int modZ, BlockFace closestBlockFace) {
        this.modX = modX;
        this.modZ = modZ;
        this.closestBlockFace = closestBlockFace;
    }

    CompassRoseDirection(CompassRoseDirection face1, CompassRoseDirection face2, BlockFace closestBlockFace) {
        this.modX = face1.getModX() + face2.getModX();
        this.modZ = face1.getModZ() + face2.getModZ();
        this.closestBlockFace = closestBlockFace;
    }

    /**
     * Get the amount of X-coordinates to modify to get the represented block
     *
     * @return Amount of X-coordinates to modify
     */
    public int getModX() {
        return modX;
    }

    /**
     * Get the amount of Z-coordinates to modify to get the represented block
     *
     * @return Amount of Z-coordinates to modify
     */
    public int getModZ() {
        return modZ;
    }

    /**
     * Gets the closest face for this direction. For example, NNE returns N.
     * Even directions like NE will return the direction to the left, N in this case.
     */
    public BlockFace getClosestBlockFace() {
        return closestBlockFace;
    }

    public CompassRoseDirection getOppositeFace() {
        switch (this) {
            case NORTH:
                return CompassRoseDirection.SOUTH;

            case SOUTH:
                return CompassRoseDirection.NORTH;

            case EAST:
                return CompassRoseDirection.WEST;

            case WEST:
                return CompassRoseDirection.EAST;

            case NORTH_EAST:
                return CompassRoseDirection.SOUTH_WEST;

            case NORTH_WEST:
                return CompassRoseDirection.SOUTH_EAST;

            case SOUTH_EAST:
                return CompassRoseDirection.NORTH_WEST;

            case SOUTH_WEST:
                return CompassRoseDirection.NORTH_EAST;

            case WEST_NORTH_WEST:
                return CompassRoseDirection.EAST_SOUTH_EAST;

            case NORTH_NORTH_WEST:
                return CompassRoseDirection.SOUTH_SOUTH_EAST;

            case NORTH_NORTH_EAST:
                return CompassRoseDirection.SOUTH_SOUTH_WEST;

            case EAST_NORTH_EAST:
                return CompassRoseDirection.WEST_SOUTH_WEST;

            case EAST_SOUTH_EAST:
                return CompassRoseDirection.WEST_NORTH_WEST;

            case SOUTH_SOUTH_EAST:
                return CompassRoseDirection.NORTH_NORTH_WEST;

            case SOUTH_SOUTH_WEST:
                return CompassRoseDirection.NORTH_NORTH_EAST;

            case WEST_SOUTH_WEST:
                return CompassRoseDirection.EAST_NORTH_EAST;
            
            default:
                throw new IncompatibleClassChangeError("New values was added to the enum");
        }
    }
}

