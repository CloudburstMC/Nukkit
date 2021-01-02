package cn.nukkit.utils;

import cn.nukkit.api.API;
import cn.nukkit.block.Block;
import cn.nukkit.math.BlockFace;

import java.util.Objects;

import static cn.nukkit.math.BlockFace.*;
import static cn.nukkit.utils.Rail.Orientation.State.*;

/**
 * INTERNAL helper class of railway
 * <p>
 * By lmlstarqaq http://snake1999.com/
 * Creation time: 2017/7/1 17:42.
 */
@API(usage = API.Usage.BLEEDING, definition = API.Definition.INTERNAL)
public final class Rail {

    public static boolean isRailBlock(Block block) {
        Objects.requireNonNull(block, "Rail block predicate can not accept null block");
        return isRailBlock(block.getId());
    }

    public enum Orientation {
        STRAIGHT_NORTH_SOUTH(STRAIGHT, NORTH, SOUTH, null),
        STRAIGHT_EAST_WEST(STRAIGHT, EAST, WEST, null),
        ASCENDING_EAST(ASCENDING, EAST, WEST, EAST),
        ASCENDING_WEST(ASCENDING, EAST, WEST, WEST),
        ASCENDING_NORTH(ASCENDING, NORTH, SOUTH, NORTH),
        ASCENDING_SOUTH(ASCENDING, NORTH, SOUTH, SOUTH),
        CURVED_SOUTH_EAST(CURVED, SOUTH, EAST, null),
        CURVED_SOUTH_WEST(CURVED, SOUTH, WEST, null),
        CURVED_NORTH_WEST(CURVED, NORTH, WEST, null),
        CURVED_NORTH_EAST(CURVED, NORTH, EAST, null);

        private static final Orientation[] META_LOOKUP = new Orientation[values().length];
        private final State state;
        private final BlockFace[] connectingDirections;
        private final BlockFace ascendingDirection;

        Orientation(State state, BlockFace from, BlockFace to, BlockFace ascendingDirection) {
            this.state = state;
            this.connectingDirections = new BlockFace[]{from, to};
            this.ascendingDirection = ascendingDirection;
        }

        public static Orientation byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public static Orientation straight(BlockFace face) {
            switch (face) {
                case NORTH:
                case SOUTH:
                    return STRAIGHT_NORTH_SOUTH;
                case EAST:
                case WEST:
                    return STRAIGHT_EAST_WEST;
            }
            return STRAIGHT_NORTH_SOUTH;
        }

        public static Orientation ascending(BlockFace face) {
            switch (face) {
                case NORTH:
                    return ASCENDING_NORTH;
                case SOUTH:
                    return ASCENDING_SOUTH;
                case EAST:
                    return ASCENDING_EAST;
                case WEST:
                    return ASCENDING_WEST;
            }
            return ASCENDING_EAST;
        }

        public static Orientation curved(BlockFace f1, BlockFace f2) {
            for (Orientation o : new Orientation[]{CURVED_SOUTH_EAST, CURVED_SOUTH_WEST, CURVED_NORTH_WEST, CURVED_NORTH_EAST}) {
                BlockFace found = null;
                for (BlockFace f0 : o.connectingDirections) {
                    if (found == f1 && f0 == f2 || found == f2 && f0 == f1)
                        return o;
                    if (f0 == f1 || f0 == f2) {
                        if (f1 == f2)
                            return o;
                        found = f0;
                    }
                }
            }
            return CURVED_SOUTH_EAST;
        }

        public static Orientation straightOrCurved(BlockFace f1, BlockFace f2) {
            for (Orientation o : new Orientation[]{STRAIGHT_NORTH_SOUTH, STRAIGHT_EAST_WEST, CURVED_SOUTH_EAST, CURVED_SOUTH_WEST, CURVED_NORTH_WEST, CURVED_NORTH_EAST}) {
                BlockFace found = null;
                for (BlockFace f0 : o.connectingDirections) {
                    if (found == f1 && f0 == f2 || found == f2 && f0 == f1)
                        return o;
                    if (f0 == f1 || f0 == f2) {
                        if (f1 == f2)
                            return o;
                        found = f0;
                    }
                }
            }
            return STRAIGHT_NORTH_SOUTH;
        }

        public int metadata() {
            return this.ordinal();
        }

        public boolean hasConnectingDirections(BlockFace... faces) {
            for1:
            for (BlockFace f1 : faces) {
                for (BlockFace f2 : connectingDirections) {
                    if (f1 == f2)
                        continue for1;
                }

                return false;
            }
            return true;
        }

        public BlockFace[] connectingDirections() {
            return connectingDirections;
        }

        public BlockFace ascendingDirection() {
            return ascendingDirection;
        }

        public enum State {
            STRAIGHT, ASCENDING, CURVED
        }

        public boolean isStraight() {
            return state == STRAIGHT;
        }

        public boolean isAscending() {
            return state == ASCENDING;
        }

        public boolean isCurved() {
            return state == CURVED;
        }

        static {
            for (Orientation o : values()) {
                META_LOOKUP[o.ordinal()] = o;
            }
        }
    }

    public static boolean isRailBlock(int blockId) {
        switch (blockId) {
            case Block.RAIL:
            case Block.POWERED_RAIL:
            case Block.ACTIVATOR_RAIL:
            case Block.DETECTOR_RAIL:
                return true;
            default:
                return false;
        }
    }

    private Rail() {
        throw new UnsupportedOperationException(); // Really no rail instance
    }
}
