package cn.nukkit.math;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockSignPost;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;

/**
 * Represents a 16 direction compass rose.
 * <p>https://en.wikipedia.org/wiki/Compass_rose#/media/File:Brosen_windrose.svg
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public enum CompassRoseDirection {
    @PowerNukkitOnly @Since("1.4.0.0-PN") NORTH(0, -1, BlockFace.NORTH, 0),
    @PowerNukkitOnly @Since("1.4.0.0-PN") EAST(1, 0, BlockFace.EAST, 90),
    @PowerNukkitOnly @Since("1.4.0.0-PN") SOUTH(0, 1, BlockFace.SOUTH, 180),
    @PowerNukkitOnly @Since("1.4.0.0-PN") WEST(-1, 0, BlockFace.WEST, 270),
    @PowerNukkitOnly @Since("1.4.0.0-PN") NORTH_EAST(NORTH, EAST, BlockFace.NORTH, 45),
    @PowerNukkitOnly @Since("1.4.0.0-PN") NORTH_WEST(NORTH, WEST, BlockFace.WEST, 315),
    @PowerNukkitOnly @Since("1.4.0.0-PN") SOUTH_EAST(SOUTH, EAST, BlockFace.EAST, 135),
    @PowerNukkitOnly @Since("1.4.0.0-PN") SOUTH_WEST(SOUTH, WEST, BlockFace.SOUTH, 225),
    @PowerNukkitOnly @Since("1.4.0.0-PN") WEST_NORTH_WEST(WEST, NORTH_WEST, BlockFace.WEST, 292.5),
    @PowerNukkitOnly @Since("1.4.0.0-PN") NORTH_NORTH_WEST(NORTH, NORTH_WEST, BlockFace.NORTH, 337.5),
    @PowerNukkitOnly @Since("1.4.0.0-PN") NORTH_NORTH_EAST(NORTH, NORTH_EAST, BlockFace.NORTH, 22.5),
    @PowerNukkitOnly @Since("1.4.0.0-PN") EAST_NORTH_EAST(EAST, NORTH_EAST, BlockFace.EAST, 67.5),
    @PowerNukkitOnly @Since("1.4.0.0-PN") EAST_SOUTH_EAST(EAST, SOUTH_EAST, BlockFace.EAST, 112.5),
    @PowerNukkitOnly @Since("1.4.0.0-PN") SOUTH_SOUTH_EAST(SOUTH, SOUTH_EAST, BlockFace.SOUTH, 157.5),
    @PowerNukkitOnly @Since("1.4.0.0-PN") SOUTH_SOUTH_WEST(SOUTH, SOUTH_WEST, BlockFace.SOUTH, 202.5),
    @PowerNukkitOnly @Since("1.4.0.0-PN") WEST_SOUTH_WEST(WEST, SOUTH_WEST, BlockFace.WEST, 247.5);

    private final int modX;
    private final int modZ;
    private final BlockFace closestBlockFace;
    private final float yaw;

    CompassRoseDirection(int modX, int modZ, BlockFace closestBlockFace, double yaw) {
        this.modX = modX;
        this.modZ = modZ;
        this.closestBlockFace = closestBlockFace;
        this.yaw = (float) yaw;
    }

    CompassRoseDirection(CompassRoseDirection face1, CompassRoseDirection face2, BlockFace closestBlockFace, double yaw) {
        this.modX = face1.getModX() + face2.getModX();
        this.modZ = face1.getModZ() + face2.getModZ();
        this.closestBlockFace = closestBlockFace;
        this.yaw = (float) yaw;
    }

    /**
     * Get the amount of X-coordinates to modify to get the represented block
     *
     * @return Amount of X-coordinates to modify
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getModX() {
        return modX;
    }

    /**
     * Get the amount of Z-coordinates to modify to get the represented block
     *
     * @return Amount of Z-coordinates to modify
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getModZ() {
        return modZ;
    }

    /**
     * Gets the closest face for this direction. For example, NNE returns N.
     * Even directions like NE will return the direction to the left, N in this case.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockFace getClosestBlockFace() {
        return closestBlockFace;
    }

    /**
     * Gets the closes direction based on the given {@link cn.nukkit.entity.Entity} yaw.
     * @param yaw An entity yaw
     * @return The closest direction
     * @since 1.4.0.0-PN
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static CompassRoseDirection getClosestFromYaw(double yaw, @Nonnull Precision precision) {
        return BlockSignPost.GROUND_SIGN_DIRECTION.getValueForMeta(
                (int) Math.round(Math.round((yaw + 180.0) * precision.directions / 360.0) * (16.0 / precision.directions)) & 0x0f
        );
    }

    /**
     * Gets the closes direction based on the given {@link cn.nukkit.entity.Entity} yaw.
     * @param yaw An entity yaw
     * @return The closest direction
     * @since 1.4.0.0-PN
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static CompassRoseDirection getClosestFromYaw(double yaw) {
        return getClosestFromYaw(yaw, Precision.SECONDARY_INTER_CARDINAL);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
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

    /**
     * Gets the {@link cn.nukkit.entity.Entity} yaw that represents this direction.
     * @return The yaw value that can be used by entities to look at this direction.
     * @since 1.4.0.0-PN
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public float getYaw() {
        return yaw;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @RequiredArgsConstructor
    public enum Precision {
        /**
         * North, South, East, West.
         */
        @PowerNukkitOnly @Since("1.4.0.0-PN") CARDINAL(4),

        /**
         * N, E, S, W, NE, NW, SE, SW.
         */
        @PowerNukkitOnly @Since("1.4.0.0-PN") PRIMARY_INTER_CARDINAL(8),

        /**
         * N, E, S, W, NE, NW, SE, SW, WNW, NNW, NNE, ENE, ESE, SSE, SSW, WSW.
         */
        @PowerNukkitOnly @Since("1.4.0.0-PN") SECONDARY_INTER_CARDINAL(16);
        protected final int directions;
    }
}
